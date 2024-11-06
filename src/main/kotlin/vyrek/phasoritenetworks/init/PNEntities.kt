@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package vyrek.phasoritenetworks.init

import dev.technici4n.grandpower.api.ILongEnergyStorage
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity

object PNEntities {
	private val ENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		PhasoriteNetworks.ID
	)

	val PHASORITE_EXPORTER by ENTITIES.register("phasorite_exporter") { ->
		BlockEntityType.Builder.of(
			::PhasoriteExporterEntity,
			PNBlocks.PHASORITE_EXPORTER
		).build(null)
	}

	val PHASORITE_IMPORTER by ENTITIES.register("phasorite_importer") { ->
		BlockEntityType.Builder.of(
			::PhasoriteImporterEntity,
			PNBlocks.PHASORITE_IMPORTER
		).build(null)
	}

	fun init(event: IEventBus) {
		ENTITIES.register(event)
		MOD_BUS.addListener(::registerCapabilities)
	}

	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PHASORITE_EXPORTER) { be, _ ->
			be.energyStorage
		}
		event.registerBlockEntity(ILongEnergyStorage.BLOCK, PHASORITE_EXPORTER) { be, _ ->
			be.energyStorage
		}

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PHASORITE_IMPORTER) { be, dir ->
			if (dir == null) return@registerBlockEntity null

			be.sides.getOrPut(dir.get3DDataValue()) {
				be.EnergyStorage(dir)
			}
		}
		event.registerBlockEntity(ILongEnergyStorage.BLOCK, PHASORITE_IMPORTER) { be, dir ->
			if (dir == null) return@registerBlockEntity null

			be.sides.getOrPut(dir.get3DDataValue()) {
				be.EnergyStorage(dir)
			}
		}
	}

}