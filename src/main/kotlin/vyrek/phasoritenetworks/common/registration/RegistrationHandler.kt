// All of this code is from owo-lib was reworked to work with objects in kotlin
// https://github.com/wisp-forest/owo-lib/blob/1.21/src/main/java/io/wispforest/owo/registration/reflect/FieldRegistrationHandler.java.

package vyrek.phasoritenetworks.common.registration

import io.wispforest.owo.registration.annotations.IterationIgnored
import io.wispforest.owo.registration.reflect.AutoRegistryContainer
import io.wispforest.owo.registration.reflect.FieldProcessingSubject
import io.wispforest.owo.util.ReflectionUtils.FieldConsumer
import io.wispforest.owo.util.ReflectionUtils.getFieldName
import net.minecraft.core.Registry
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.PhasoriteNetworks.LOGGER

object RegistrationHandler {
	fun <T> register(container: AutoRegistryContainer<T>, recursive: Boolean = false) {
		if (container::class.objectInstance == null) throw RuntimeException("Object has no instance")

		LOGGER.info("Initializing registry object name: ${container.javaClass.name}")

		iterateObjectFields(container, container.targetFieldType, createProcessor(container) { value, name, field ->
			Registry.register(container.registry, PhasoriteNetworks.identifier(name), value!!)
			container.postProcessField(PhasoriteNetworks.ID, value, name, field)
		})

		container.afterFieldProcessing()
	}

	private fun <T> createProcessor(handler: FieldProcessingSubject<T>, consumer: FieldConsumer<T>): FieldConsumer<T> {
		return FieldConsumer { value, name, field ->
			if (handler.shouldProcessField(value, name, field)) {
				consumer.accept(value, name, field)
			}
		}
	}

	private fun <O, F> iterateObjectFields(
		obj: O,
		type: Class<F>,
		consumer: FieldConsumer<F>
	) {
		val objClass = obj!!::class.java

		for (field in objClass.declaredFields) {
			if (field.name == "INSTANCE") continue

			field.isAccessible = true

			var value: F? = null

			try {
				@Suppress("UNCHECKED_CAST")
				value = field.get(obj) as F?
			} catch (e: Exception) {
				continue
			}

			if (value == null && !type.isAssignableFrom(field.type)) continue
			if (field.isAnnotationPresent(IterationIgnored::class.java)) continue

			val name = getFieldName(field)

			consumer.accept(value, name, field)
		}
	}
}