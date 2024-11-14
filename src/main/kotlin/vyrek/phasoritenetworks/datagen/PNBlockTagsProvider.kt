package vyrek.phasoritenetworks.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.init.PNBlocks
import java.util.concurrent.CompletableFuture

class PNBlockTagsProvider(
	output: PackOutput,
	provider: CompletableFuture<HolderLookup.Provider>,
	fileHelper: ExistingFileHelper
) : BlockTagsProvider(output, provider, PhasoriteNetworks.ID, fileHelper) {
	override fun addTags(provider: HolderLookup.Provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
			PNBlocks.PHASORITE_EXPORTER,
			PNBlocks.PHASORITE_IMPORTER,
			PNBlocks.PHASORITE_BLOCK,
			PNBlocks.BUDDING_PHASORITE_BLOCK,
			PNBlocks.SMALL_PHASORITE_BUD,
			PNBlocks.MEDIUM_PHASORITE_BUD,
			PNBlocks.LARGE_PHASORITE_BUD,
			PNBlocks.PHASORITE_CLUSTER,
			PNBlocks.CHARGED_PHASORITE_CLUSTER
		)

		tag(BlockTags.NEEDS_IRON_TOOL).add(
			PNBlocks.PHASORITE_EXPORTER,
			PNBlocks.PHASORITE_IMPORTER,
			PNBlocks.PHASORITE_BLOCK,
			PNBlocks.BUDDING_PHASORITE_BLOCK,
			PNBlocks.SMALL_PHASORITE_BUD,
			PNBlocks.MEDIUM_PHASORITE_BUD,
			PNBlocks.LARGE_PHASORITE_BUD,
			PNBlocks.PHASORITE_CLUSTER,
			PNBlocks.CHARGED_PHASORITE_CLUSTER
		)

		tag(Tags.Blocks.BUDDING_BLOCKS).add(
			PNBlocks.BUDDING_PHASORITE_BLOCK
		)

		tag(blockTag("ae2:growth_acceleratable")).add(
			PNBlocks.BUDDING_PHASORITE_BLOCK
		)
	}

	fun blockTag(tagName: String): TagKey<Block> {
		return TagKey.create(Registries.BLOCK, ResourceLocation.parse(tagName))
	}
}