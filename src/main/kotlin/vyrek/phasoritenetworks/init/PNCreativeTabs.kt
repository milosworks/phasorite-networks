package vyrek.phasoritenetworks.init

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks

object PNCreativeTabs {
	private val CREATIVE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		PhasoriteNetworks.ID
	)

	val PHASORITE_NETWORKS_TAB by CREATIVE_TABS.register("phasorite_networks_tab") { ->
		CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.${PhasoriteNetworks.ID}.phasorite_networks"))
			.icon { ItemStack(PNItems.PHASORITE_CRYSTAL) }
			.displayItems { _, o ->
				// Blocks
				o.accept(PNBlocks.PHASORITE_EXPORTER)
				o.accept(PNBlocks.PHASORITE_IMPORTER)
				o.accept(PNBlocks.PHASORITE_BLOCK)
				o.accept(PNBlocks.BUDDING_PHASORITE_BLOCK)

				// Items
				o.accept(PNItems.CHARGED_PHASORITE_CRYSTAL)
				o.accept(PNItems.PHASORITE_CRYSTAL)
				o.accept(PNItems.PHASORITE_DUST)
				o.accept(PNItems.PHASORITE_LENS)
				o.accept(PNItems.PHASORITE_CORE)
				o.accept(PNItems.PHASORITE_SEED)
			}
			.build()
	}

	fun init(event: IEventBus) {
		CREATIVE_TABS.register(event)
	}
}
