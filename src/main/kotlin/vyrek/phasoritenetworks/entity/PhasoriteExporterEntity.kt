package vyrek.phasoritenetworks.entity

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.common.networks.DistributionMode
import vyrek.phasoritenetworks.common.networks.TransferHandler
import vyrek.phasoritenetworks.init.PNEntities

class PhasoriteExporterEntity(
	pos: BlockPos,
	state: BlockState,
) : PhasoriteComponentEntity(PNEntities.PHASORITE_EXPORTER, pos, state) {
	val energyStorage = EnergyStorage()
	override val transferHandler = ExporterTransferHandler()

	private val distributionMode: DistributionMode = DistributionMode.ROUND_ROBIN

	override var componentType = ComponentType.EXPORTER

	inner class ExporterTransferHandler : TransferHandler() {
		override fun start() {
			super.start()

			requestedLimit = updateRequestedLimit()
		}

		override fun stop() {
			throughput = distribute()

			level?.sendBlockUpdated(blockPos, blockState, blockState, 0)
		}

		fun bufferLimiter(): Long {
			return maxOf(requestedLimit - buffer, 0)
		}

		private fun updateRequestedLimit(): Long {
			var req = 0L

			for (node in nodes.values) {
				val ex = node.insert(limit, true)
				req += ex
			}

			return req
		}

		private fun distribute(): Long {
			if (!transferHandler.canDistribute) return 0L

			val distributed = when (distributionMode) {
				DistributionMode.ROUND_ROBIN -> {
					distributeRoundRobin()
				}

				DistributionMode.FILL_FIRST -> {
					distributeFillFirst()
				}
			}

			return distributed
		}

		private fun distributeRoundRobin(): Long {
			var totalTransferred = 0L
			var energyTransferredInThisLoop: Boolean

			do {
				energyTransferredInThisLoop = false

				for (node in nodes.values) {
					val energyToTransfer = minOf(buffer, limit)
					val transferred = node.insert(energyToTransfer)

					if (transferred > 0L) {
						buffer -= transferred
						totalTransferred += transferred
						energyTransferredInThisLoop = true
					}
				}
			} while (buffer > 0L && energyTransferredInThisLoop)

			return totalTransferred
		}

		private fun distributeFillFirst(): Long {
			var totalTransferred = 0L

			for (node in nodes.values) {
				if (buffer <= 0L) break

				val energyToTransfer = minOf(buffer, limit)
				val transferred = node.insert(energyToTransfer)

				totalTransferred += transferred
				buffer -= transferred
			}

			return totalTransferred
		}
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