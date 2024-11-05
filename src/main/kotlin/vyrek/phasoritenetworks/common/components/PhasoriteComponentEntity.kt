package vyrek.phasoritenetworks.common.components

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Block.UPDATE_CLIENTS
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import vyrek.phasoritenetworks.common.components.PhasoriteComponentBlock.Companion.SIDES
import vyrek.phasoritenetworks.common.networks.*
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import vyrek.phasoritenetworks.init.PNComponents
import vyrek.phasoritenetworks.networking.PNEndecsData
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

open class PhasoriteComponentEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(type, pos, state), BlockEntityTicker<PhasoriteComponentEntity> {
	var initialized = false
	var name = ""
	val defaultName: String
		get() = when (componentType) {
			ComponentType.IMPORTER -> Component.translatable("block.phasoritenetworks.phasorite_importer")
				.string

			ComponentType.EXPORTER -> Component.translatable("block.phasoritenetworks.phasorite_exporter")
				.string

			else -> ""
		}

	var priority: Int = NetworkConstants.DEFAULT_PRIORITY
	var overrideMode = false
	var rawLimit: Int = NetworkConstants.DEFAULT_LIMIT
	val limit: Int
		get() = (if (limitlessMode) Int.MAX_VALUE else rawLimit)
	var limitlessMode = false

	var networkId: Uuid = Uuid.NIL
	var ownerUuid: UUID = Util.NIL_UUID

	var network = Network()

	val transferHandler = TransferHandler()

	open var componentType: ComponentType = ComponentType.INVALID

	fun connect(conn: Network) {
		network = conn
		networkId = conn.id

		setChanged()
		level?.sendBlockUpdated(blockPos, blockState, blockState, 0)
	}

	fun disconnect() {
		networkId = Uuid.NIL
		network = Network()

		setChanged()
		level?.sendBlockUpdated(blockPos, blockState, blockState, 0)
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

		val conn = Connection(ConnectionStatus.Connect, this)
		checkNetwork.connectionQueue.offer(conn)
	}

	override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: PhasoriteComponentEntity) {
		if (level.isClientSide) return

		if (!initialized) {
			if (networkId != Uuid.NIL && !network.isValid) handleNetworkConnection(networkId)

			var state = blockState

			for (direction in Direction.entries) {
				val target = level.getBlockEntity(worldPosition.relative(direction)) ?: continue
				if (target is PhasoriteImporterEntity || target is PhasoriteExporterEntity) continue
				val storage = level.getCapability(
					Capabilities.EnergyStorage.BLOCK,
					target.blockPos,
					target.blockState,
					target,
					direction.opposite
				) ?: continue

				transferHandler.updateNodes(direction, target, storage)
				state = state.setValue(SIDES[direction.get3DDataValue()], true)
			}

			if (state != blockState) level.setBlock(blockPos, state, UPDATE_CLIENTS)

			initialized = true
		}
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		tag.putUUID(NetworkConstants.OWNER, ownerUuid)
		tag.putUUID(NetworkConstants.ID, networkId.toJavaUuid())
		tag.putString(NetworkConstants.NAME, name)
		tag.putInt(NetworkConstants.PRIORITY, priority)
		tag.putBoolean(NetworkConstants.OVERRIDE_MODE, overrideMode)
		tag.putInt(NetworkConstants.RAW_LIMIT, rawLimit)
		tag.putBoolean(NetworkConstants.LIMITLESS_MODE, limitlessMode)

		tag.put(NetworkConstants.TRANSFER, CompoundTag().also { transferHandler.saveAdditional(it) })

		super.saveAdditional(tag, registries)
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		ownerUuid = tag.getUUID(NetworkConstants.OWNER)
		name = tag.getString(NetworkConstants.NAME).takeIf { it.isNotEmpty() } ?: ""
		priority = tag.getInt(NetworkConstants.PRIORITY)
		overrideMode = tag.getBoolean(NetworkConstants.OVERRIDE_MODE)
		rawLimit = tag.getInt(NetworkConstants.RAW_LIMIT)
		limitlessMode = tag.getBoolean(NetworkConstants.LIMITLESS_MODE)

		tag.getUUID(NetworkConstants.ID).toKotlinUuid()
			.takeIf { it != Uuid.NIL && ownerUuid.toKotlinUuid() != Uuid.NIL }?.let {
				networkId = it
				handleNetworkConnection(it)
			}

		tag.getCompound(NetworkConstants.TRANSFER).let {
			transferHandler.loadAdditional(it)
		}

		super.loadAdditional(tag, registries)
	}

	override fun collectImplicitComponents(components: DataComponentMap.Builder) {
		components.set(
			PNComponents.COMPONENT_DATA, PNEndecsData.ComponentData(
				name,
				priority,
				overrideMode,
				rawLimit,
				limitlessMode,
				networkId,
				network.color
			)
		)
	}

	override fun applyImplicitComponents(componentInput: DataComponentInput) {
		val data = componentInput.get(PNComponents.COMPONENT_DATA) ?: return

		name = data.name
		priority = data.priority
		overrideMode = data.overrideMode
		rawLimit = data.rawLimit
		limitlessMode = data.limitlessMode

		data.networkId.takeIf { it != Uuid.NIL }?.let {
			networkId = it
		}
	}

	override fun removeComponentsFromTag(tag: CompoundTag) {
		tag.remove(NetworkConstants.NAME)
		tag.remove(NetworkConstants.PRIORITY)
		tag.remove(NetworkConstants.OVERRIDE_MODE)
		tag.remove(NetworkConstants.RAW_LIMIT)
		tag.remove(NetworkConstants.LIMITLESS_MODE)
	}

	override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
		return CompoundTag().also {
			it.putUUID(NetworkConstants.ID, networkId.toJavaUuid())
			it.putInt(NetworkConstants.COLOR, network.color)
		}
	}

	override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
		return ClientboundBlockEntityDataPacket.create(this)
	}

	override fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
		network.id = tag.getUUID(NetworkConstants.ID).toKotlinUuid()
		network.color = tag.getInt(NetworkConstants.COLOR)

		level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE)
	}

	override fun onDataPacket(
		net: net.minecraft.network.Connection,
		pkt: ClientboundBlockEntityDataPacket,
		lookupProvider: HolderLookup.Provider
	) {
		val tag = pkt.tag

		network.id = tag.getUUID(NetworkConstants.ID).toKotlinUuid()
		network.color = tag.getInt(NetworkConstants.COLOR)

		level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE)
	}
}