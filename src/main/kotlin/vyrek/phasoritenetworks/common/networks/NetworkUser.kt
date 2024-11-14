package vyrek.phasoritenetworks.common.networks

import net.minecraft.nbt.CompoundTag
import vyrek.phasoritenetworks.networking.PNEndecsData
import java.util.*

enum class NetworkUserAccess {
	MEMBER,
	ADMIN
}

class NetworkUser(var uuid: UUID, val name: String, var access: NetworkUserAccess = NetworkUserAccess.MEMBER) {
	constructor(tag: CompoundTag) : this(
		tag.getUUID(NetworkConstants.USER),
		tag.getString(NetworkConstants.NAME),
		NetworkUserAccess.entries[tag.getInt(NetworkConstants.MEMBER_TYPE)]
	)

	fun saveAdditional(tag: CompoundTag) {
		tag.putUUID(NetworkConstants.USER, uuid)
		tag.putString(NetworkConstants.NAME, name)
		tag.putInt(NetworkConstants.MEMBER_TYPE, access.ordinal)
	}

	fun toClientData(): PNEndecsData.ClientUserData {
		return PNEndecsData.ClientUserData(
			uuid,
			name,
			access.ordinal
		)
	}
}