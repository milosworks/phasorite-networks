package vyrek.phasoritenetworks.common.networks

import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity

inline fun <T> Iterable<T>.cappedSumOf(selector: (T) -> Long): Long {
	var sum: Long = 0
	for (element in this) {
		val value = selector(element)
		if (sum == Long.MAX_VALUE || sum > Long.MAX_VALUE - value) {
			return Long.MAX_VALUE
		}
		sum += value
	}
	return sum
}

const val TICKS_PER_SECOND = 20

class NetworkStatistics(private val network: Network) {
	companion object {
		private const val MAX_ENTRIES = 5
	}

	var inputEnergy = 0L
	var exportEnergy = 0L
	var throughput = 0L

	val networkChange = mutableListOf<Long>()

	fun onTick(tickCount: Int) {
		// Every 20 ticks
		if (tickCount % TICKS_PER_SECOND == 0) {
			inputEnergy = network.filterComponents<PhasoriteImporterEntity>(ComponentType.IMPORTER)
				.cappedSumOf { it.transferHandler.throughput }
			exportEnergy = network.filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER)
				.cappedSumOf { it.transferHandler.throughput }
		}
		// Every 100 ticks
		if (tickCount % (TICKS_PER_SECOND * 5) == 0) {
			networkChange.addFirst(inputEnergy - exportEnergy)

			if (networkChange.size > MAX_ENTRIES) networkChange.removeLast()
		}
	}
}