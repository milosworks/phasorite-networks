package vyrek.phasoritenetworks.common.networks

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class Node(val direction: Direction, val entity: BlockEntity, val storage: IEnergyStorage) {
	fun insert(amount: Int, simulated: Boolean = false): Int {
		if (entity.isRemoved) return 0
		if (!storage.canReceive()) return 0

		return storage.receiveEnergy(amount, simulated)
	}

	fun extract(amount: Int, simulated: Boolean = false): Int {
		if (entity.isRemoved) return 0
		if (!storage.canExtract()) return 0

		return storage.extractEnergy(amount, simulated)
	}
}