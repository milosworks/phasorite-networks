package vyrek.phasoritenetworks.init

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.item.PhasoriteSeedItem

object PNItems {
	val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(PhasoriteNetworks.ID)

	val PHASORITE_CORE by item("phasorite_core")
	val PHASORITE_LENS by item("phasorite_lens")

	val CHARGED_PHASORITE_CRYSTAL by item("charged_phasorite_crystal")
	val PHASORITE_CRYSTAL by item("phasorite_crystal")
	val PHASORITE_DUST by item("phasorite_dust")

	val PHASORITE_SEED by item("phasorite_seed", ::PhasoriteSeedItem)

	fun item(id: String, supplier: (Item.Properties) -> Item = { props -> Item(props) }): DeferredItem<Item> {
		return ITEMS.registerItem(id) { supplier(Item.Properties()) }
	}

	fun init(event: IEventBus) {
		ITEMS.register(event)
	}
}