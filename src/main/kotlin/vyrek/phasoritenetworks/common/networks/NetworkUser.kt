package vyrek.phasoritenetworks.common.networks

import net.minecraft.nbt.CompoundTag
import java.util.UUID

class NetworkUser(var uuid: UUID, var name: String) {
	constructor(tag: CompoundTag) : this(tag.getUUID(NetworkConstants.USER), tag.getString(NetworkConstants.NAME))

	fun saveAdditional(tag: CompoundTag) {
		tag.putUUID(NetworkConstants.USER, uuid)
		tag.putString(NetworkConstants.NAME, name)
	}

	fun loadAdditional(tag: CompoundTag) {
		uuid = tag.getUUID(NetworkConstants.USER)
		name = tag.getString(NetworkConstants.NAME)
	}
}