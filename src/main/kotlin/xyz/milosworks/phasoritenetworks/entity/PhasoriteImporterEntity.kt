package xyz.milosworks.phasoritenetworks.entity

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentEntity
import xyz.milosworks.phasoritenetworks.common.networks.ComponentType
import xyz.milosworks.phasoritenetworks.common.networks.TransferHandler
import xyz.milosworks.phasoritenetworks.init.PNEntities

class PhasoriteImporterEntity(
	pos: BlockPos,
	state: BlockState,
) : PhasoriteComponentEntity(PNEntities.PHASORITE_IMPORTER, pos, state) {
	val sides: MutableMap<Int, EnergyStorage> = mutableMapOf()
	override val transferHandler = ImporterTransferHandler()

	override var componentType = ComponentType.IMPORTER

	inner class ImporterTransferHandler : TransferHandler() {
		val canExtract: Boolean
			get() = buffer != 0L

		/**
		 * Energy that this importer received
		 */
		private var networkInput = 0L

		/**
		 * Energy that was removed when distributing energy from the network
		 */
		private var networkOutput = 0L

		override fun stop() {
			throughput = networkInput
			networkInput = 0L
			networkOutput = 0L

			level?.sendBlockUpdated(blockPos, blockState, blockState, 0)
		}

		fun extractFromBuffer(energy: Long): Long {
			val taken = minOf(minOf(energy, buffer), limit - networkOutput)
			require(taken >= 0) { "Taken should never be negative" }

			buffer -= taken
			networkOutput += taken

			return taken
		}

		fun receive(energy: Long, side: Direction, simulate: Boolean): Long {
			val maxLimit = minOf(limit, network.requestedEnergy)
			val availableSpace = maxLimit - buffer
			val toReceive = minOf(availableSpace, energy)
			if (toReceive <= 0) return 0
			if (simulate) return toReceive

			networkInput += toReceive
			buffer += toReceive
			// Node that gave the energy (this is why we don't support null side)
			nodes[side.get3DDataValue()]!!.throughput += toReceive

			return toReceive
		}
	}

	inner class EnergyStorage(private val side: Direction) : ILongEnergyStorage {
		override fun receive(energy: Long, simulate: Boolean): Long {
			if (network.isValid) {
				if (transferHandler.nodes[side.get3DDataValue()] == null) return 0

				val toReturn = transferHandler.receive(energy, side, simulate)
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