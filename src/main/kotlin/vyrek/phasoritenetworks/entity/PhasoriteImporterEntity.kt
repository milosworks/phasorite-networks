package vyrek.phasoritenetworks.entity

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
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

	inner class EnergyStorage(private val side: Direction) : ILongEnergyStorage {
		override fun receive(energy: Long, simulate: Boolean): Long {
			if (network.isValid) {
				val maxLimit = minOf(limit, network.requestedEnergy)

				val toReturn = transferHandler.receive(energy, maxLimit, side, simulate)
				network.statistics.addEnergyTick(toReturn, NetworkStatistics.EnergyType.IMPORTED)

				level?.sendBlockUpdated(blockPos, blockState, blockState, 0)

				return toReturn
			}

			return 0
		}

		override fun extract(maxExtract: Long, simulate: Boolean): Long {
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
			return network.isValid
		}
	}
}