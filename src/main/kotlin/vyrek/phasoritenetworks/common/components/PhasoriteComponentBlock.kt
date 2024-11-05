package vyrek.phasoritenetworks.common.components

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.block.PhasoriteExporterBlock
import vyrek.phasoritenetworks.block.PhasoriteImporterBlock
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.NetworksData
import vyrek.phasoritenetworks.networking.PNEndecs
import vyrek.phasoritenetworks.networking.PNEndecsData
import vyrek.phasoritenetworks.ui.UIMenu
import kotlin.uuid.Uuid

open class PhasoriteComponentBlock<T : PhasoriteComponentEntity>(props: Properties) :
	Block(props), EntityBlock {
	protected open var registryEntity: BlockEntityType<T>? = null

	private val shape = makeShape()

	init {
		registerDefaultState(
			getStateDefinition().any()
				.setValue(DOWN, false)
				.setValue(UP, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(EAST, false)
		)
	}

	override fun codec(): MapCodec<out BaseEntityBlock> {
		throw UnsupportedOperationException()
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		super.createBlockStateDefinition(builder)
		builder.add(*SIDES)
	}

	override fun setPlacedBy(
		level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack
	) {
		if (placer == null || level.isClientSide) return

		val entity = level.getBlockEntity(pos) as? PhasoriteComponentEntity ?: return

		entity.ownerUuid = placer.uuid

		entity.setChanged()
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (level.isClientSide || player !is ServerPlayer) return InteractionResult.SUCCESS_NO_ITEM_USED

		val entity =
			level.getBlockEntity(pos) as? PhasoriteComponentEntity ?: return InteractionResult.SUCCESS_NO_ITEM_USED

		val id = when (entity.componentType) {
			ComponentType.IMPORTER -> "phasoritenetworks:phasorite_importer"
			ComponentType.EXPORTER -> "phasoritenetworks:phasorite_exporter"
			else -> return InteractionResult.SUCCESS_NO_ITEM_USED
		}

		val data = PNEndecsData.ComponentScreenData(
			pos,
			id,
			entity.name,
			entity.defaultName,
			entity.rawLimit,
			entity.limitlessMode,
			entity.priority,
			entity.overrideMode,
			if (entity.network.isValid) entity.network.toClientData(true) else null,
			NetworksData.get().networks
				.filter { it.value.discoverable(player.uuid) }
				.map { it.value.toClientData() }
				.toList(),
		)

		player.openMenu(
			SimpleMenuProvider({ containerId, _, player -> UIMenu(containerId, data, player) }, Component.literal(""))
		) { buf ->
			buf.write(PNEndecs.COMPONENT_SCREEN_ENDEC, data)
		}

		return InteractionResult.SUCCESS_NO_ITEM_USED
	}

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (state.block != newState.block) {
			(level.getBlockEntity(pos) as? PhasoriteComponentEntity)?.let { entity ->
				entity.transferHandler.run {
					nodes.clear()
					buffer = 0
				}

				entity.handleNetworkConnection(Uuid.NIL)
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun newBlockEntity(
		pos: BlockPos,
		state: BlockState
	): BlockEntity? {
		throw UnsupportedOperationException()
	}

	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		type: BlockEntityType<T>
	): BlockEntityTicker<T>? {
		return createTickerHelper(type, registryEntity!!) { world, pos, state, be ->
			be.tick(world, pos, state, be)
		}
	}

	override fun neighborChanged(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		oldNeighborBlock: Block,
		neighborPos: BlockPos,
		movedByPiston: Boolean
	) {
		val entity = level.getBlockEntity(pos) as? PhasoriteComponentEntity ?: return

		//b.subtract(a) is the direction from A to B
		val offset = neighborPos.subtract(pos)
		val directionToNeighbor = Direction.fromDelta(offset.x, offset.y, offset.z)!!
		val neighborState = level.getBlockState(neighborPos)
		if (neighborState.isAir ||
			neighborState.block is PhasoriteExporterBlock ||
			neighborState.block is PhasoriteImporterBlock
		) {
			if (state.getValue(SIDES[directionToNeighbor.get3DDataValue()])) level.setBlock(
				pos,
				state.setValue(SIDES[directionToNeighbor.get3DDataValue()], false),
				UPDATE_CLIENTS
			)

			entity.transferHandler.removeNode(directionToNeighbor)
			return
		}

		val neighbor = level.getBlockEntity(neighborPos) ?: run {
			if (state.getValue(SIDES[directionToNeighbor.get3DDataValue()])) level.setBlock(
				pos,
				state.setValue(SIDES[directionToNeighbor.get3DDataValue()], false),
				UPDATE_CLIENTS
			)
			entity.transferHandler.removeNode(directionToNeighbor)
			return
		}
		val storage = level.getCapability(
			Capabilities.EnergyStorage.BLOCK,
			neighborPos,
			neighborState,
			neighbor,
			directionToNeighbor.opposite
		) ?: run {
			if (state.getValue(SIDES[directionToNeighbor.get3DDataValue()])) level.setBlock(
				pos,
				state.setValue(SIDES[directionToNeighbor.get3DDataValue()], false),
				UPDATE_CLIENTS
			)
			entity.transferHandler.removeNode(directionToNeighbor)
			return
		}


		entity.transferHandler.updateNodes(directionToNeighbor, neighbor, storage)
		level.setBlock(
			pos,
			state.setValue(SIDES[directionToNeighbor.get3DDataValue()], true),
			UPDATE_CLIENTS
		)
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
		return shape
	}

	open fun makeShape(): VoxelShape {
		return Shapes.block()
	}

	companion object {
		val SIDES: Array<BooleanProperty> = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST)

		@Suppress("UNCHECKED_CAST")
		fun <E : BlockEntity, A : BlockEntity> createTickerHelper(
			serverType: BlockEntityType<A>,
			clientType: BlockEntityType<E>,
			ticker: BlockEntityTicker<in E>?
		): BlockEntityTicker<A>? {
			return if (clientType == serverType) ticker as? BlockEntityTicker<A> else null
		}
	}
}