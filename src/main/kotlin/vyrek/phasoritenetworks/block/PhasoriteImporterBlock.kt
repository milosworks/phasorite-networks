package vyrek.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import vyrek.phasoritenetworks.common.components.PhasoriteComponentBlock
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import vyrek.phasoritenetworks.init.PhasoriteNetworksEntities

class PhasoriteImporterBlock(props: BlockBehaviour.Properties) :
	PhasoriteComponentBlock<PhasoriteImporterEntity>(props) {
	override var registryEntity = PhasoriteNetworksEntities.PHASORITE_IMPORTER

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
		return PhasoriteImporterEntity(pos, state)
	}
}

