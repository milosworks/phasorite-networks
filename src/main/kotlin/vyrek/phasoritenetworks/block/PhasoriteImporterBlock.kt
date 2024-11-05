package vyrek.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import vyrek.phasoritenetworks.common.components.PhasoriteComponentBlock
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import vyrek.phasoritenetworks.init.PNEntities


class PhasoriteImporterBlock(props: Properties) :
	PhasoriteComponentBlock<PhasoriteImporterEntity>(props) {
	override var registryEntity: BlockEntityType<PhasoriteImporterEntity>? = null
		get() = PNEntities.PHASORITE_IMPORTER

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
		return PhasoriteImporterEntity(pos, state)
	}

	override fun makeShape(): VoxelShape {
		var shape = Shapes.empty()
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5720000000000002,
				0.37399999999999994,
				0.1760000000000001,
				0.6260000000000002,
				0.626,
				0.21200000000000008
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.374, 0.37399999999999994, 0.1760000000000001, 0.42800000000000005, 0.626, 0.21200000000000008),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.42800000000000005,
				0.37399999999999994,
				0.1760000000000001,
				0.5720000000000002,
				0.42800000000000005,
				0.21200000000000008
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.42800000000000005,
				0.5720000000000001,
				0.1760000000000001,
				0.5720000000000002,
				0.626,
				0.21200000000000008
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.21200000000000008, 0.21200000000000002, 0.21200000000000008, 0.788, 0.7880000000000001, 0.788),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6440000000000001,
				0.644,
				0.14000000000000007,
				0.8600000000000001,
				0.8599999999999999,
				0.35600000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.14000000000000007,
				0.644,
				0.14000000000000007,
				0.35600000000000015,
				0.8599999999999999,
				0.35600000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6440000000000001, 0.14, 0.6440000000000001, 0.8600000000000001, 0.356, 0.8600000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6440000000000001,
				0.644,
				0.6440000000000001,
				0.8600000000000001,
				0.8599999999999999,
				0.8600000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.14000000000000007,
				0.644,
				0.6440000000000001,
				0.35600000000000015,
				0.8599999999999999,
				0.8600000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.14000000000000007, 0.14, 0.6440000000000001, 0.35600000000000015, 0.356, 0.8600000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6440000000000001, 0.14, 0.14000000000000007, 0.8600000000000001, 0.356, 0.35600000000000015),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.14000000000000007, 0.14, 0.14000000000000007, 0.35600000000000015, 0.356, 0.35600000000000015),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.5720000000000001, 0.37399999999999994, 0.788, 0.6260000000000002, 0.626, 0.8240000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.4280000000000002, 0.5720000000000001, 0.788, 0.5720000000000001, 0.626, 0.8240000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.374, 0.37399999999999994, 0.788, 0.4280000000000002, 0.626, 0.8240000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4280000000000002,
				0.37399999999999994,
				0.788,
				0.5720000000000001,
				0.42800000000000005,
				0.8240000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.788,
				0.37399999999999994,
				0.42800000000000005,
				0.8240000000000001,
				0.42800000000000005,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.788, 0.5720000000000001, 0.42800000000000005, 0.8240000000000001, 0.626, 0.5720000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.788, 0.37399999999999994, 0.5720000000000001, 0.8240000000000001, 0.626, 0.6260000000000002),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.788, 0.37399999999999994, 0.374, 0.8240000000000001, 0.626, 0.42800000000000005),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.176,
				0.37399999999999994,
				0.4280000000000002,
				0.21200000000000002,
				0.42800000000000005,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.176, 0.37399999999999994, 0.374, 0.21200000000000002, 0.626, 0.4280000000000002),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.176, 0.37399999999999994, 0.5720000000000001, 0.21200000000000002, 0.626, 0.6260000000000002),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.176, 0.5720000000000001, 0.4280000000000002, 0.21200000000000002, 0.626, 0.5720000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5720000000000001,
				0.7880000000000001,
				0.4280000000000002,
				0.6260000000000002,
				0.8240000000000003,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.374,
				0.7880000000000001,
				0.5720000000000001,
				0.6260000000000002,
				0.8240000000000003,
				0.6260000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.374, 0.7880000000000001, 0.374, 0.6260000000000002, 0.8240000000000003, 0.4280000000000002),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.374,
				0.7880000000000001,
				0.4280000000000002,
				0.4280000000000002,
				0.8240000000000003,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.374,
				0.1759999999999997,
				0.4280000000000002,
				0.4280000000000002,
				0.2119999999999999,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.374, 0.1759999999999997, 0.374, 0.6260000000000002, 0.2119999999999999, 0.4280000000000002),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5720000000000001,
				0.1759999999999997,
				0.4280000000000002,
				0.6260000000000002,
				0.2119999999999999,
				0.5720000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.374,
				0.1759999999999997,
				0.5720000000000001,
				0.6260000000000002,
				0.2119999999999999,
				0.6260000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.21200000000000008, 0.21200000000000002, 0.21200000000000008, 0.788, 0.7880000000000001, 0.788),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6440000000000001, 0.14, 0.14000000000000007, 0.8600000000000001, 0.356, 0.35600000000000015),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.14000000000000007, 0.14, 0.14000000000000007, 0.35600000000000015, 0.356, 0.35600000000000015),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.14000000000000007,
				0.644,
				0.14000000000000007,
				0.35600000000000015,
				0.8599999999999999,
				0.35600000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6440000000000001,
				0.644,
				0.14000000000000007,
				0.8600000000000001,
				0.8599999999999999,
				0.35600000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.14000000000000007, 0.14, 0.6440000000000001, 0.35600000000000015, 0.356, 0.8600000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6440000000000001, 0.14, 0.6440000000000001, 0.8600000000000001, 0.356, 0.8600000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6440000000000001,
				0.644,
				0.6440000000000001,
				0.8600000000000001,
				0.8599999999999999,
				0.8600000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.14000000000000007,
				0.644,
				0.6440000000000001,
				0.35600000000000015,
				0.8599999999999999,
				0.8600000000000001
			),
			BooleanOp.OR
		)

		return shape
	}
}

