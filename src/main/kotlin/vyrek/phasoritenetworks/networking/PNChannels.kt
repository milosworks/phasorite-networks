package vyrek.phasoritenetworks.networking

import io.wispforest.endec.Endec
import io.wispforest.endec.impl.ReflectiveEndecBuilder
import io.wispforest.owo.network.ClientAccess
import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.ServerAccess
import net.minecraft.client.Minecraft
import net.minecraft.core.GlobalPos
import net.minecraft.world.entity.player.Player
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.client.ui.Tabs
import vyrek.phasoritenetworks.client.ui.UIMenu
import vyrek.phasoritenetworks.client.ui.UIScreen
import vyrek.phasoritenetworks.client.ui.tabs.ComponentsTab
import vyrek.phasoritenetworks.client.ui.tabs.MembersTab
import vyrek.phasoritenetworks.client.ui.tabs.NetworkTab
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.Network
import vyrek.phasoritenetworks.common.networks.NetworkUser
import vyrek.phasoritenetworks.common.networks.NetworkUserAccess
import vyrek.phasoritenetworks.common.networks.NetworksData
import kotlin.reflect.KClass
import kotlin.uuid.Uuid

fun <R : Record> OwoNetChannel.registerServerbound(
	messageClass: KClass<R>,
	handler: (R, ServerAccess) -> Unit
) = this.registerServerbound(messageClass.java, handler)

fun <R : Record> OwoNetChannel.registerClientboundDeferred(kClass: KClass<R>) =
	this.registerClientboundDeferred(kClass.java)

fun <R : Record> OwoNetChannel.registerClientbound(
	messageClass: KClass<R>,
	handler: (R, ClientAccess) -> Unit
) = this.registerClientbound(messageClass.java, handler)

fun <T : Any> ReflectiveEndecBuilder.register(endec: Endec<T>, kClass: KClass<T>): ReflectiveEndecBuilder =
	this.register(endec, kClass.java)

fun <R> OwoNetChannel.serverboundWithEntity(
	messageClass: KClass<R>,
	handler: (R, ServerAccess, PhasoriteComponentEntity) -> Unit
) where R : Record, R : PosRecord = this.registerServerbound(messageClass) { msg, access ->
	val entity = access.player.serverLevel().getBlockEntity(msg.pos) as? PhasoriteComponentEntity
		?: return@registerServerbound

	handler(msg, access, entity)
}

object PNChannels {
	val CHANNEL: OwoNetChannel = OwoNetChannel.create(PhasoriteNetworks.id("main")).apply {
		addEndecs {
			it.register(PNEndecs.RAW_COMPONENT_ENDEC, PNEndecsData.RawComponentData::class)
			it.register(PNEndecs.CLIENT_USER_ENDEC, PNEndecsData.ClientUserData::class)
			it.register(PNEndecs.CLIENT_NETWORK_ENDEC, PNEndecsData.ClientNetworkData::class)
			it.register(PNEndecs.EXTRA_NETWORK_ENDEC, PNEndecsData.ExtraNetworkData::class)
			it.register(Endecs.GLOBAL_POS, GlobalPos::class)
			it.register(Endecs.UUID, Uuid::class)
		}
	}

	fun init() {
		CHANNEL.serverboundWithEntity(PNPackets.UpdateComponentData::class) { msg, access, entity ->
			if (msg.name != entity.name) entity.name = msg.name
			if (msg.priority != entity.priority) entity.priority = msg.priority
			if (msg.overrideMode != entity.overrideMode) entity.overrideMode = msg.overrideMode
			if (msg.limit != entity.rawLimit) entity.rawLimit = msg.limit
			if (msg.limitlessMode != entity.limitlessMode) entity.limitlessMode = msg.limitlessMode

			entity.setChanged()
		}

		CHANNEL.serverboundWithEntity(PNPackets.PutNetwork::class) { msg, access, entity ->
			when (msg.type) {
				PutType.CREATE -> {
					val network = NetworksData.get()
						.createNetwork(msg.name, msg.owner, msg.color, msg.private, msg.password)

					updateScreenData(entity, access.player, network)
				}

				PutType.UPDATE -> {
					if (msg.id == Uuid.NIL) throw IllegalAccessException("Message should contain an ID")
					val network = NetworksData.get().modifyNetwork(msg.id) {
						name = msg.name
						owner = msg.owner
						color = msg.color
						private = msg.private
						password = msg.password
					} ?: throw IllegalAccessException("Network doesnt exist")

					updateScreenData(entity, access.player, network, false)

					access.player.level().sendBlockUpdated(entity.blockPos, entity.blockState, entity.blockState, 0)
				}
			}
		}

		CHANNEL.serverboundWithEntity(PNPackets.ConnectNetwork::class) { msg, access, entity ->
			val network = NetworksData.get().getNetwork(msg.networkId) ?: return@serverboundWithEntity
			if (!network.discoverable(msg.userId) && network.password != msg.password) return@serverboundWithEntity

			updateScreenData(entity, access.player, network)
		}

		CHANNEL.serverboundWithEntity(PNPackets.DisconnectComponents::class) { msg, access, entity ->
			for (globalPos in msg.positions) {
				val level = access.player.level().takeIf { globalPos.dimension == it.dimension() }
					?: access.player.server.getLevel(globalPos.dimension)!!

				val componentEntity =
					level.getBlockEntity(globalPos.pos) as? PhasoriteComponentEntity ?: continue

				componentEntity.handleNetworkConnection(Uuid.NIL)
			}

			CHANNEL.serverHandle(access.player).send(
				PNPackets.UpdateComponentScreenData(
					msg.pos,
					entity.network.toClientData(true).let { data ->
						data.copy(components = data.components.filterNot { msg.positions.contains(it.globalPos) || it.globalPos == entity.globalPos })
					},
					NetworksData.get().networks
						.filter { it.value.discoverable(access.player.uuid) }
						.map { it.value.toClientData() }
						.toList(),
				))
		}

		CHANNEL.serverboundWithEntity(PNPackets.ManagePlayer::class) { msg, access, entity ->
			val network = entity.network

			when (msg.type) {
				ManageType.KICK -> {
					for (component in network.components.filter { it.ownerUuid == msg.uuid }) {
						component.handleNetworkConnection(Uuid.NIL)
					}

					CHANNEL.serverHandle(access.player).send(
						PNPackets.UpdateComponentScreenData(
							entity.blockPos,
							network.toClientData(true).let { data ->
								data.copy(
									components = data.components.filterNot { it.globalPos == entity.globalPos || it.owner == msg.uuid },
									members = data.members.filterNot { it.key == msg.uuid })
							},
							NetworksData.get().networks
								.filter { it.value.discoverable(access.player.uuid) }
								.map { it.value.toClientData() }
								.toList(),
						))
				}

				ManageType.PASS_OWNERSHIP -> {
					val player = access.player.server.playerList.getPlayer(msg.uuid)!!

					network.owner = msg.uuid
					network.members[msg.uuid] = NetworkUser(msg.uuid, player.gameProfile.name, NetworkUserAccess.ADMIN)

					updateScreenData(entity, access.player, network, false)
				}

				ManageType.SET_ACCESS -> {
					val member = network.members[msg.uuid] ?: return@serverboundWithEntity

					member.access = msg.access

					updateScreenData(entity, access.player, network, false)
				}
			}
		}

		CHANNEL.serverboundWithEntity(PNPackets.CommandPacket::class) { msg, access, entity ->
			when (msg.action) {
				ActionType.DISCONNECT_NETWORK -> {
					entity.handleNetworkConnection(Uuid.NIL)

					CHANNEL.serverHandle(access.player).send(
						PNPackets.CommandPacket(
							msg.pos,
							ActionType.DISCONNECT_NETWORK
						)
					)
				}

				ActionType.DELETE_NETWORK -> {
					NetworksData.get().deleteNetwork(entity.networkId)

					CHANNEL.serverHandle(access.player).send(
						PNPackets.CommandPacket(
							msg.pos,
							ActionType.DELETE_NETWORK
						)
					)
				}

				ActionType.CLOSE_MENU -> {
					entity.isGuiOpen = false
				}
			}
		}

		// Client deferred packets
		CHANNEL.registerClientboundDeferred(PNPackets.UpdateComponentScreenData::class)
		CHANNEL.registerClientboundDeferred(PNPackets.CommandPacket::class)
	}

	private fun updateScreenData(
		entity: PhasoriteComponentEntity,
		player: Player,
		network: Network,
		handleConn: Boolean = true
	) {
		if (handleConn) entity.handleNetworkConnection(network.id)

		CHANNEL.serverHandle(player).send(
			PNPackets.UpdateComponentScreenData(
				entity.blockPos,
				network.toClientData(true).let { data ->
					data.copy(components = data.components.filterNot { it.globalPos == entity.globalPos })
				},
				NetworksData.get().networks
					.filter { it.value.discoverable(player.uuid) }
					.map { it.value.toClientData() }
					.toList(),
			))
	}

	fun clinit() {
		CHANNEL.registerClientbound(PNPackets.UpdateComponentScreenData::class) { msg, access ->
			val menu = access.player().containerMenu as? UIMenu ?: return@registerClientbound
			val screen = Minecraft.getInstance().screen as? UIScreen ?: return@registerClientbound

			menu.network = msg.network
			menu.accessibleNetworks = msg.accessibleNetworks

			when (screen.activeTab) {
				Tabs.NETWORK -> screen.buildTab(screen.rootComponent, screen.activeTab, ::NetworkTab)
				Tabs.COMPONENTS -> screen.buildTab(screen.rootComponent, screen.activeTab, ::ComponentsTab)
				Tabs.MEMBERS -> screen.buildTab(screen.rootComponent, screen.activeTab, ::MembersTab)
				else -> {}
			}
		}

		CHANNEL.registerClientbound(PNPackets.CommandPacket::class) { msg, access ->
			val menu = access.player().containerMenu as? UIMenu ?: return@registerClientbound
			val screen = Minecraft.getInstance().screen as? UIScreen ?: return@registerClientbound

			when (msg.action) {
				ActionType.DISCONNECT_NETWORK -> {
					menu.network = null
					screen.buildTab(screen.rootComponent, Tabs.NETWORK, ::NetworkTab)
				}

				ActionType.DELETE_NETWORK -> {
					menu.accessibleNetworks = menu.accessibleNetworks.filter { it.id != menu.network?.id }
					menu.network = null
					screen.buildTab(screen.rootComponent, Tabs.NETWORK, ::NetworkTab)
				}

				else -> {}
			}
		}
	}
}

