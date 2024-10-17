package vyrek.phasoritenetworks.networking

import io.wispforest.owo.ui.core.Color
import net.minecraft.core.BlockPos
import java.util.*

object NetworkingPackets {
	@JvmRecord
	data class UpdateComponentData(
		val pos: BlockPos,
		val name: String,
		val priority: Int,
		val overrideMode: Boolean,
		val limit: Int,
		val limitlessMode: Boolean
	)

	@JvmRecord
	data class CreateNetwork(
		val pos: BlockPos,
		val name: String,
		val owner: UUID,
		val color: Color
	)
}