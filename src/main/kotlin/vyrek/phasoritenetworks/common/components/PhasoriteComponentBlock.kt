package vyrek.phasoritenetworks.common.components

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.block.PhasoriteExporterBlock
import vyrek.phasoritenetworks.block.PhasoriteImporterBlock
import vyrek.phasoritenetworks.common.getDirectionByPos
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.networking.NetworkingEndecs
import vyrek.phasoritenetworks.networking.NetworkingEndecsData
import vyrek.phasoritenetworks.ui.UIMenu
import kotlin.uuid.Uuid

open class PhasoriteComponentBlock<T : PhasoriteComponentEntity>(props: Properties) :
	Block(props), EntityBlock {
	protected open var registryEntity: BlockEntityType<T>? = null

	override fun codec(): MapCodec<out BaseEntityBlock> {
		throw UnsupportedOperationException()
	}

	override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		super.setPlacedBy(level, pos, state, placer, stack)
		if (placer == null) return

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
		if (level.isClientSide || player !is ServerPlayer) return InteractionResult.PASS

		val entity = level.getBlockEntity(pos) as? PhasoriteComponentEntity ?: return InteractionResult.PASS

		val id = when (entity.componentType) {
			ComponentType.IMPORTER -> "phasoritenetworks:phasorite_importer"
			ComponentType.EXPORTER -> "phasoritenetworks:phasorite_exporter"
			else -> return InteractionResult.PASS
		}

		val defaultName = Component.translatable("block.${id.replace(':', '.')}").string

		val data = NetworkingEndecsData.ComponentData(
			id,
			pos,
			entity.name,
			defaultName,
			entity.priority,
			entity.overrideMode,
			entity.rawLimit,
			entity.limitlessMode
		)

		player.openMenu(
			SimpleMenuProvider({ containerId, _, _ -> UIMenu(containerId, data) }, Component.literal(""))
		) { buf ->
			buf.write(NetworkingEndecs.COMPONENT_ENDEC, data)
		}

		return InteractionResult.sidedSuccess(level.isClientSide)
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
		return throw UnsupportedOperationException()
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

	companion object {
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