package vyrek.phasoritenetworks.ui

import io.wispforest.owo.ui.core.Color
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import vyrek.phasoritenetworks.init.PhasoriteNetworksMenus
import vyrek.phasoritenetworks.networking.NetworkingChannels
import vyrek.phasoritenetworks.networking.NetworkingEndecs
import vyrek.phasoritenetworks.networking.NetworkingEndecsData
import vyrek.phasoritenetworks.networking.NetworkingPackets

class UIMenu(containerId: Int, data: NetworkingEndecsData.ComponentData) :
	AbstractContainerMenu(PhasoriteNetworksMenus.UI.get(), containerId) {
	companion object {
		fun getUIMenuProvider(id: Int, buf: RegistryFriendlyByteBuf): UIMenu {
			val data = buf.read(NetworkingEndecs.COMPONENT_ENDEC)

			return UIMenu(id, data)
		}
	}

	var id = data.id
	var pos: BlockPos = data.pos
	var name: String = data.name
	var defaultName = data.defaultName
	var priority: Int = data.priority
	var overrideMode: Boolean = data.overrideMode
	var limit: Int = data.limit
	var limitlessMode: Boolean = data.limitlessMode

	fun updateEntityData() {
		NetworkingChannels.CHANNEL.clientHandle().send(
			NetworkingPackets.UpdateComponentData(
				pos,
				name,
				priority,
				overrideMode,
				limit,
				limitlessMode
			)
		)
	}

	fun createNetwork(name: String, color: Color) {
		NetworkingChannels.CHANNEL.clientHandle().send(
			NetworkingPackets.CreateNetwork(pos, name, player().uuid, color)
		)
	}

	override fun quickMoveStack(player: Player, index: Int): ItemStack {
		return ItemStack.EMPTY
	}

	override fun stillValid(player: Player): Boolean {
		return true
	}
}