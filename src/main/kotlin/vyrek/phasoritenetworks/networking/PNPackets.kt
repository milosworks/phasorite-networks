package vyrek.phasoritenetworks.networking

import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import vyrek.phasoritenetworks.common.networks.NetworkUserAccess
import java.util.*
import kotlin.uuid.Uuid

interface PosRecord {
	val pos: BlockPos
}

enum class ManageType {
	KICK,
	PASS_OWNERSHIP,
	SET_ACCESS
}

enum class ActionType {
	DISCONNECT_NETWORK,
	DELETE_NETWORK,
	CLOSE_MENU
}

enum class PutType {
	CREATE, UPDATE
}

object PNPackets {
	@JvmRecord
	data class UpdateComponentData(
		override val pos: BlockPos,
		val name: String,
		val priority: Int,
		val overrideMode: Boolean,
		val limit: Int,
		val limitlessMode: Boolean
	) : PosRecord

	@JvmRecord
	data class UpdateComponentScreenData(
		override val pos: BlockPos,
		val network: PNEndecsData.ClientNetworkData,
		val accessibleNetworks: List<PNEndecsData.ClientNetworkData>,
	) : PosRecord

	@JvmRecord
	data class PutNetwork(
		override val pos: BlockPos,
		val type: PutType,
		val name: String,
		val owner: UUID,
		val color: Int,
		val private: Boolean,
		val password: String,
		val id: Uuid = Uuid.NIL
	) : PosRecord

	@JvmRecord
	data class ConnectNetwork(
		override val pos: BlockPos,
		val networkId: Uuid,
		val userId: UUID,
		val password: String
	) : PosRecord

	@JvmRecord
	data class DisconnectComponents(
		override val pos: BlockPos,
		val positions: List<GlobalPos>
	) : PosRecord

	@JvmRecord
	data class ManagePlayer(
		override val pos: BlockPos,
		val uuid: UUID,
		val type: ManageType,
		val access: NetworkUserAccess
	) : PosRecord

	@JvmRecord
	data class CommandPacket(
		override val pos: BlockPos,
		val action: ActionType
	) : PosRecord
}