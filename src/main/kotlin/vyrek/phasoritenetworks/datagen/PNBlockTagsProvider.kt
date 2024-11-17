package vyrek.phasoritenetworks.datagen

import appeng.api.ids.AETags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
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

		tag(AETags.GROWTH_ACCELERATABLE).add(
			PNBlocks.BUDDING_PHASORITE_BLOCK
		)
	}
}