package vyrek.phasoritenetworks.common.components

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.common.networks.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

open class PhasoriteComponentEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(type, pos, state), BlockEntityTicker<PhasoriteComponentEntity> {
	private var initialized = false
	var name = ""
		get() {
			if (field != "") return field

			return when (componentType) {
				ComponentType.IMPORTER -> Component.translatable("block.phasoritenetworks.phasorite_importer")
					.string

				ComponentType.EXPORTER -> Component.translatable("block.phasoritenetworks.phasorite_exporter")
					.string

				else -> ""
			}
		}

	var priority: Int = NetworkConstants.DEFAULT_PRIORITY
	var overrideMode = false
	var rawLimit: Int = NetworkConstants.DEFAULT_LIMIT
	val limit: Int
		get() = (if (limitlessMode) Int.MAX_VALUE else rawLimit)
	var limitlessMode = false

	var networkId: Uuid = Uuid.NIL
	var ownerUuid = Util.NIL_UUID

	var network = Network()

	val transferHandler = TransferHandler()

	open var componentType: ComponentType = ComponentType.INVALID

	fun connect(conn: Network) {
		network = conn
		networkId = conn.id
	}

	fun disconnect() {
		networkId = Uuid.NIL
		network = Network()
	}

	fun handleNetworkConnection(id: Uuid) {
		if (id == Uuid.NIL) {
			if (network.isValid) {
				network.connectionQueue.offer(Connection(ConnectionStatus.Disconnect, this))

				return
			}

			disconnect()

			return
		}

		val checkNetwork = NetworksData.get().getNetwork(id)
			?: throw IllegalArgumentException("No network found with id: $id")

		checkNetwork.connectionQueue.offer(Connection(ConnectionStatus.Connect, this))
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
		tag.putUUID(NetworkConstants.ID, networkId.toJavaUuid())
		tag.putString(NetworkConstants.NAME, name)
		tag.putInt(NetworkConstants.PRIORITY, priority)
		tag.putBoolean(NetworkConstants.OVERRIDE_MODE, overrideMode)
		tag.putInt(NetworkConstants.RAW_LIMIT, rawLimit)
		tag.putBoolean(NetworkConstants.LIMITLESS_MODE, limitlessMode)
		tag.putUUID(NetworkConstants.OWNER, ownerUuid)

		super.saveAdditional(tag, registries)
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		tag.getUUID(NetworkConstants.ID).toKotlinUuid().takeIf { it != Uuid.NIL }?.let {
			networkId = it
			handleNetworkConnection(it)
		}

		name = tag.getString(NetworkConstants.NAME).takeIf { it.isNotEmpty() } ?: name
		priority = tag.getInt(NetworkConstants.PRIORITY)
		overrideMode = tag.getBoolean(NetworkConstants.OVERRIDE_MODE)
		rawLimit = tag.getInt(NetworkConstants.RAW_LIMIT)
		limitlessMode = tag.getBoolean(NetworkConstants.LIMITLESS_MODE)
		ownerUuid = tag.getUUID(NetworkConstants.OWNER)

		super.loadAdditional(tag, registries)
	}
}