package vyrek.phasoritenetworks.common.networks

import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.entity.PhasoriteExporterEntity
import vyrek.phasoritenetworks.entity.PhasoriteImporterEntity
import java.util.LinkedList
import java.util.Queue
import java.util.UUID

class Network(
	var id: Int = NetworkConstants.INVALID_NUM,
	var name: String = "",
	var owner: UUID = Util.NIL_UUID
) {
	val isValid: Boolean
		get() = id != NetworkConstants.INVALID_NUM

	val members: HashMap<UUID, NetworkUser> = HashMap()

	val components: MutableList<PhasoriteComponentEntity> = mutableListOf()

	val connectionQueue: Queue<PhasoriteComponentEntity> = LinkedList()

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
			conn.connect(this)
			components.add(conn)
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
		tag.putInt(NetworkConstants.ID, id)
		tag.putString(NetworkConstants.NAME, name)
		tag.putUUID(NetworkConstants.OWNER, owner)

		val membersList = ListTag()
		for ((_, user) in members) {
			val memberTag = CompoundTag()
			user.saveAdditional(memberTag)
			membersList.add(membersList)
		}
		tag.put(NetworkConstants.MEMBERS, membersList)
	}

	fun loadAdditional(tag: CompoundTag) {
		id = tag.getInt(NetworkConstants.ID)
		name = tag.getString(NetworkConstants.NAME)
		owner = tag.getUUID(NetworkConstants.OWNER)

		val membersList = tag.getList(NetworkConstants.MEMBERS, Tag.TAG_COMPOUND.toInt())
		for (i in 0..membersList.size) {
			val memberTag = membersList.getCompound(i)
			val member = NetworkUser(memberTag)
			members.put(member.uuid, member)
		}
	}
}

