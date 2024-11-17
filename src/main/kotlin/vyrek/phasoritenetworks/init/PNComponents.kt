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

object PNComponents {
	private val DATA_COMPONENTS: DeferredRegister.DataComponents =
		DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PhasoriteNetworks.ID)

	private val COMPONENT_CODEC: Codec<PNEndecsData.ItemComponentData> =
		CodecUtils.toCodec(PNEndecs.COMPONENT_ENDEC)
	private val COMPONENT_STREAM_CODEC: StreamCodec<FriendlyByteBuf, PNEndecsData.ItemComponentData> =
		CodecUtils.toPacketCodec(PNEndecs.COMPONENT_ENDEC)
	val COMPONENT_DATA: DeferredHolder<DataComponentType<*>, DataComponentType<PNEndecsData.ItemComponentData>> =
		DATA_COMPONENTS.registerComponentType("component") {
			it.persistent(COMPONENT_CODEC)
				.networkSynchronized(COMPONENT_STREAM_CODEC)
		}

	fun init(event: IEventBus) {
		DATA_COMPONENTS.register(event)
	}
}