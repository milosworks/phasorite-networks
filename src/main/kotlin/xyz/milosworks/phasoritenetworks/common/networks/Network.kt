package xyz.milosworks.phasoritenetworks.common.networks

import io.wispforest.endec.impl.KeyedEndec
import io.wispforest.owo.ui.core.Color
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.MinecraftServer
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentEntity
import xyz.milosworks.phasoritenetworks.entity.PhasoriteExporterEntity
import xyz.milosworks.phasoritenetworks.entity.PhasoriteImporterEntity
import xyz.milosworks.phasoritenetworks.networking.Endecs
import xyz.milosworks.phasoritenetworks.networking.PNEndecsData
import java.util.*
import kotlin.uuid.Uuid

val comparator = compareByDescending<PhasoriteComponentEntity> { it.overrideMode }.thenByDescending { it.priority }

val UUID_KEY = KeyedEndec(NetworkConstants.ID, Endecs.UUID, Uuid.NIL)

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

	val members: MutableMap<UUID, NetworkUser> = mutableMapOf()

	val components: MutableList<PhasoriteComponentEntity> = mutableListOf()

	val connectionQueue: Queue<Connection> = LinkedList()

	var requestedEnergy = 0L

	val statistics = NetworkStatistics(this)

	fun <T : PhasoriteComponentEntity> filterComponents(type: ComponentType): List<T> {
		@Suppress("UNCHECKED_CAST")
		return (components.filter { c ->
			c.componentType == type
		} as List<T>)
	}

	fun onPostTick(server: MinecraftServer) {
		handleConnections(server)

		val importers = filterComponents<PhasoriteImporterEntity>(ComponentType.IMPORTER)
		val exporters = filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER).onEach {
			it.transferHandler.start()
		}

		for (imp in importers) {
			imp.transferHandler.start()

			if (!imp.transferHandler.canExtract) continue

			val eligibleExporters =
				exporters.filter { it.transferHandler.canDistribute && it.transferHandler.buffer < it.transferHandler.requestedLimit }

			for (exp in eligibleExporters) {
				if (!imp.transferHandler.canExtract) break

				val lim = exp.transferHandler.bufferLimiter()
				val taken = imp.transferHandler.extractFromBuffer(lim)
				if (taken > 0) {
					exp.transferHandler.buffer += taken
				}
			}
		}

		requestedEnergy = 0
		for (com in components) {
			com.transferHandler.stop()

			if (com is PhasoriteExporterEntity) {
				if (requestedEnergy == Long.MAX_VALUE) continue
				if (com.transferHandler.requestedLimit == Long.MAX_VALUE) requestedEnergy = Long.MAX_VALUE
				else requestedEnergy += com.transferHandler.requestedLimit
			}
		}

		statistics.onTick(server.tickCount)
	}

	private fun handleConnections(server: MinecraftServer) {
		var needsSorting = false
		while (connectionQueue.isNotEmpty()) {
			val conn = connectionQueue.poll()

			when (conn.status) {
				ConnectionStatus.Connect -> {
					components.add(conn.target)
					conn.target.connect(this)

					if (conn.target.ownerUuid != owner && !members.containsKey(conn.target.ownerUuid)) {
						val player = server.playerList.getPlayer(conn.target.ownerUuid)!!
						members[conn.target.ownerUuid] = NetworkUser(conn.target.ownerUuid, player.gameProfile.name)
					}

					needsSorting = true
				}

				ConnectionStatus.Disconnect -> {
					components.remove(conn.target)
					conn.target.disconnect()

					if (conn.target.ownerUuid != owner && components.find { c -> c.ownerUuid == conn.target.ownerUuid } == null) {
						members.remove(conn.target.ownerUuid)
					}

					needsSorting = true
				}
			}
		}
		if (needsSorting) components.sortWith(comparator)
	}

	fun saveAdditional(tag: CompoundTag) {
		tag.put(UUID_KEY, id)
		tag.putString(NetworkConstants.NAME, name)
		tag.putUUID(NetworkConstants.PN_OWNER, owner)
		tag.putInt(NetworkConstants.COLOR, color)
		tag.putString(NetworkConstants.PASSWORD, password)
		tag.putBoolean(NetworkConstants.PRIVATE, private)

		val membersList = ListTag()
		for ((_, user) in members) {
			membersList.add(CompoundTag().also { user.saveAdditional(it) })
		}
		tag.put(NetworkConstants.MEMBERS, membersList)
	}

	fun loadAdditional(tag: CompoundTag) {
		id = tag.get(UUID_KEY)
		name = tag.getString(NetworkConstants.NAME)
		owner = tag.getUUID(NetworkConstants.PN_OWNER)
		color = tag.getInt(NetworkConstants.COLOR)
		password = tag.getString(NetworkConstants.PASSWORD)
		private = tag.getBoolean(NetworkConstants.PRIVATE)

		val membersList = tag.getList(NetworkConstants.MEMBERS, 10)
		if (membersList.isEmpty()) return

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
			components.map { it.toRawData() },
			if (includeExtra) PNEndecsData.ExtraNetworkData(
				filterComponents<PhasoriteImporterEntity>(ComponentType.IMPORTER).size,
				filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER).size,
				statistics.inputEnergy,
				statistics.exportEnergy
			) else null
		)
	}
}

enum class ConnectionStatus {
	Connect,
	Disconnect
}

data class Connection(val status: ConnectionStatus, val target: PhasoriteComponentEntity)