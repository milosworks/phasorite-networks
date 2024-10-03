package vyrek.phasoritenetworks.common.components

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.block.PhasoriteExporterBlock
import vyrek.phasoritenetworks.block.PhasoriteImporterBlock
import vyrek.phasoritenetworks.common.getDirectionByPos
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity

open class PhasoriteComponentBlock<T : PhasoriteComponentEntity>(props: Properties) :
	BaseEntityBlock(props), EntityBlock {
	open var registryEntity: BlockEntityType<T>? = null

	override fun codec(): MapCodec<out BaseEntityBlock> {
		throw UnsupportedOperationException()
	}

	override fun newBlockEntity(
		pos: BlockPos,
		state: BlockState
	): BlockEntity? {
		return throw UnsupportedOperationException()
	}

	override fun <T : BlockEntity?> getTicker(
		level: Level,
		state: BlockState,
		type: BlockEntityType<T?>
	): BlockEntityTicker<T?>? {
		return createTickerHelper(type, registryEntity) { world, pos, state, be ->
			be.tick(world, pos, state, be)
		}
	}

	override fun neighborChanged(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		neighborBlock: Block,
		neighborPos: BlockPos,
		movedByPiston: Boolean
	) {
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
		if (neighborBlock is PhasoriteExporterBlock || neighborBlock is PhasoriteImporterBlock || movedByPiston) return

		val entity = level.getBlockEntity(pos) as? PhasoriteExporterEntity ?: return
		val neighbor = level.getBlockEntity(neighborPos)
		val direction = getDirectionByPos(pos, neighborPos) ?: return
		if (neighbor == null || neighborBlock == Blocks.AIR) {
			entity.transferHandler.removeNode(direction)
			return
		}

		val storage = level.getCapability(
			Capabilities.EnergyStorage.BLOCK,
			neighborPos,
			neighbor.blockState,
			neighbor,
			direction
		)
		if (storage == null) {
			entity.transferHandler.removeNode(direction)
			return
		}

		entity.transferHandler.updateNodes(direction, neighbor, storage)
	}
}