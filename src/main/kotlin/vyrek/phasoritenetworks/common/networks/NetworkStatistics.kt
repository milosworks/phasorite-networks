package vyrek.phasoritenetworks.common.networks

const val TICKS_PER_SECOND = 20

class NetworkStatistics {
	companion object {
		private const val MAX_ENTRIES = 5
	}

	enum class EnergyType { IMPORTED, EXPORTED }

	private data class EnergyData(
		val tickEnergy: MutableList<Int> = mutableListOf(),
		var transferredEnergy: Int = 0,
		val energyAverages: ArrayDeque<Double> = ArrayDeque(),
		var rollingAverage: Int = 0
	)

	private val energyData = mutableMapOf(
		EnergyType.IMPORTED to EnergyData(),
		EnergyType.EXPORTED to EnergyData()
	)

	/**
	 * Adds the specified energy amount to the statistics for the current tick.
	 *
	 * @param energy The amount of energy to be added for the current tick.
	 * @param type The energy type being added, whether it is input or output energy.
	 */
	fun addEnergyTick(energy: Int, type: EnergyType) {
		val data = energyData[type]!!

		data.tickEnergy.add(energy)
	}

	/**
	 * Called each tick to update network statistics.
	 *
	 * @param tickCount Current tick count of the game.
	 */
	fun onTick(tickCount: Int) {
		energyData.forEach { (_, data) ->
			if (tickCount % (TICKS_PER_SECOND / 4) == 0) {
				data.transferredEnergy = data.tickEnergy.sum()
			}

			if (tickCount % TICKS_PER_SECOND == 0) {
				data.rollingAverage += data.transferredEnergy
			}

			if (tickCount % (TICKS_PER_SECOND * 5) == 0) {
				data.energyAverages.addFirst(data.rollingAverage.toDouble() / 5)

				if (data.energyAverages.size >= MAX_ENTRIES) data.energyAverages.removeLast()
			}

			data.tickEnergy.clear()
		}
	}

	/**
	 * Get the transferred energy (input/output) of the network.
	 *
	 * @param type Whether to get the input or output energy.
	 */
	fun getTransferredEnergy(type: EnergyType): Int {
		val data = energyData[type]!!

		return data.transferredEnergy
	}
}