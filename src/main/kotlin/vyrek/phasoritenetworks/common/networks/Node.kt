package vyrek.phasoritenetworks.common.networks

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class Node(val direction: Direction, val entity: BlockEntity, val storage: IEnergyStorage) {
//	var energyStatistic: Int = 0

	fun insert(amount: Int, simulated: Boolean = false): Int {
		if (entity.isRemoved) return 0
		if (!storage.canReceive()) return 0
//		if (!simulated) energyStatistic += amount

		return storage.receiveEnergy(amount, simulated)
	}

	// Not necessary as we follow FE convention
//	fun extract(amount: Int, simulated: Boolean = false): Int {
//		if (entity.isRemoved) return 0
//		if (!storage.canExtract()) return 0
//
//		return storage.extractEnergy(amount, simulated)
//	}
}