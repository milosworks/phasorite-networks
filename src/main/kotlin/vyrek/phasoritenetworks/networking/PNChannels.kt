package vyrek.phasoritenetworks.networking

import io.wispforest.owo.network.ClientAccess
import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.ServerAccess
import net.minecraft.client.Minecraft
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.NetworksData
import vyrek.phasoritenetworks.ui.Tabs
import vyrek.phasoritenetworks.ui.UIMenu
import vyrek.phasoritenetworks.ui.UIScreen
import kotlin.reflect.KClass
import kotlin.uuid.Uuid

fun <R : Record> OwoNetChannel.registerServerbound(
	messageClass: KClass<R>,
	handler: (R, ServerAccess) -> Unit
) = this.registerServerbound(messageClass.java, handler)

fun <R : Record> OwoNetChannel.registerClientbound(
	messageClass: KClass<R>,
	handler: (R, ClientAccess) -> Unit
) = this.registerClientbound(messageClass.java, handler)

fun <R> OwoNetChannel.serverboundWithEntity(
	messageClass: KClass<R>,
	handler: (R, ServerAccess, PhasoriteComponentEntity) -> Unit
) where R : Record, R : PosRecord = this.registerServerbound(messageClass) { msg, access ->
	val entity = access.player.serverLevel().getBlockEntity(msg.pos) as? PhasoriteComponentEntity
		?: return@registerServerbound

	handler(msg, access, entity)
}

object PNChannels {
	val CHANNEL: OwoNetChannel = OwoNetChannel.create(PhasoriteNetworks.id("main"))

	fun init() {
		CHANNEL.addEndecs {
			it.register(PNEndecs.CLIENT_USER_ENDEC, PNEndecsData.ClientUserData::class.java)
			it.register(PNEndecs.CLIENT_NETWORK_ENDEC, PNEndecsData.ClientNetworkData::class.java)
			it.register(PNEndecs.EXTRA_NETWORK_ENDEC, PNEndecsData.ExtraNetworkData::class.java)
			it.register(Endecs.UUID, Uuid::class.java)
		}

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

					entity.handleNetworkConnection(network.id)

					CHANNEL.serverHandle(access.player).send(PNPackets.UpdateComponentScreenData(
						entity.blockPos,
						network.toClientData(true),
						NetworksData.get().networks
							.filter { it.value.discoverable(access.player.uuid) }
							.map { it.value.toClientData() }
							.toList(),
					))
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

					CHANNEL.serverHandle(access.player).send(PNPackets.UpdateComponentScreenData(
						entity.blockPos,
						network.toClientData(true),
						NetworksData.get().networks
							.filter { it.value.discoverable(access.player.uuid) }
							.map { it.value.toClientData() }
							.toList(),
					))

					access.player.level().sendBlockUpdated(entity.blockPos, entity.blockState, entity.blockState, 0)
				}
			}
		}

		CHANNEL.serverboundWithEntity(PNPackets.ConnectNetwork::class) { msg, access, entity ->
			val network = NetworksData.get().getNetwork(msg.networkId) ?: return@serverboundWithEntity
			if (!network.discoverable(msg.userId) && network.password != msg.password) return@serverboundWithEntity

			entity.handleNetworkConnection(network.id)

			CHANNEL.serverHandle(access.player).send(PNPackets.UpdateComponentScreenData(
				entity.blockPos,
				network.toClientData(true),
				NetworksData.get().networks
					.filter { it.value.discoverable(access.player.uuid) }
					.map { it.value.toClientData() }
					.toList(),
			))
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
			}
		}
	}

	fun clinit() {
		CHANNEL.registerClientbound(PNPackets.UpdateComponentScreenData::class) { msg, access ->
			val menu = access.player().containerMenu as? UIMenu ?: return@registerClientbound
			val screen = Minecraft.getInstance().screen as? UIScreen ?: return@registerClientbound

			menu.network = msg.network
			menu.accessibleNetworks = msg.accessibleNetworks

			if (screen.activeTab == Tabs.NETWORK) screen.updateNetworkTab()
		}

		CHANNEL.registerClientbound(PNPackets.CommandPacket::class) { msg, access ->
			val menu = access.player().containerMenu as? UIMenu ?: return@registerClientbound
			val screen = Minecraft.getInstance().screen as? UIScreen ?: return@registerClientbound

			when (msg.action) {
				ActionType.DISCONNECT_NETWORK -> {
					menu.network = null
					screen.updateNetworkTab()
				}

				ActionType.DELETE_NETWORK -> {
					menu.accessibleNetworks = menu.accessibleNetworks.filter { it.id != menu.network?.id }
					menu.network = null
					screen.updateNetworkTab()
				}
			}
		}
	}
}