package vyrek.phasoritenetworks.networking

import io.wispforest.endec.*
import io.wispforest.endec.impl.BuiltInEndecs
import io.wispforest.endec.impl.StructEndecBuilder
import io.wispforest.owo.serialization.endec.MinecraftEndecs
import net.minecraft.core.BlockPos
import java.util.*
import kotlin.uuid.Uuid

object Endecs {
	val UUID: Endec<Uuid> = Endec.of(
		{ ctx: SerializationContext?, serializer: Serializer<*>, value: Uuid? ->
			serializer.writeBytes(ctx, value!!.toByteArray())
		},
		{ ctx: SerializationContext?, deserializer: Deserializer<*> ->
			val bytes = deserializer.readBytes(ctx)
			Uuid.fromByteArray(bytes)
		})
}

object PNEndecsData {
	data class ComponentScreenData(
		val pos: BlockPos,
		val blockId: String,
		var name: String,
		val defaultName: String,
		var limit: Int,
		var limitlessMode: Boolean,
		var priority: Int,
		var overrideMode: Boolean,
		val network: ClientNetworkData?,
		val accessibleNetworks: List<ClientNetworkData>,
	)

	data class ClientNetworkData(
		val id: Uuid,
		val name: String,
		val color: Int,
		val owner: UUID,
		val private: Boolean,
		val password: String,
		val members: Map<UUID, ClientUserData>,
		val components: List<BlockPos>,
		val extra: ExtraNetworkData?
	)

	data class ExtraNetworkData(
		val importers: Int,
		val exporters: Int,
		val importedEnergy: Long,
		val exportedEnergy: Long
	)

	data class ClientUserData(
		val uuid: UUID,
		val name: String,
		val access: Int
	)

	data class ComponentData(
		val name: String,
		val priority: Int,
		val overrideMode: Boolean,
		val rawLimit: Int,
		val limitlessMode: Boolean,
		val networkId: Uuid,
		val color: Int,
	)
}

object PNEndecs {
	val COMPONENT_ENDEC: StructEndec<PNEndecsData.ComponentData> = StructEndecBuilder.of(
		Endec.STRING.fieldOf("name", PNEndecsData.ComponentData::name),
		Endec.INT.fieldOf("priority", PNEndecsData.ComponentData::priority),
		Endec.BOOLEAN.fieldOf("overrideMode", PNEndecsData.ComponentData::overrideMode),
		Endec.INT.fieldOf("rawLimit", PNEndecsData.ComponentData::rawLimit),
		Endec.BOOLEAN.fieldOf("limitlessMode", PNEndecsData.ComponentData::limitlessMode),
		Endecs.UUID.fieldOf("networkId", PNEndecsData.ComponentData::networkId),
		Endec.INT.fieldOf("color", PNEndecsData.ComponentData::color),
		PNEndecsData::ComponentData
	)

	val CLIENT_USER_ENDEC: StructEndec<PNEndecsData.ClientUserData> = StructEndecBuilder.of(
		BuiltInEndecs.UUID.fieldOf("uuid", PNEndecsData.ClientUserData::uuid),
		Endec.STRING.fieldOf("name", PNEndecsData.ClientUserData::name),
		Endec.INT.fieldOf("access", PNEndecsData.ClientUserData::access),
		PNEndecsData::ClientUserData
	)

	val EXTRA_NETWORK_ENDEC: StructEndec<PNEndecsData.ExtraNetworkData> = StructEndecBuilder.of(
		Endec.INT.fieldOf("importers", PNEndecsData.ExtraNetworkData::importers),
		Endec.INT.fieldOf("exporters", PNEndecsData.ExtraNetworkData::exporters),
		Endec.LONG.fieldOf("importedEnergy", PNEndecsData.ExtraNetworkData::importedEnergy),
		Endec.LONG.fieldOf("exportedEnergy", PNEndecsData.ExtraNetworkData::exportedEnergy),
		PNEndecsData::ExtraNetworkData
	)

	val CLIENT_NETWORK_ENDEC: StructEndec<PNEndecsData.ClientNetworkData> = StructEndecBuilder.of(
		Endecs.UUID.fieldOf("id", PNEndecsData.ClientNetworkData::id),
		Endec.STRING.fieldOf("name", PNEndecsData.ClientNetworkData::name),
		Endec.INT.fieldOf("color", PNEndecsData.ClientNetworkData::color),
		BuiltInEndecs.UUID.fieldOf("owner", PNEndecsData.ClientNetworkData::owner),
		Endec.BOOLEAN.fieldOf("private", PNEndecsData.ClientNetworkData::private),
		Endec.STRING.fieldOf("password", PNEndecsData.ClientNetworkData::password),
		Endec.map(BuiltInEndecs.UUID, CLIENT_USER_ENDEC)
			.fieldOf("members", PNEndecsData.ClientNetworkData::members),
		MinecraftEndecs.BLOCK_POS.listOf().fieldOf("components", PNEndecsData.ClientNetworkData::components),
		EXTRA_NETWORK_ENDEC.optionalFieldOf("extra", PNEndecsData.ClientNetworkData::extra) { null },
		PNEndecsData::ClientNetworkData
	)

	val COMPONENT_SCREEN_ENDEC: StructEndec<PNEndecsData.ComponentScreenData> = StructEndecBuilder.of(
		MinecraftEndecs.BLOCK_POS.fieldOf("pos", PNEndecsData.ComponentScreenData::pos),
		Endec.STRING.fieldOf("blockId", PNEndecsData.ComponentScreenData::blockId),
		Endec.STRING.fieldOf("name", PNEndecsData.ComponentScreenData::name),
		Endec.STRING.fieldOf("name", PNEndecsData.ComponentScreenData::defaultName),
		Endec.INT.fieldOf("limit", PNEndecsData.ComponentScreenData::limit),
		Endec.BOOLEAN.fieldOf("limitlessMode", PNEndecsData.ComponentScreenData::limitlessMode),
		Endec.INT.fieldOf("priority", PNEndecsData.ComponentScreenData::priority),
		Endec.BOOLEAN.fieldOf("overrideMode", PNEndecsData.ComponentScreenData::overrideMode),
		CLIENT_NETWORK_ENDEC.optionalFieldOf("network", PNEndecsData.ComponentScreenData::network) { null },
		CLIENT_NETWORK_ENDEC.listOf()
			.fieldOf(" accessibleNetworks", PNEndecsData.ComponentScreenData::accessibleNetworks),
		PNEndecsData::ComponentScreenData
	)
}