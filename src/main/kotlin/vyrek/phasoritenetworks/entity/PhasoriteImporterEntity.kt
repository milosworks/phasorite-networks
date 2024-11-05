package vyrek.phasoritenetworks.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.NetworkStatistics
import vyrek.phasoritenetworks.init.PNEntities

class PhasoriteImporterEntity(
	pos: BlockPos,
	state: BlockState,
) : PhasoriteComponentEntity(PNEntities.PHASORITE_IMPORTER, pos, state) {
	val sides: MutableMap<Int, EnergyStorage> = mutableMapOf()

	override var componentType = ComponentType.IMPORTER

	inner class EnergyStorage(val side: Direction) : IEnergyStorage {
		override fun receiveEnergy(energy: Int, simulate: Boolean): Int {
			if (network.isValid) {
				val maxLimit = minOf(limit, network.requestedEnergy)

				val toReturn = transferHandler.receive(energy, maxLimit, side, simulate)
				network.statistics.addEnergyTick(toReturn, NetworkStatistics.EnergyType.IMPORTED)

				return toReturn
			}
			return 0
		}

		override fun extractEnergy(energy: Int, simulate: Boolean): Int {
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
			return network.isValid
		}
	}
}