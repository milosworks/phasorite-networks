package vyrek.phasoritenetworks.networking

import io.wispforest.endec.Endec
import io.wispforest.endec.StructEndec
import io.wispforest.endec.impl.BuiltInEndecs
import io.wispforest.endec.impl.StructEndecBuilder
import io.wispforest.endec.impl.StructField
import io.wispforest.owo.serialization.CodecUtils
import io.wispforest.owo.serialization.endec.MinecraftEndecs
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import vyrek.phasoritenetworks.common.networks.ComponentType
import java.util.*
import kotlin.uuid.Uuid

// Utility functions for creating common Endec fields
fun <T> stringField(name: String, getter: (T) -> String): StructField<T, String> =
	Endec.STRING.fieldOf(name, getter)

fun <T> intField(name: String, getter: (T) -> Int): StructField<T, Int> =
	Endec.INT.fieldOf(name, getter)

fun <T> booleanField(name: String, getter: (T) -> Boolean): StructField<T, Boolean> =
	Endec.BOOLEAN.fieldOf(name, getter)

fun <T, S> Endec<T>.listField(name: String, getter: (S) -> List<T>): StructField<S, List<T>> =
	listOf().fieldOf(name, getter)

// Endec Definitions
object Endecs {
	val UUID: Endec<Uuid> = Endec.of(
		{ ctx, serializer, value -> serializer.writeBytes(ctx, value!!.toByteArray()) },
		{ ctx, deserializer -> Uuid.fromByteArray(deserializer.readBytes(ctx)) }
	)

	val GLOBAL_POS: Endec<GlobalPos> = CodecUtils.toEndec(GlobalPos.CODEC, GlobalPos.STREAM_CODEC)
}

object PNEndecsData {
	// Represents the data of a component's screen, including its position, block ID, name, limits, and associated networks
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
		val accessibleNetworks: List<ClientNetworkData>
	)

	// Represents a network's data on the client-side (not the server-side representation)
	data class ClientNetworkData(
		val id: Uuid,
		val name: String,
		val color: Int,
		val owner: UUID,
		val private: Boolean,
		val password: String,
		val members: Map<UUID, ClientUserData>,
		val components: List<RawComponentData>,
		val extra: ExtraNetworkData?
	)

	// Represents extra statistics related to the network, such as energy flow and number of importers/exporters
	data class ExtraNetworkData(
		val importers: Int,
		val exporters: Int,
		val importedEnergy: Long,
		val exportedEnergy: Long
	)

	// Represents a network member on the client-side, including their name and access level
	data class ClientUserData(
		val uuid: UUID,
		val name: String,
		val access: Int
	)

	// Represents the data of a component (e.g., block entity) on the network used to update the server state
	data class ComponentData(
		val name: String,
		val priority: Int,
		val overrideMode: Boolean,
		val rawLimit: Int,
		val limitlessMode: Boolean,
		val networkId: Uuid,
		val color: Int
	)

	// Represents the data of a component (e.g., block entity) on the network used for data components.
	data class ItemComponentData(
		val name: String,
		val priority: Int,
		val overrideMode: Boolean,
		val rawLimit: Int,
		val limitlessMode: Boolean,
		val networkId: Uuid,
		val networkName: String,
		val color: Int
	)

	// Represents raw data of components on the network, including their name, position, and type
	data class RawComponentData(
		val name: String,
		val owner: UUID,
		val globalPos: GlobalPos,
		val type: ComponentType,
		val throughput: Long
	)
}

object PNEndecs {
	val RAW_COMPONENT_ENDEC: StructEndec<PNEndecsData.RawComponentData> = StructEndecBuilder.of(
		stringField("name", PNEndecsData.RawComponentData::name),
		BuiltInEndecs.UUID.fieldOf("owner", PNEndecsData.RawComponentData::owner),
		Endecs.GLOBAL_POS.fieldOf("pos", PNEndecsData.RawComponentData::globalPos),
		Endec.INT.xmap({ v -> ComponentType.entries[v] }, { v -> v.ordinal })
			.fieldOf("type", PNEndecsData.RawComponentData::type),
		Endec.LONG.fieldOf("buffer", PNEndecsData.RawComponentData::throughput),
		PNEndecsData::RawComponentData
	)

	val COMPONENT_ENDEC: StructEndec<PNEndecsData.ItemComponentData> = StructEndecBuilder.of(
		stringField("name", PNEndecsData.ItemComponentData::name),
		intField("priority", PNEndecsData.ItemComponentData::priority),
		booleanField("overrideMode", PNEndecsData.ItemComponentData::overrideMode),
		intField("rawLimit", PNEndecsData.ItemComponentData::rawLimit),
		booleanField("limitlessMode", PNEndecsData.ItemComponentData::limitlessMode),
		Endecs.UUID.fieldOf("networkId", PNEndecsData.ItemComponentData::networkId),
		stringField("networkName", PNEndecsData.ItemComponentData::networkName),
		intField("color", PNEndecsData.ItemComponentData::color),
		PNEndecsData::ItemComponentData
	)

	val CLIENT_USER_ENDEC: StructEndec<PNEndecsData.ClientUserData> = StructEndecBuilder.of(
		BuiltInEndecs.UUID.fieldOf("uuid", PNEndecsData.ClientUserData::uuid),
		stringField("name", PNEndecsData.ClientUserData::name),
		intField("access", PNEndecsData.ClientUserData::access),
		PNEndecsData::ClientUserData
	)

	val EXTRA_NETWORK_ENDEC: StructEndec<PNEndecsData.ExtraNetworkData> = StructEndecBuilder.of(
		intField("importers", PNEndecsData.ExtraNetworkData::importers),
		intField("exporters", PNEndecsData.ExtraNetworkData::exporters),
		Endec.LONG.fieldOf("importedEnergy", PNEndecsData.ExtraNetworkData::importedEnergy),
		Endec.LONG.fieldOf("exportedEnergy", PNEndecsData.ExtraNetworkData::exportedEnergy),
		PNEndecsData::ExtraNetworkData
	)

	val CLIENT_NETWORK_ENDEC: StructEndec<PNEndecsData.ClientNetworkData> = StructEndecBuilder.of(
		Endecs.UUID.fieldOf("id", PNEndecsData.ClientNetworkData::id),
		stringField("name", PNEndecsData.ClientNetworkData::name),
		intField("color", PNEndecsData.ClientNetworkData::color),
		BuiltInEndecs.UUID.fieldOf("owner", PNEndecsData.ClientNetworkData::owner),
		booleanField("private", PNEndecsData.ClientNetworkData::private),
		stringField("password", PNEndecsData.ClientNetworkData::password),
		Endec.map(BuiltInEndecs.UUID, CLIENT_USER_ENDEC).fieldOf("members", PNEndecsData.ClientNetworkData::members),
		RAW_COMPONENT_ENDEC.listField("components", PNEndecsData.ClientNetworkData::components),
		EXTRA_NETWORK_ENDEC.optionalFieldOf("extra", PNEndecsData.ClientNetworkData::extra) { null },
		PNEndecsData::ClientNetworkData
	)

	val COMPONENT_SCREEN_ENDEC: StructEndec<PNEndecsData.ComponentScreenData> = StructEndecBuilder.of(
		MinecraftEndecs.BLOCK_POS.fieldOf("pos", PNEndecsData.ComponentScreenData::pos),
		stringField("blockId", PNEndecsData.ComponentScreenData::blockId),
		stringField("name", PNEndecsData.ComponentScreenData::name),
		stringField("defaultName", PNEndecsData.ComponentScreenData::defaultName),
		intField("limit", PNEndecsData.ComponentScreenData::limit),
		booleanField("limitlessMode", PNEndecsData.ComponentScreenData::limitlessMode),
		intField("priority", PNEndecsData.ComponentScreenData::priority),
		booleanField("overrideMode", PNEndecsData.ComponentScreenData::overrideMode),
		CLIENT_NETWORK_ENDEC.optionalFieldOf("network", PNEndecsData.ComponentScreenData::network) { null },
		CLIENT_NETWORK_ENDEC.listField("accessibleNetworks", PNEndecsData.ComponentScreenData::accessibleNetworks),
		PNEndecsData::ComponentScreenData
	)
}