package vyrek.phasoritenetworks.common

fun <T> List<T>.searchBy(input: String, selector: (T) -> String): List<T> {
	val cleanedInput = input.trim().lowercase()

	return this.filter {
		val targetValue = selector(it).lowercase()
		targetValue.equals(cleanedInput, ignoreCase = true) || targetValue.contains(cleanedInput)
	}
}