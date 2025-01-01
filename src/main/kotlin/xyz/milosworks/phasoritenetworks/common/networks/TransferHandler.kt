package xyz.milosworks.phasoritenetworks.common.networks

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

open class TransferHandler {
	val nodes: HashMap<Int, Node> = HashMap()

	var throughput = 0L

	var buffer = 0L
	var requestedLimit = 0L

	val canDistribute: Boolean
		get() = nodes.isNotEmpty()

	open fun start() {
		for ((_, node) in nodes) {
			node.start()
		}
	}

	open fun stop() {}

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

	fun saveAdditional(tag: CompoundTag) {
//		tag.putLong(NetworkConstants.BUFFER, buffer)
		tag.putLong(NetworkConstants.THROUGHPUT, throughput)
	}

	fun loadAdditional(tag: CompoundTag) {
//		buffer = tag.getLong(NetworkConstants.BUFFER)
		throughput = tag.getLong(NetworkConstants.THROUGHPUT)
	}
}