package vyrek.phasoritenetworks.common.networks

import io.wispforest.owo.ui.core.Color
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.MinecraftServer
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import vyrek.phasoritenetworks.networking.PNEndecsData
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class Network(
	var name: String = "",
	var owner: UUID = Util.NIL_UUID,
	var color: Int = Color.WHITE.argb(),
	var private: Boolean = true,
	var password: String = ""
) {
	var id: Uuid = Uuid.NIL
	val isValid: Boolean
		get() = id != Uuid.NIL

	private val members: MutableMap<UUID, NetworkUser> = mutableMapOf()

	val components: MutableList<PhasoriteComponentEntity> = mutableListOf()

	val connectionQueue: Queue<Connection> = LinkedList()

	var requestedEnergy = 0

	val statistics = NetworkStatistics()

	fun <T : PhasoriteComponentEntity> filterComponents(type: ComponentType): List<T> {
		@Suppress("UNCHECKED_CAST")
		return (components.filter { c ->
			c.componentType == type
		} as List<T>).sortedWith(
			compareByDescending<T> { it.overrideMode }
				.thenByDescending { it.priority }
		)
	}

	private fun calcRequestedEnergy() {
		for (exporter in filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER)) {
			requestedEnergy += exporter.transferHandler.updateRequestedLimit(exporter.limit)
		}
	}

	fun onTick(server: MinecraftServer) {
		val importers = filterComponents<PhasoriteImporterEntity>(ComponentType.IMPORTER)
		val exporters = filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER)

		for (importer in importers) {
			if (!importer.transferHandler.canExtract) continue

			var buffer = importer.transferHandler.buffer

			for (exporter in exporters) {
				if (!exporter.transferHandler.canDistribute) continue
				if (exporter.transferHandler.buffer >= exporter.transferHandler.requestedLimit) continue

				val limit = exporter.limit.coerceAtMost(exporter.transferHandler.requestedLimit)
				val taken = limit.coerceAtMost(buffer)

				exporter.transferHandler.buffer += taken
				buffer -= taken
				exporter.transferHandler.requestedLimit -= taken

				if (buffer <= 0) break
			}

			importer.transferHandler.buffer = buffer
		}

		connectionQueue.forEach { conn ->
			when (conn.status) {
				ConnectionStatus.Connect -> {
					components.add(conn.target)
					conn.target.connect(this)

					if (conn.target.ownerUuid != owner && !members.containsKey(conn.target.ownerUuid)) {
						val player = server.playerList.getPlayer(conn.target.ownerUuid)!!
						val member = NetworkUser(
							conn.target.ownerUuid,
							player.displayName!!.string
						)

						members[conn.target.ownerUuid] = member
					}

					connectionQueue.remove(conn)
				}

				ConnectionStatus.Disconnect -> {
					components.remove(conn.target)
					conn.target.disconnect()

					if (conn.target.ownerUuid != owner && components.find { c -> c.ownerUuid == conn.target.ownerUuid } == null) {
						members.remove(conn.target.ownerUuid)
					}

					connectionQueue.remove(conn)
				}
			}
		}

		components.forEach { com ->
			if (com is PhasoriteExporterEntity) {
				com.distributeEnergy()
			}

			com.transferHandler.reset()
		}

		requestedEnergy = 0
		calcRequestedEnergy()
		statistics.onTick(server.tickCount)
	}

	fun saveAdditional(tag: CompoundTag) {
		tag.putUUID(NetworkConstants.ID, id.toJavaUuid())
		tag.putString(NetworkConstants.NAME, name)
		tag.putUUID(NetworkConstants.OWNER, owner)
		tag.putInt(NetworkConstants.COLOR, color)
		tag.putString(NetworkConstants.PASSWORD, password)
		tag.putBoolean(NetworkConstants.PRIVATE, private)

		val membersList = ListTag()
		for ((_, user) in members) {
			val memberTag = CompoundTag()
			user.saveAdditional(memberTag)
			membersList.add(membersList)
		}
		tag.put(NetworkConstants.MEMBERS, membersList)
	}

	fun loadAdditional(tag: CompoundTag) {
		id = tag.getUUID(NetworkConstants.ID).toKotlinUuid()
		name = tag.getString(NetworkConstants.NAME)
		owner = tag.getUUID(NetworkConstants.OWNER)
		color = tag.getInt(NetworkConstants.COLOR)
		password = tag.getString(NetworkConstants.PASSWORD)
		private = tag.getBoolean(NetworkConstants.PRIVATE)

		val membersList = tag.getList(NetworkConstants.MEMBERS, Tag.TAG_COMPOUND.toInt())
		for (i in 0 until membersList.size) {
			val memberTag = membersList.getCompound(i)
			val member = NetworkUser(memberTag)
			members[member.uuid] = member
		}
	}

	fun discoverable(uuid: UUID): Boolean {
		return when {
			uuid == owner -> true
			private && members.containsKey(uuid) -> true
			!private -> true
			else -> false
		}
	}

	fun toClientData(includeExtra: Boolean = false): PNEndecsData.ClientNetworkData {
		return PNEndecsData.ClientNetworkData(
			id,
			name,
			color,
			owner,
			private,
			password,
			members.mapValues { it.value.toClientData() },
			if (includeExtra) PNEndecsData.ExtraNetworkData(
				filterComponents<PhasoriteImporterEntity>(ComponentType.IMPORTER).size,
				filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER).size,
				statistics.getTransferredEnergy(NetworkStatistics.EnergyType.IMPORTED),
				statistics.getTransferredEnergy(NetworkStatistics.EnergyType.EXPORTED)
			) else null
		)
	}
}

enum class ConnectionStatus {
	Connect,
	Disconnect
}

data class Connection(val status: ConnectionStatus, val target: PhasoriteComponentEntity)