package vyrek.phasoritenetworks.common.networks

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class TransferHandler {
	val nodes: HashMap<Int, Node> = HashMap()

	var buffer = 0L
	var requestedLimit = 0L

	val canDistribute: Boolean
		get() = nodes.isNotEmpty()
	val canExtract: Boolean
		get() = buffer != 0L

	fun receive(energy: Long, limit: Long, side: Direction, simulate: Boolean): Long {
		val availableSpace = limit - buffer
		val toReceive = minOf(availableSpace, energy)
		if (toReceive <= 0) return 0
		if (simulate) return toReceive

		buffer += toReceive
//		nodes[side.get3DDataValue()]!!.energyStatistic += toReceive

		return toReceive
	}

	fun distribute(limit: Long, mode: DistributionMode): Long {
		var totalTransfer = 0L

		when (mode) {
			DistributionMode.ROUND_ROBIN -> {
				var index = 0
				var energyTransferredInLastLoop = true

				while (buffer > 0L && energyTransferredInLastLoop) {
					energyTransferredInLastLoop = false

					for (node in nodes.values) {
						val energyToTransfer = minOf(buffer, limit)
						val moved = node.insert(energyToTransfer)

						if (moved > 0L) {
							buffer -= moved
							totalTransfer += moved
							energyTransferredInLastLoop = true
						}

						index++
					}
				}
			}

			DistributionMode.FILL_FIRST -> {
				for (node in nodes.values) {
					if (buffer <= 0L) break

					val energyToTransfer = minOf(buffer, limit)
					val moved = node.insert(energyToTransfer)
					totalTransfer += moved
					buffer -= moved
				}
			}
		}

		return totalTransfer
	}

	fun updateRequestedLimit(limit: Long): Long {
		for (node in nodes.values) {
			val ex = node.insert(limit, true)
			requestedLimit += ex
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
		requestedLimit = 0L
	}

	fun saveAdditional(tag: CompoundTag) {
		tag.putLong(NetworkConstants.BUFFER, buffer)
	}

	fun loadAdditional(tag: CompoundTag) {
		buffer = tag.getLong(NetworkConstants.BUFFER)
	}
}