package vyrek.phasoritenetworks.ui

import io.wispforest.owo.ui.core.Color
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import vyrek.phasoritenetworks.init.PNMenus
import vyrek.phasoritenetworks.networking.*
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

	override fun quickMoveStack(player: Player, index: Int): ItemStack {
		return ItemStack.EMPTY
	}

	override fun stillValid(player: Player): Boolean {
		return true
	}
}