package vyrek.phasoritenetworks.init

import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.ui.UIMenu

object PNMenus {
	private val MENUS = DeferredRegister.create(BuiltInRegistries.MENU, PhasoriteNetworks.ID)

	val UI = MENUS.register("ui") { ->
		IMenuTypeExtension.create { containerId, inventory, buf ->
			UIMenu.getUIMenuProvider(containerId, buf, inventory.player)
		}
	}

	fun init(event: IEventBus) {
		MENUS.register(event)
	}
}