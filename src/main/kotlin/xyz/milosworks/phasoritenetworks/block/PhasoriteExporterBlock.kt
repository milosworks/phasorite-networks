package xyz.milosworks.phasoritenetworks.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentBlock
import xyz.milosworks.phasoritenetworks.entity.PhasoriteExporterEntity
import xyz.milosworks.phasoritenetworks.init.PNEntities


class PhasoriteExporterBlock(props: Properties) :
	PhasoriteComponentBlock<PhasoriteExporterEntity>(props) {
	override var registryEntity: BlockEntityType<PhasoriteExporterEntity>? = null
		get() = PNEntities.PHASORITE_EXPORTER

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
		return PhasoriteExporterEntity(pos, state)
	}

	override fun makeShape(): VoxelShape {
		var shape = Shapes.empty()
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.37512500000000015,
				0.37737499999999996,
				0.17600000000000005,
				0.4291250000000001,
				0.629375,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5731250000000001,
				0.37737499999999996,
				0.17600000000000005,
				0.6271250000000003,
				0.629375,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.37737499999999996,
				0.17600000000000005,
				0.5731250000000001,
				0.43137499999999995,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.575375,
				0.17600000000000005,
				0.5731250000000001,
				0.629375,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.21312500000000012,
				0.21537499999999998,
				0.21200000000000002,
				0.7891250000000001,
				0.7913750000000002,
				0.788
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7846250000000001,
				0.37737499999999996,
				0.572,
				0.8206249999999999,
				0.629375,
				0.6259999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7846250000000001,
				0.37737499999999996,
				0.37399999999999983,
				0.8206249999999999,
				0.629375,
				0.4279999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.7846250000000001, 0.575375, 0.4279999999999998, 0.8206249999999999, 0.629375, 0.572),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7846250000000001,
				0.37737499999999996,
				0.4279999999999998,
				0.8206249999999999,
				0.43137499999999995,
				0.572
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.2356250000000002,
				0.6428750000000001,
				0.1715000000000001,
				0.3616250000000002,
				0.768875,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.2356250000000002,
				0.23787500000000011,
				0.1715000000000001,
				0.3616250000000002,
				0.36387499999999984,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.640625,
				0.6428750000000001,
				0.1715000000000001,
				0.7666249999999999,
				0.768875,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.640625,
				0.23787500000000011,
				0.1715000000000001,
				0.7666249999999999,
				0.36387499999999995,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6428750000000003,
				0.23787500000000011,
				0.788,
				0.7688750000000003,
				0.36387499999999984,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.23787500000000011,
				0.788,
				0.36612500000000037,
				0.36387499999999995,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.6428750000000001,
				0.788,
				0.36612500000000037,
				0.768875,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6451250000000002, 0.6428750000000001, 0.788, 0.7711250000000004, 0.768875, 0.8284999999999999),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6451250000000002,
				0.7778749999999999,
				0.23449999999999993,
				0.7711250000000001,
				0.8183749999999999,
				0.36049999999999965
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.7778749999999999,
				0.23449999999999993,
				0.36612500000000037,
				0.8183749999999999,
				0.3604999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.7778749999999999,
				0.6395,
				0.36612500000000037,
				0.8183749999999999,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6451250000000002,
				0.7778749999999999,
				0.6395,
				0.7711250000000001,
				0.8183749999999999,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.23112500000000014,
				0.17487500000000017,
				0.2345000000000002,
				0.3571250000000002,
				0.2153750000000001,
				0.36050000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.23112500000000014,
				0.17487500000000017,
				0.6395000000000001,
				0.3571250000000002,
				0.2153750000000001,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6361250000000004,
				0.17487500000000017,
				0.6395000000000001,
				0.762125,
				0.2153750000000001,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6361250000000004,
				0.17487500000000017,
				0.2345000000000002,
				0.762125,
				0.2153750000000001,
				0.36050000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.23787500000000011,
				0.635,
				0.8296249999999996,
				0.36387499999999995,
				0.7609999999999997
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.23787500000000011,
				0.23000000000000015,
				0.8296249999999996,
				0.36387499999999984,
				0.356
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.6428750000000001,
				0.23000000000000015,
				0.8296249999999996,
				0.768875,
				0.356
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.7891250000000001, 0.6428750000000001, 0.635, 0.8296249999999996, 0.768875, 0.7609999999999997),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.23787500000000011,
				0.2345000000000002,
				0.21312500000000012,
				0.36387499999999995,
				0.36049999999999993
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.23787500000000011,
				0.6395,
				0.21312500000000012,
				0.36387499999999984,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.6428750000000001,
				0.6395,
				0.21312500000000012,
				0.768875,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.6428750000000001,
				0.2345000000000002,
				0.21312500000000012,
				0.768875,
				0.36049999999999993
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.37737499999999996,
				0.42800000000000005,
				0.20862500000000023,
				0.43137500000000006,
				0.5720000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.37737499999999996,
				0.37400000000000017,
				0.20862500000000023,
				0.629375,
				0.42800000000000005
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.5753750000000001,
				0.42800000000000005,
				0.20862500000000023,
				0.629375,
				0.5720000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.37737499999999996,
				0.5720000000000002,
				0.20862500000000023,
				0.629375,
				0.6260000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.42912500000000026, 0.575375, 0.788, 0.5731250000000003, 0.629375, 0.8240000000000001),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5731250000000003,
				0.37737499999999996,
				0.788,
				0.6271250000000003,
				0.629375,
				0.8240000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.37512500000000026,
				0.37737499999999996,
				0.788,
				0.42912500000000026,
				0.629375,
				0.8240000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.42912500000000026,
				0.37737499999999996,
				0.788,
				0.5731250000000003,
				0.43137499999999995,
				0.8240000000000001
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.7913749999999998,
				0.5764999999999998,
				0.5731250000000001,
				0.8273749999999999,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5731250000000001,
				0.7913749999999998,
				0.37849999999999984,
				0.6271250000000003,
				0.8273749999999999,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.37512500000000015,
				0.7913749999999998,
				0.37849999999999984,
				0.4291250000000001,
				0.8273749999999999,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.7913749999999998,
				0.37849999999999984,
				0.5731250000000001,
				0.8273749999999999,
				0.4325
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.17262500000000003,
				0.5764999999999998,
				0.5731250000000001,
				0.20862500000000006,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.5731250000000001,
				0.17262500000000003,
				0.37849999999999984,
				0.6271250000000003,
				0.20862500000000006,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.4291250000000001,
				0.17262500000000003,
				0.37849999999999984,
				0.5731250000000001,
				0.20862500000000006,
				0.4325
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.37512500000000015,
				0.17262500000000003,
				0.37849999999999984,
				0.4291250000000001,
				0.20862500000000006,
				0.6304999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.21312500000000012,
				0.21537499999999998,
				0.21200000000000002,
				0.7891250000000001,
				0.7913750000000002,
				0.788
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.2356250000000002,
				0.6428750000000001,
				0.1715000000000001,
				0.3616250000000002,
				0.768875,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.640625,
				0.6428750000000001,
				0.1715000000000001,
				0.7666249999999999,
				0.768875,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.640625,
				0.23787500000000011,
				0.1715000000000001,
				0.7666249999999999,
				0.36387499999999995,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.2356250000000002,
				0.23787500000000011,
				0.1715000000000001,
				0.3616250000000002,
				0.36387499999999984,
				0.21200000000000002
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6361250000000004,
				0.17487500000000017,
				0.6395000000000001,
				0.762125,
				0.2153750000000001,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6361250000000004,
				0.17487500000000017,
				0.2345000000000002,
				0.762125,
				0.2153750000000001,
				0.36050000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.23112500000000014,
				0.17487500000000017,
				0.6395000000000001,
				0.3571250000000002,
				0.2153750000000001,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.23112500000000014,
				0.17487500000000017,
				0.2345000000000002,
				0.3571250000000002,
				0.2153750000000001,
				0.36050000000000015
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6428750000000003,
				0.23787500000000011,
				0.788,
				0.7688750000000003,
				0.36387499999999984,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.23787500000000011,
				0.788,
				0.36612500000000037,
				0.36387499999999995,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.6451250000000002, 0.6428750000000001, 0.788, 0.7711250000000004, 0.768875, 0.8284999999999999),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.6428750000000001,
				0.788,
				0.36612500000000037,
				0.768875,
				0.8284999999999999
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.23787500000000011,
				0.23000000000000015,
				0.8296249999999996,
				0.36387499999999984,
				0.356
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.23787500000000011,
				0.635,
				0.8296249999999996,
				0.36387499999999995,
				0.7609999999999997
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(0.7891250000000001, 0.6428750000000001, 0.635, 0.8296249999999996, 0.768875, 0.7609999999999997),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.7891250000000001,
				0.6428750000000001,
				0.23000000000000015,
				0.8296249999999996,
				0.768875,
				0.356
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6451250000000002,
				0.7778749999999999,
				0.6395,
				0.7711250000000001,
				0.8183749999999999,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.7778749999999999,
				0.6395,
				0.36612500000000037,
				0.8183749999999999,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.24012500000000048,
				0.7778749999999999,
				0.23449999999999993,
				0.36612500000000037,
				0.8183749999999999,
				0.3604999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.6451250000000002,
				0.7778749999999999,
				0.23449999999999993,
				0.7711250000000001,
				0.8183749999999999,
				0.36049999999999965
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.6428750000000001,
				0.6395,
				0.21312500000000012,
				0.768875,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.6428750000000001,
				0.2345000000000002,
				0.21312500000000012,
				0.768875,
				0.36049999999999993
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.23787500000000011,
				0.6395,
				0.21312500000000012,
				0.36387499999999984,
				0.7654999999999998
			),
			BooleanOp.OR
		)
		shape = Shapes.join(
			shape,
			Shapes.box(
				0.17262500000000014,
				0.23787500000000011,
				0.2345000000000002,
				0.21312500000000012,
				0.36387499999999995,
				0.36049999999999993
			),
			BooleanOp.OR
		)

		return shape
	}
}

