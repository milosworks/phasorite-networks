//package vyrek.phasoritenetworks.init
//
//import com.mojang.serialization.Codec
//import net.minecraft.core.BlockPos
//import net.minecraft.network.codec.ByteBufCodecs
//import net.neoforged.bus.api.IEventBus
//import net.neoforged.neoforge.registries.DeferredRegister
//import vyrek.phasoritenetworks.PhasoriteNetworks
//
//object PhasoriteNetworksDataComponents {
//	private val DATA_COMPONENTS = DeferredRegister.createDataComponents(PhasoriteNetworks.ID)
//
//	val LOCATOR_SETUP = DATA_COMPONENTS.registerComponentType("locator_setup") {builder ->
//		builder
//			.persistent(Codec.BOOL)
//			.networkSynchronized(ByteBufCodecs.BOOL)
//	}
//	val LOCATOR = DATA_COMPONENTS.registerComponentType("locator") { builder ->
//		builder
//			.persistent(BlockPos.CODEC)
//			.networkSynchronized(BlockPos.STREAM_CODEC)
//	}
//
//	fun register(event: IEventBus) {
//		DATA_COMPONENTS.register(event)
//	}
//}

//object PhasoriteNetworksDataComponents: AutoRegistryContainer<DataComponentType<*>> {
//	val LOCATOR_SETUP = DataComponentType.builder<Boolean>()
//		.persistent(Codec.BOOL)
//		.networkSynchronized(ByteBufCodecs.BOOL)
//		.build()
//	val LOCATOR = DataComponentType.builder<BlockPos>()
//		.persistent(BlockPos.CODEC)
//		.networkSynchronized(BlockPos.STREAM_CODEC)
//		.build()
//
//	override fun getRegistry(): Registry<DataComponentType<*>> {
//		return BuiltInRegistries.DATA_COMPONENT_TYPE
//	}
//
//	@Suppress("UNCHECKED_CAST")
//	override fun getTargetFieldType(): Class<DataComponentType<*>> {
//		return DataComponentType::class.java as Class<DataComponentType<*>>
//	}
//}