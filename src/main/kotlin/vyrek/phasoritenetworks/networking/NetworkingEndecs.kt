package vyrek.phasoritenetworks.networking

import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import io.wispforest.owo.serialization.endec.MinecraftEndecs
import net.minecraft.core.BlockPos

object NetworkingEndecsData {
	data class ComponentData(
		val id: String,
		val pos: BlockPos,
		val name: String,
		val defaultName: String,
		val priority: Int,
		val overrideMode: Boolean,
		val limit: Int,
		val limitlessMode: Boolean
	)
}

object NetworkingEndecs {
	val COMPONENT_ENDEC = StructEndecBuilder.of(
		Endec.STRING.fieldOf("id", NetworkingEndecsData.ComponentData::id),
		MinecraftEndecs.BLOCK_POS.fieldOf("pos", NetworkingEndecsData.ComponentData::pos),
		Endec.STRING.fieldOf("name", NetworkingEndecsData.ComponentData::name),
		Endec.STRING.fieldOf("default_name", NetworkingEndecsData.ComponentData::defaultName),
		Endec.INT.fieldOf("priority", NetworkingEndecsData.ComponentData::priority),
		Endec.BOOLEAN.fieldOf("overrideMode", NetworkingEndecsData.ComponentData::overrideMode),
		Endec.INT.fieldOf("limit", NetworkingEndecsData.ComponentData::limit),
		Endec.BOOLEAN.fieldOf("limitlessMode", NetworkingEndecsData.ComponentData::limitlessMode),
		NetworkingEndecsData::ComponentData
	)
}