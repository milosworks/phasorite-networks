package xyz.milosworks.phasoritenetworks.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks
import xyz.milosworks.phasoritenetworks.init.PNBlocks

class PhasoriteSeedItem(props: Properties) : Item(props) {
	override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
		val state = context.level.getBlockState(context.clickedPos)
		if (state.block != Blocks.BUDDING_AMETHYST) return InteractionResult.PASS

		context.itemInHand.shrink(1)
		context.level.setBlockAndUpdate(context.clickedPos, PNBlocks.BUDDING_PHASORITE_BLOCK.defaultBlockState())
		return InteractionResult.sidedSuccess(context.level.isClientSide)
	}
}