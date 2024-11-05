package vyrek.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

open class PhasoriteClusterBlock(height: Double, offset: Double, props: Properties) : PhasoriteBlock(props),
	SimpleWaterloggedBlock {
	private val upAabb = box(offset, 0.0, offset, (16 - offset), height, (16 - offset))
	private val downAabb = box(offset, (16 - height), offset, (16 - offset), 16.0, (16 - offset));
	private val northAabb = box(offset, offset, (16 - height), (16 - offset), (16 - offset), 16.0);
	private val southAabb = box(offset, offset, 0.0, (16 - offset), (16 - offset), height);
	private val eastAabb = box(0.0, offset, offset, height, (16 - offset), (16 - offset));
	private val westAabb = box((16 - height), offset, offset, 16.0, (16 - offset), (16 - offset));

	init {
		registerDefaultState(
			getStateDefinition().any()
				.setValue(FACING, Direction.UP)
				.setValue(WATERLOGGED, false)
		)
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
		return when (state.getValue(FACING)) {
			Direction.NORTH -> northAabb
			Direction.SOUTH -> southAabb
			Direction.EAST -> eastAabb
			Direction.WEST -> westAabb
			Direction.DOWN -> downAabb
			Direction.UP -> upAabb
		}
	}

	override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
		val dir = state.getValue(FACING)
		val wallPos = pos.relative(dir.opposite)

		return level.getBlockState(wallPos).isFaceSturdy(level, wallPos, dir)
	}

	override fun updateShape(
		state: BlockState,
		direction: Direction,
		neighborState: BlockState,
		level: LevelAccessor,
		pos: BlockPos,
		neighborPos: BlockPos
	): BlockState {
		if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))

		return if (direction == state.getValue(FACING).opposite && !state.canSurvive(
				level,
				pos
			)
		) Blocks.AIR.defaultBlockState() else super.updateShape(
			state,
			direction,
			neighborState,
			level,
			pos,
			neighborPos
		)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
		val level = context.level
		val pos = context.clickedPos

		return defaultBlockState().setValue(WATERLOGGED, level.getFluidState(pos).type == Fluids.WATER)
			.setValue(FACING, context.clickedFace)
	}

	override fun rotate(state: BlockState, level: LevelAccessor, pos: BlockPos, direction: Rotation): BlockState {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)))
	}

	override fun mirror(state: BlockState, mirror: Mirror): BlockState {
		return state.rotate(mirror.getRotation(state.getValue(AmethystClusterBlock.FACING)))
	}

	override fun getFluidState(state: BlockState): FluidState {
		return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
		builder.add(WATERLOGGED, FACING)
	}
}