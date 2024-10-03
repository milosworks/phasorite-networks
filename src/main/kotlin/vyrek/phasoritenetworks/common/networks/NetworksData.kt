package vyrek.phasoritenetworks.common.networks

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.neoforge.server.ServerLifecycleHooks

class NetworksData : SavedData() {
	val networks: MutableList<Network> = mutableListOf()
	var nextId = 0

//	fun createNetwork(): Network {
//
//	}

	fun removeNetwork(id: Int) {
		networks.removeAll { it.id == id }
	}

	fun getNetwork(id: Int): Network? {
		return networks.find { it.id == id }
	}

	override fun save(
		tag: CompoundTag,
		provider: HolderLookup.Provider
	): CompoundTag {
		tag.putInt(NetworkConstants.NEXT_ID, nextId)

		val networksList = ListTag()
		for (network in networks) {
			val networkTag = CompoundTag()
			network.saveAdditional(networkTag)
			networksList.add(networkTag)
		}
		tag.put(NetworkConstants.NETWORKS, networksList)

		return tag
	}

	fun load(tag: CompoundTag) {
		nextId = tag.getInt(NetworkConstants.NEXT_ID)

		val networksList = tag.getList(NetworkConstants.NETWORKS, Tag.TAG_COMPOUND.toInt())
		for (i in 0..networksList.size) {
			val networkTag = networksList.getCompound(i)
			val network = Network()
			network.loadAdditional(networkTag)
			networks.add(network)
		}
	}

	companion object {
		fun create(): NetworksData {
			return NetworksData()
		}

		fun load(tag: CompoundTag): NetworksData {
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