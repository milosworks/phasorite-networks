package vyrek.phasoritenetworks.common.components

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.Network
import vyrek.phasoritenetworks.common.networks.NetworkConstants
import vyrek.phasoritenetworks.common.networks.NetworksData
import vyrek.phasoritenetworks.common.networks.TransferHandler

open class PhasoriteComponentEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(type, pos, state), BlockEntityTicker<PhasoriteComponentEntity> {
	var initialized = false
	var name = ""

	var priority = NetworkConstants.DEFAULT_PRIORITY
	var overrideMode = false
	var rawLimit: Int = NetworkConstants.DEFAULT_LIMIT
	val limit: Int
		get() = (if (limitlessMode) Int.MAX_VALUE else rawLimit)
	var limitlessMode = false

	var networkId = NetworkConstants.INVALID_NUM
	var ownerUuid = Util.NIL_UUID

	var network = Network()

	val transferHandler = TransferHandler()

	open var componentType: ComponentType = ComponentType.INVALID

	fun connect(conn: Network) {
		network = conn
		networkId = conn.id
	}

	fun handleNetworkConnection(id: Int) {
		if (id == NetworkConstants.INVALID_NUM) {
			networkId = NetworkConstants.INVALID_NUM
			network = Network()

			return
		}

		val checkNetwork = NetworksData.get().getNetwork(id)
		if (checkNetwork == null) {
			throw IllegalArgumentException("No network found with id: $id")
		}

		checkNetwork.connectionQueue.offer(this)
	}

	fun updateRequestedLimit() {
		transferHandler.updateRequestedLimit(limit)
	}

	override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: PhasoriteComponentEntity) {
		if (!initialized) {
			for (direction in Direction.values()) {
				val target = level.getBlockEntity(worldPosition.relative(direction)) ?: continue
				val storage = level.getCapability(
					Capabilities.EnergyStorage.BLOCK,
					target.blockPos,
					target.blockState,
					target,
					direction
				) ?: continue

				transferHandler.updateNodes(direction, target, storage)
			}

			initialized = true
		}
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)

		if (networkId != NetworkConstants.INVALID_NUM) tag.putInt(NetworkConstants.ID, networkId)
		if (name != "") tag.putString(NetworkConstants.NAME, name)
		tag.putInt(NetworkConstants.PRIORITY, priority)
		tag.putBoolean(NetworkConstants.OVERRIDE_MODE, overrideMode)
		tag.putInt(NetworkConstants.RAW_LIMIT, rawLimit)
		tag.putBoolean(NetworkConstants.LIMITLESS_MODE, limitlessMode)
		tag.putUUID(NetworkConstants.OWNER, ownerUuid)
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)

		tag.takeIf { it.contains(NetworkConstants.ID) }?.let {
			networkId = it.getInt(NetworkConstants.ID)
			handleNetworkConnection(networkId)
		}
		name = tag.getString(NetworkConstants.NAME).takeIf { it.isNotEmpty() } ?: name
		priority = tag.getInt(NetworkConstants.PRIORITY)
		overrideMode = tag.getBoolean(NetworkConstants.OVERRIDE_MODE)
		rawLimit = tag.getInt(NetworkConstants.RAW_LIMIT)
		limitlessMode = tag.getBoolean(NetworkConstants.LIMITLESS_MODE)
		ownerUuid = tag.getUUID(NetworkConstants.OWNER)
	}
}