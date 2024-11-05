package vyrek.phasoritenetworks.init

import com.mojang.serialization.Codec
import io.wispforest.owo.serialization.CodecUtils
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.networking.PNEndecs
import vyrek.phasoritenetworks.networking.PNEndecsData

//val COMPONENT_CODEC: Codec<ComponentData> = RecordCodecBuilder.create { instance ->
//	instance.group(
//		Codec.STRING.fieldOf("name").forGetter(ComponentData::name),
//		Codec.INT.fieldOf("priority").forGetter(ComponentData::priority),
//		Codec.BOOL.fieldOf("overrideMode").forGetter(ComponentData::overrideMode),
//		Codec.INT.fieldOf("rawLimit").forGetter(ComponentData::rawLimit),
//		Codec.BOOL.fieldOf("limitlessMode").forGetter(ComponentData::limitlessMode),
//		UUIDUtil.CODEC.fieldOf("networkId").forGetter(ComponentData::networkId),
//		Codec.INT.fieldOf("color").forGetter(ComponentData::color)
//	).apply(instance, ::ComponentData)
//}
//
//val COMPONENT_STREAM_CODEC: StreamCodec<ByteBuf, ComponentData> = StreamCodec.composite(
//	ByteBufCodecs.STRING_UTF8, ComponentData::name,
//	ByteBufCodecs.INT, ComponentData::priority,
//	ByteBufCodecs.BOOL, ComponentData::overrideMode,
//	ByteBufCodecs.INT, ComponentData::rawLimit,
//	ByteBufCodecs.BOOL, ComponentData::limitlessMode,
//	UUIDUtil.STREAM_CODEC, ComponentData::networkId,
//	ByteBufCodecs.INT, ComponentData::color
//)

object PNComponents {
	private val DATA_COMPONENTS: DeferredRegister.DataComponents =
		DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PhasoriteNetworks.ID)

	private val COMPONENT_CODEC: Codec<PNEndecsData.ComponentData> = CodecUtils.toCodec(PNEndecs.COMPONENT_ENDEC)
	private val COMPONENT_STREAM_CODEC: StreamCodec<FriendlyByteBuf, PNEndecsData.ComponentData> =
		CodecUtils.toPacketCodec(PNEndecs.COMPONENT_ENDEC)
	val COMPONENT_DATA: DeferredHolder<DataComponentType<*>, DataComponentType<PNEndecsData.ComponentData>> =
		DATA_COMPONENTS.registerComponentType("component") {
			it.persistent(COMPONENT_CODEC)
				.networkSynchronized(COMPONENT_STREAM_CODEC)
		}

	fun init(event: IEventBus) {
		DATA_COMPONENTS.register(event)
	}
}