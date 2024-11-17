package vyrek.phasoritenetworks.common

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import vyrek.phasoritenetworks.client.ui.parseLong
import vyrek.phasoritenetworks.common.networks.ComponentType
import kotlin.uuid.Uuid

object Translations {
	val NETWORK = Component.translatable("gui.phasoritenetworks.network")
	val LIMIT = Component.translatable("gui.phasoritenetworks.network.limit")
	val PRIORITY = Component.translatable("gui.phasoritenetworks.network.priority")

	val OUTLINE = Component.translatable("gui.phasoritenetworks.outline")
	val COORDINATES =
		{ x: Int, y: Int, z: Int -> Component.translatable("gui.phasoritenetworks.coordinates", x, y, z) }
	val DIMENSION = { n: String -> Component.translatable("gui.phasoritenetworks.dimension", n) }

	val OCCUPIED = Component.translatable("gui.phasoritenetworks.occupied")
		.withStyle(ChatFormatting.BOLD)
		.withStyle(ChatFormatting.RED)
	val DENIED = Component.translatable("gui.phasoritenetworks.denied")
		.withStyle(ChatFormatting.BOLD)
		.withStyle(ChatFormatting.RED)

	val MAKE = { s: Component -> Component.translatable("gui.phasoritenetworks.make", s.string) }

	val NETWORK_ID = { id: Uuid -> Component.translatable("gui.phasoritenetworks.networkid", id.toString()) }

	fun relative(method: String): Component {
		return Component.translatable("gui.phasoritenetworks.${method}")
	}

	fun readableEnergy(type: ComponentType, energy: Long): Component {
		return Component.literal("${opPrefix(type)}${parseLong(energy)}")
	}

	fun energySuffix(str: String): Component {
		return Component.literal("$str FE/t")
	}

	fun opPrefix(type: ComponentType): String {
		return if (type == ComponentType.EXPORTER) "-" else "+"
	}
}