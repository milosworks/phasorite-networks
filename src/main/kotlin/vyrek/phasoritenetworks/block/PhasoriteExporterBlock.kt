package vyrek.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import vyrek.phasoritenetworks.common.components.PhasoriteComponentBlock
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.init.PhasoriteNetworksEntities

class PhasoriteExporterBlock(props: Properties) : PhasoriteComponentBlock<PhasoriteExporterEntity>(props) {
	override var registryEntity = PhasoriteNetworksEntities.PHASORITE_EXPORTER

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
		return PhasoriteExporterEntity(pos, state)
	}
}

