package vyrek.phasoritenetworks.init

import net.minecraft.world.item.BlockItem
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks

object PhasoriteNetworksItems {
	private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(PhasoriteNetworks.ID)

	val PHASORITE_IMPORTER: BlockItem by ITEMS.registerSimpleBlockItem("phasorite_importer") { PhasoriteNetworksBlocks.PHASORITE_IMPORTER }
	val PHASORITE_EXPORTER: BlockItem by ITEMS.registerSimpleBlockItem("phasorite_exporter") { PhasoriteNetworksBlocks.PHASORITE_EXPORTER }

	fun init(event: IEventBus) {
		ITEMS.register(event)
	}
}