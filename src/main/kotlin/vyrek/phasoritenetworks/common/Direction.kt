package vyrek.phasoritenetworks.common

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

fun getDirectionByPos(pos1: BlockPos, pos2: BlockPos): Direction? {
	val directionVector = pos2.subtract(pos1)

	return Direction.entries.firstOrNull { direction ->
		val normal = direction.normal
		normal.x == directionVector.x && normal.y == directionVector.y && normal.z == directionVector.z
	}
}