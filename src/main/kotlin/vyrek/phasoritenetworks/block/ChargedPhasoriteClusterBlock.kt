package vyrek.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState

class ChargedPhasoriteClusterBlock(height: Double, offset: Double, props: Properties) :
	PhasoriteClusterBlock(height, offset, props) {
	override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
		return super.canSurvive(state, level, pos) && (level.canSeeSky(pos) && (!level.dimensionType()
			.hasFixedTime() && level.skyDarken < 4))
	}
}