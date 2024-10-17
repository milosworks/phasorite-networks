package vyrek.phasoritenetworks.init

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.block.PhasoriteExporterBlock
import vyrek.phasoritenetworks.block.PhasoriteImporterBlock

//: AutoRegistryContainer<Blocks>
object PhasoriteNetworksBlocks {
	private val BLOCKS: DeferredRegister<Block> = DeferredRegister.createBlocks(PhasoriteNetworks.ID)

	val PHASORITE_EXPORTER by
	BLOCKS.register("phasorite_exporter") { -> PhasoriteExporterBlock(BlockBehaviour.Properties.of()) }
	val PHASORITE_IMPORTER by
	BLOCKS.register("phasorite_importer") { -> PhasoriteImporterBlock(BlockBehaviour.Properties.of()) }
//	val PHASORITE_EXPORTER = PhasoriteExporterBlock(BlockBehaviour.Properties.of())
//	val PHASORITE_IMPORTER = PhasoriteImporterBlock(BlockBehaviour.Properties.of())

//	override fun createBlockItem(block: Block, identifier: String): BlockItem {
//		return BlockItem(block, Item.Properties())
//	}

	fun init(event: IEventBus) {
		BLOCKS.register(event)
	}
}