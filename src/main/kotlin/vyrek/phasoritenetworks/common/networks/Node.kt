package vyrek.phasoritenetworks.common.networks

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class Node(val direction: Direction, val entity: BlockEntity, private val storage: IEnergyStorage) {
//	var energyStatistic: Int = 0

	fun insert(amount: Long, simulated: Boolean = false): Long {
		if (entity.isRemoved) return 0
		if (!storage.canReceive()) return 0
//		if (!simulated) energyStatistic += amount

		return if (storage is ILongEnergyStorage) storage.receive(amount, simulated)
		else storage.receiveEnergy(amount.toInt(), simulated).toLong()
	}
}