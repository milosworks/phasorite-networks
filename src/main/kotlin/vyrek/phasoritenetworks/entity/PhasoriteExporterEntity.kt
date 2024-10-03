package vyrek.phasoritenetworks.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.DistributionMode
import vyrek.phasoritenetworks.init.PhasoriteNetworksEntities

class PhasoriteExporterEntity(
	pos: BlockPos,
	state: BlockState,
) : PhasoriteComponentEntity(PhasoriteNetworksEntities.PHASORITE_EXPORTER, pos, state) {
	val energyStorage = EnergyStorage()

	val distributionMode: DistributionMode = DistributionMode.ROUND_ROBIN

	override var componentType = ComponentType.EXPORTER

	fun distributeEnergy(): Int {
		if (!transferHandler.canDistribute) return 0

		return transferHandler.distribute(limit, distributionMode)
	}

	inner class EnergyStorage() : IEnergyStorage {
		override fun receiveEnergy(energy: Int, simulated: Boolean): Int {
			return 0
		}

		override fun extractEnergy(energy: Int, simulated: Boolean): Int {
			return 0
		}

		override fun getEnergyStored(): Int {
			return 0
		}

		override fun getMaxEnergyStored(): Int {
			return 0
		}

		override fun canExtract(): Boolean {
			return false
		}

		override fun canReceive(): Boolean {
			return false
		}
	}
}