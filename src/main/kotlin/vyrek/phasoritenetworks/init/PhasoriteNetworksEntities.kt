package vyrek.phasoritenetworks.init

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity

//: AutoRegistryContainer<BlockEntityType<*>>
object PhasoriteNetworksEntities {
	val ENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		PhasoriteNetworks.ID
	)

	val PHASORITE_EXPORTER by ENTITIES.register("phasorite_exporter") { ->
		BlockEntityType.Builder.of(
			::PhasoriteExporterEntity,
			PhasoriteNetworksBlocks.PHASORITE_EXPORTER
		).build(null)
	}

	val PHASORITE_IMPORTER by ENTITIES.register("phasorite_importer") { ->
		BlockEntityType.Builder.of(
			::PhasoriteImporterEntity,
			PhasoriteNetworksBlocks.PHASORITE_IMPORTER
		).build(null)
	}

	fun init(event: IEventBus) {
		ENTITIES.register(event)
	}

	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PHASORITE_EXPORTER) { be, dir ->
			be.energyStorage
		}

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PHASORITE_IMPORTER) { be, dir ->
			be.sides.getOrPut(dir.get3DDataValue()) {
				be.EnergyStorage(dir)
			}
		}
	}

	//	override fun getRegistry(): Registry<BlockEntityType<*>> {
//		return BuiltInRegistries.BLOCK_ENTITY_TYPE
//	}
//
//	@Suppress("UNCHECKED_CAST")
//	override fun getTargetFieldType(): Class<BlockEntityType<*>> {
//		return BlockEntityType::class.java as Class<BlockEntityType<*>>
//	}

//	override fun afterFieldProcessing() {
//		NeoForge.EVENT_BUS.addListener<RegisterCapabilitiesEvent>(::registerCapabilities)
//	}
}