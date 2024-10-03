package vyrek.phasoritenetworks.init

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks

//: AutoRegistryContainer<CreativeModeTab>
object PhasoriteNetworksCreativeTabs {
//	val PHASORITE_NETWORKS_TAB_KEY = PhasoriteNetworks.identifier("phasorite_networks")
//	val PHASORITE_NETWORKS_TAB = OwoItemGroup.builder(PHASORITE_NETWORKS_TAB_KEY) {
//		Icon.of(PhasoriteNetworksBlocks.PHASORITE_EXPORTER)
//	}
//		.build()

	//	fun init() {
//		PHASORITE_NETWORKS_TAB.initialize()
//	}
//	val PHASORITE_NETWORKS_TAB_KEY = ResourceKey.create(
//		BuiltInRegistries.CREATIVE_MODE_TAB.key(),
//		PhasoriteNetworks.identifier("phasorite_networks")
//	)
//	val PHASORITE_NETWORKS_TAB = FabricItemGroup.builder()
//		.title(Component.translatable("itemGroup.${PhasoriteNetworks.ID}.phasorite_networks"))
//		.icon { -> ItemStack(PhasoriteNetworksBlocks.PHASORITE_IMPORTER) }
//		.build()
//
//	fun init() {
//		Registry.register(
//			BuiltInRegistries.CREATIVE_MODE_TAB,
//			PhasoriteNetworks.identifier("phasorite_networks"),
//			PHASORITE_NETWORKS_TAB
//		)
//		ItemGroupEvents.modifyEntriesEvent(PHASORITE_NETWORKS_TAB_KEY).register { g ->
////			g.accept(PhasoriteNetworksItems.LINK_WRENCH)
//			g.accept(PhasoriteNetworksBlocks.PHASORITE_IMPORTER)
//			g.accept(PhasoriteNetworksBlocks.PHASORITE_EXPORTER)
//		}
//	}

	val CREATIVE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		PhasoriteNetworks.ID
	)

	val PHASORITE_NETWORKS_TAB by CREATIVE_TABS.register("phasorite_networks_tab") { ->
		CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.${PhasoriteNetworks.ID}.phasorite_networks"))
			.icon { -> ItemStack(PhasoriteNetworksBlocks.PHASORITE_EXPORTER) }
			.displayItems { _, o ->
				o.accept(PhasoriteNetworksBlocks.PHASORITE_IMPORTER)
				o.accept(PhasoriteNetworksBlocks.PHASORITE_EXPORTER)
			}
			.build()
	}

	fun init(event: IEventBus) {
		CREATIVE_TABS.register(event)
	}

//	override fun getRegistry(): Registry<CreativeModeTab> {
//		return BuiltInRegistries.CREATIVE_MODE_TAB
//	}
//
//	@Suppress("UNCHECKED_CAST")
//	override fun getTargetFieldType(): Class<CreativeModeTab> {
//		return CreativeModeTab::class.java as Class<CreativeModeTab>
//	}
}
