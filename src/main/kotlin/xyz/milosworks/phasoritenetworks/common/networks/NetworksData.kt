package xyz.milosworks.phasoritenetworks.common.networks

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*
import kotlin.uuid.Uuid

class NetworksData : SavedData() {
	val networks: MutableMap<Uuid, Network> = mutableMapOf()

	fun createNetwork(name: String, owner: UUID, color: Int, private: Boolean, password: String): Network {
		val network = Network(name, owner, color, private, password)
		network.id = Uuid.random()
		networks[network.id] = network

		setDirty()

		return network
	}

	fun deleteNetwork(id: Uuid): Boolean {
		val network = getNetwork(id) ?: return false

		for (component in network.components.toList()) {
			component.disconnect()
			network.components.remove(component)
		}
		if (network.components.size != 0) throw IllegalStateException("Network components aren't empty after technically disconnecting all of them.")

		networks.remove(id)

		return true
	}

	fun getNetwork(id: Uuid): Network? {
		return networks[id]
	}

	fun modifyNetwork(id: Uuid, edit: Network.() -> Unit): Network? {
		val network = getNetwork(id) ?: return null

		network.edit()

		setDirty()

		return network
	}

	override fun save(
		tag: CompoundTag,
		provider: HolderLookup.Provider
	): CompoundTag {
		val networksList = ListTag()
		for (network in networks.values) {
			val networkTag = CompoundTag()
			network.saveAdditional(networkTag)
			networksList.add(networkTag)
		}
		tag.put(NetworkConstants.NETWORKS, networksList)

		return tag
	}

	fun load(tag: CompoundTag) {
		val networksList = tag.getList(NetworkConstants.NETWORKS, Tag.TAG_COMPOUND.toInt())
		for (i in 0 until networksList.size) {
			val networkTag = networksList.getCompound(i)
			val network = Network()
			network.loadAdditional(networkTag)
			networks[network.id] = network
		}
	}

	companion object {
		fun create(): NetworksData {
			return NetworksData()
		}

		private fun load(tag: CompoundTag): NetworksData {
			val data = NetworksData()
			data.load(tag)
			return data
		}

		fun get(): NetworksData {
			val overworld =
				ServerLifecycleHooks.getCurrentServer()?.overworld()
					?: throw IllegalStateException("Overworld is not available. Server is in an invalid state.")

			return overworld.dataStorage
				.computeIfAbsent(
					Factory(this@Companion::create) { tag: CompoundTag, _: HolderLookup.Provider -> load(tag) },
					"data"
				)
		}
	}
}