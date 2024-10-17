package vyrek.phasoritenetworks.common.networks

import io.wispforest.owo.ui.core.Color
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class Network(
	var name: String = "",
	var owner: UUID = Util.NIL_UUID,
	var color: Color = Color.BLACK
) {
	var id: Uuid = Uuid.random()
	val isValid: Boolean
		get() = id != Uuid.NIL

	val members: HashMap<UUID, NetworkUser> = HashMap()

	val components: MutableList<PhasoriteComponentEntity> = mutableListOf()

	val connectionQueue: Queue<Connection> = LinkedList()

	var requestedEnergy = 0

	fun <T : PhasoriteComponentEntity> filterComponents(type: ComponentType): List<T> {
		@Suppress("UNCHECKED_CAST")
		return (components.filter { c ->
			c.componentType == type
		} as List<T>).sortedWith(
			compareByDescending<T> { it.overrideMode }
				.thenByDescending { it.priority }
		)
	}

	fun calcRequestedEnergy() {
		for (exporter in filterComponents<PhasoriteExporterEntity>(ComponentType.EXPORTER)) {
			requestedEnergy += exporter.transferHandler.updateRequestedLimit(exporter.limit)
		}
	}

	fun onTick() {
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
					conn.target.connect(this)
				}

				ConnectionStatus.Disconnect -> {
					components.remove(conn.target)
					conn.target.disconnect()
				}
			}
		}

		importers.forEach { importer ->
			importer.transferHandler.reset()
			importer.updateRequestedLimit()
		}

		exporters.forEach { exporter ->
			exporter.distributeEnergy()
			exporter.transferHandler.reset()
			exporter.updateRequestedLimit()
		}

		calcRequestedEnergy()
	}

	fun saveAdditional(tag: CompoundTag) {
		tag.putUUID(NetworkConstants.ID, id.toJavaUuid())
		tag.putString(NetworkConstants.NAME, name)
		tag.putUUID(NetworkConstants.OWNER, owner)
		tag.putInt(NetworkConstants.COLOR, color.argb())

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
		color = Color.ofArgb(tag.getInt(NetworkConstants.COLOR))

		val membersList = tag.getList(NetworkConstants.MEMBERS, Tag.TAG_COMPOUND.toInt())
		for (i in 0..membersList.size) {
			val memberTag = membersList.getCompound(i)
			val member = NetworkUser(memberTag)
			members.put(member.uuid, member)
		}
	}
}

enum class ConnectionStatus {
	Connect,
	Disconnect
}

data class Connection(val status: ConnectionStatus, val target: PhasoriteComponentEntity)