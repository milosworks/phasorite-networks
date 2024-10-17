package vyrek.phasoritenetworks.common.networks

import io.wispforest.owo.ui.core.Color
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

	fun createNetwork(name: String, owner: UUID, color: Color): Network {
		val network = Network(name, owner, color)
		networks[network.id] = network

		return network
	}

	fun removeNetwork(id: Uuid) {
		networks.remove(id)
	}

	fun getNetwork(id: Uuid): Network? {
		return networks[id]
	}

	fun getNetwork(uid: UUID) {
		networks.filter { (_, v) -> v.owner == uid }
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
		for (i in 0..networksList.size) {
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
					Factory(this@Companion::create, { tag: CompoundTag, _: HolderLookup.Provider -> load(tag) }),
					"data"
				)
		}
	}
}