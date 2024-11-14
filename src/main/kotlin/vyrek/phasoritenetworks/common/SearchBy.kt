package vyrek.phasoritenetworks.common

fun <T> List<T>.searchBy(input: String, predicate: (T) -> String): List<T> {
	val cleanedInput = input.trim().lowercase()

	return this.filter {
		val targetValue = predicate(it).lowercase()
		targetValue.equals(cleanedInput, ignoreCase = true) || targetValue.contains(cleanedInput)
	}
}