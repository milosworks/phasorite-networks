package vyrek.phasoritenetworks.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.DistributionMode
import vyrek.phasoritenetworks.common.networks.NetworkStatistics
import vyrek.phasoritenetworks.init.PNEntities

class PhasoriteExporterEntity(
	pos: BlockPos,
	state: BlockState,
) : PhasoriteComponentEntity(PNEntities.PHASORITE_EXPORTER, pos, state) {
	val energyStorage = EnergyStorage()

	private val distributionMode: DistributionMode = DistributionMode.ROUND_ROBIN

	override var componentType = ComponentType.EXPORTER

	fun distributeEnergy(): Int {
		if (!transferHandler.canDistribute) return 0

		val toReturn = transferHandler.distribute(limit, distributionMode)
		network.statistics.addEnergyTick(toReturn, NetworkStatistics.EnergyType.EXPORTED)

		return toReturn
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