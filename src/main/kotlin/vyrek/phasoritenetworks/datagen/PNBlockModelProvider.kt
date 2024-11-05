package vyrek.phasoritenetworks.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.init.PNBlocks

class PNBlockModelProvider(output: PackOutput, fileHelper: ExistingFileHelper) :
	BlockStateProvider(output, PhasoriteNetworks.ID, fileHelper) {

	override fun registerStatesAndModels() {
		blockWithConnector(
			PNBlocks.PHASORITE_EXPORTER, models().getExistingFile(
				ResourceLocation.fromNamespaceAndPath(
					PhasoriteNetworks.ID,
					"block/phasorite_exporter"
				)
			)
		)

		blockWithConnector(
			PNBlocks.PHASORITE_IMPORTER, models().getExistingFile(
				ResourceLocation.fromNamespaceAndPath(
					PhasoriteNetworks.ID,
					"block/phasorite_importer"
				)
			)
		)

		blockWithItem(PNBlocks.PHASORITE_BLOCK)
		blockWithItem(PNBlocks.BUDDING_PHASORITE_BLOCK)

		generateCluster(
			PNBlocks.SMALL_PHASORITE_BUD,
			"phasoritenetworks:small_phasorite_bud",
			PhasoriteNetworks.id("block/small_phasorite_bud")
		)
		generateCluster(
			PNBlocks.MEDIUM_PHASORITE_BUD,
			"phasoritenetworks:medium_phasorite_bud",
			PhasoriteNetworks.id("block/medium_phasorite_bud")
		)
		generateCluster(
			PNBlocks.LARGE_PHASORITE_BUD,
			"phasoritenetworks:large_phasorite_bud",
			PhasoriteNetworks.id("block/large_phasorite_bud")
		)
		generateCluster(
			PNBlocks.PHASORITE_CLUSTER,
			"phasoritenetworks:phasorite_cluster",
			PhasoriteNetworks.id("block/phasorite_cluster")
		)
		generateCluster(
			PNBlocks.CHARGED_PHASORITE_CLUSTER,
			"phasoritenetworks:charged_phasorite_cluster",
			PhasoriteNetworks.id("block/charged_phasorite_cluster")
		)
	}

	private fun generateCluster(block: Block, id: String, texture: ResourceLocation) {
		directionalBlock(
			block,
			models().cross(
				id,
				texture
			).renderType("cutout")
		)

		itemModels().withExistingParent(id, mcLoc("item/generated")).texture("layer0", texture)
	}

	private fun blockWithConnector(block: Block, model: ModelFile.ExistingModelFile) {
		val connector = models().getExistingFile(
			ResourceLocation.fromNamespaceAndPath(
				PhasoriteNetworks.ID,
				"block/phasorite_connector"
			)
		)

		getMultipartBuilder(block).part()
			.modelFile(
				model
			).addModel().end()
			.part().modelFile(connector).addModel()
			.condition(BlockStateProperties.NORTH, true).end()
			.part().modelFile(connector).rotationY(90).addModel()
			.condition(BlockStateProperties.EAST, true).end()
			.part().modelFile(connector).rotationY(180).addModel()
			.condition(BlockStateProperties.SOUTH, true).end()
			.part().modelFile(connector).rotationY(270).addModel()
			.condition(BlockStateProperties.WEST, true).end()
			.part().modelFile(connector).rotationX(270).addModel()
			.condition(BlockStateProperties.UP, true).end()
			.part().modelFile(connector).rotationX(90).addModel()
			.condition(BlockStateProperties.DOWN, true).end()

		simpleBlockItem(block, model)
	}

	private fun blockWithItem(block: Block) {
		simpleBlockWithItem(block, cubeAll(block))
	}
}