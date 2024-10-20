package vyrek.phasoritenetworks.common.networks

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class TransferHandler {
	val nodes: HashMap<Int, Node> = HashMap()

	var buffer = 0
	var requestedLimit = 0

	val canDistribute: Boolean
		get() = nodes.isNotEmpty()
	val canExtract: Boolean
		get() = buffer != 0

	fun receive(energy: Int, limit: Int, side: Direction, simulate: Boolean): Int {
		val availableSpace = limit - buffer
		val toReceive = minOf(availableSpace, energy)
		if (toReceive <= 0) return 0
		if (simulate) return toReceive

		buffer += toReceive

		return toReceive
		// We can do statistics and tell the network certain node gave energy to the plug


//		val node = nodes[side.get3DDataValue()] ?: return toReceive
//		node.insert(toReceive, false)
	}

	fun distribute(limit: Int, mode: DistributionMode): Int {
		when (mode) {
			DistributionMode.ROUND_ROBIN -> {
				var index = 0
				var energyTransferredInLastLoop = true

				while (buffer > 0 && energyTransferredInLastLoop) {
					energyTransferredInLastLoop = false

					for (node in nodes.values) {
						val energyToTransfer = minOf(buffer, limit)
						val moved = node.insert(energyToTransfer)

						if (moved > 0) {
							buffer -= moved
							energyTransferredInLastLoop = true
						}

						index++
					}
				}
			}

			DistributionMode.FILL_FIRST -> {
				for (node in nodes.values) {
					if (buffer <= 0) break

					val energyToTransfer = minOf(buffer, limit)
					val moved = node.insert(energyToTransfer)
					buffer -= moved
				}
			}
		}

		return buffer
	}

	fun updateRequestedLimit(limit: Int): Int {
		for (node in nodes.values) {
			requestedLimit += node.extract(limit, true)
		}

		return requestedLimit
	}

	fun updateNodes(direction: Direction, entity: BlockEntity, storage: IEnergyStorage) {
		nodes.entries.find { it.value.entity != entity.blockPos }?.let { (_, node) ->
			if (node.entity != entity) {
				nodes[direction.get3DDataValue()] = Node(direction, node.entity, storage)
			}
		} ?: run {
			nodes[direction.get3DDataValue()] = Node(direction, entity, storage)
		}
	}

	fun removeNode(direction: Direction): Boolean {
		return nodes.remove(direction.get3DDataValue()) != null
	}

	fun reset() {
//		buffer = 0
		requestedLimit = 0
	}

	fun saveAdditional(tag: CompoundTag) {
		tag.putInt(NetworkConstants.BUFFER, buffer)
	}

	fun loadAdditional(tag: CompoundTag) {
		buffer = tag.getInt(NetworkConstants.BUFFER)
	}
}