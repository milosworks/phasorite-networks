package xyz.milosworks.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED
import net.minecraft.world.level.material.Fluids
import xyz.milosworks.phasoritenetworks.init.PNBlocks

class BuddingPhasoriteBlock(props: Properties) : PhasoriteBlock(props) {
	companion object {
		const val GROWTH_CHANCE = 6
		val SIDES = Direction.entries.toTypedArray()
	}

	override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
		if (random.nextInt(GROWTH_CHANCE) != 0) return

		val dir = SIDES[random.nextInt(SIDES.size)]
		val blockPos = pos.relative(dir)
		var blockState = level.getBlockState(blockPos)

		val block: Block = when {
			canClusterGrow(blockState) -> PNBlocks.SMALL_PHASORITE_BUD
			blockState.block == PNBlocks.SMALL_PHASORITE_BUD && blockState.getValue(FACING) == dir -> PNBlocks.MEDIUM_PHASORITE_BUD
			blockState.block == PNBlocks.MEDIUM_PHASORITE_BUD && blockState.getValue(FACING) == dir -> PNBlocks.LARGE_PHASORITE_BUD
			blockState.block == PNBlocks.LARGE_PHASORITE_BUD && blockState.getValue(FACING) == dir -> PNBlocks.PHASORITE_CLUSTER
			blockState.block == PNBlocks.PHASORITE_CLUSTER && blockState.getValue(FACING) == dir && (level.canSeeSky(
				blockPos
			) && level.isDay) -> PNBlocks.CHARGED_PHASORITE_CLUSTER

			else -> null
		} ?: return

		blockState =
			block.defaultBlockState().setValue(FACING, dir)
				.setValue(WATERLOGGED, blockState.fluidState.type == Fluids.WATER)
		level.setBlockAndUpdate(blockPos, blockState)
	}

	private fun canClusterGrow(state: BlockState): Boolean {
		return state.isAir || state.block == Blocks.WATER && state.fluidState.amount == 8
	}
}