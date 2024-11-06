package vyrek.phasoritenetworks.entity

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
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

	fun distributeEnergy(): Long {
		if (!transferHandler.canDistribute) return 0L

		val toReturn = transferHandler.distribute(limit, distributionMode)
		network.statistics.addEnergyTick(toReturn, NetworkStatistics.EnergyType.EXPORTED)

		level?.sendBlockUpdated(blockPos, blockState, blockState, 0)

		return toReturn
	}

	inner class EnergyStorage() : ILongEnergyStorage {
		override fun receive(energy: Long, simulated: Boolean): Long {
			return 0
		}

		override fun extract(energy: Long, simulated: Boolean): Long {
			return 0
		}

		override fun getAmount(): Long {
			return 0
		}

		override fun getCapacity(): Long {
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