package xyz.milosworks.phasoritenetworks.client.ui

import io.wispforest.owo.ui.core.Color
import net.minecraft.core.GlobalPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentEntity
import xyz.milosworks.phasoritenetworks.common.networks.NetworkUserAccess
import xyz.milosworks.phasoritenetworks.init.PNMenus
import xyz.milosworks.phasoritenetworks.networking.*
import java.util.*
import kotlin.uuid.Uuid

class UIMenu(containerId: Int, data: PNEndecsData.ComponentScreenData, val player: Player) :
	AbstractContainerMenu(PNMenus.UI.get(), containerId) {
	companion object {
		fun getUIMenuProvider(id: Int, buf: RegistryFriendlyByteBuf, player: Player): UIMenu {
			val data = buf.read(PNEndecs.COMPONENT_SCREEN_ENDEC)

			return UIMenu(id, data, player)
		}
	}

	val blockId = data.blockId
	val pos = data.pos
	var name = data.name
	var defaultName = data.defaultName
	var limit = data.limit
	var limitlessMode = data.limitlessMode
	var priority = data.priority
	var overrideMode = data.overrideMode

	var accessibleNetworks = data.accessibleNetworks
	var network: PNEndecsData.ClientNetworkData? = data.network

	val clientEntity = player.level().getBlockEntity(data.pos) as PhasoriteComponentEntity
	val throughput get() = clientEntity.transferHandler.throughput

	fun updateComponentData() {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.UpdateComponentData(
				pos,
				name,
				priority,
				overrideMode,
				limit,
				limitlessMode
			)
		)
	}

	fun putNetwork(
		type: PutType,
		name: String,
		color: Color,
		private: Boolean,
		password: String,
		ply: UUID = player.uuid,
		id: Uuid = Uuid.NIL
	) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.PutNetwork(
				pos,
				type,
				name,
				ply,
				color.argb(),
				private,
				if (password == "") "" else password,
				id
			)
		)
	}

	fun deleteNetwork() {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.CommandPacket(
				pos,
				ActionType.DELETE_NETWORK
			)
		)
	}

	fun disconnectNetwork() {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.CommandPacket(
				pos,
				ActionType.DISCONNECT_NETWORK
			)
		)
	}

	fun connectNetwork(id: Uuid, password: String) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.ConnectNetwork(
				pos,
				id,
				player().uuid,
				password
			)
		)
	}

	fun disconnectComponents(positions: List<GlobalPos>) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.DisconnectComponents(
				pos,
				positions
			)
		)
	}

	fun kickFromNetwork(id: UUID) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.ManagePlayer(
				pos,
				id,
				ManageType.KICK,
				// TODO: Fix owo so it allows nullable fields in packets so i can put null here
				NetworkUserAccess.MEMBER
			)
		)
	}

	fun passOwnership(id: UUID) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.ManagePlayer(
				pos,
				id,
				ManageType.PASS_OWNERSHIP,
				// TODO: Fix owo so it allows nullable fields in packets so i can put null here
				NetworkUserAccess.MEMBER
			)
		)
	}

	fun setAccess(id: UUID, access: NetworkUserAccess) {
		PNChannels.CHANNEL.clientHandle().send(
			PNPackets.ManagePlayer(
				pos,
				id,
				ManageType.SET_ACCESS,
				access
			)
		)
	}

	override fun removed(player: Player) {
		if (player is ServerPlayer) {
			val ent = player.level().getBlockEntity(pos) as? PhasoriteComponentEntity ?: return
			ent.isGuiOpen = false
			ent.setChanged()
		}

		super.removed(player)
	}

	override fun quickMoveStack(player: Player, index: Int): ItemStack {
		return ItemStack.EMPTY
	}

	override fun stillValid(player: Player): Boolean {
		return true
	}
}