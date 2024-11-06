package vyrek.phasoritenetworks.common.networks

const val TICKS_PER_SECOND = 20

class NetworkStatistics {
	companion object {
		private const val MAX_ENTRIES = 5
	}

	enum class EnergyType { IMPORTED, EXPORTED }

	private data class EnergyData(
		val tickEnergy: MutableList<Long> = mutableListOf(),
		var transferredEnergy: Long = 0L,
		val energyAverages: ArrayDeque<Long> = ArrayDeque(),
		var rollingAverage: Long = 0L
	)

	private val energyData = mutableMapOf(
		EnergyType.IMPORTED to EnergyData(),
		EnergyType.EXPORTED to EnergyData()
	)
	
	fun addEnergyTick(energy: Long, type: EnergyType) {
		val data = energyData[type]!!

		data.tickEnergy.add(energy)
	}

	fun onTick(tickCount: Int) {
		energyData.forEach { (_, data) ->
			if (tickCount % (TICKS_PER_SECOND / 4) == 0) {
				data.transferredEnergy = data.tickEnergy.sum()
			}

			if (tickCount % TICKS_PER_SECOND == 0) {
				data.rollingAverage += data.transferredEnergy
			}

			if (tickCount % (TICKS_PER_SECOND * 5) == 0) {
				data.energyAverages.addFirst(data.rollingAverage / 5)

				if (data.energyAverages.size >= MAX_ENTRIES) data.energyAverages.removeLast()
			}

			data.tickEnergy.clear()
		}
	}

	fun getTransferredEnergy(type: EnergyType): Long {
		val data = energyData[type]!!

		return data.transferredEnergy
	}
}