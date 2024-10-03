package vyrek.phasoritenetworks.common.networks

object NetworkConstants {
	const val INVALID_NUM = -1

	const val MAX_NETWORK_NAME_LEN = 16
	const val MAX_PASSWORD_LEN = 12
	const val MAX_PRIORITY = 999

	const val MIN_PRIORITY = -999

	const val DEFAULT_PRIORITY = 1
	const val DEFAULT_LIMIT = 100000

	const val ID = "id"
	const val NAME = "name"
	const val PRIORITY = "priority"
	const val OVERRIDE_MODE = "override_mode"
	const val RAW_LIMIT = "raw_limit"
	const val LIMITLESS_MODE = "limitless_mode"
	const val OWNER = "owner"
	const val USER = "user_uuid"
	const val MEMBERS = "members"
	const val BUFFER = "buffer"
	const val NEXT_ID = "next_id"
	const val NETWORKS = "networks"
}

enum class ComponentType {
	INVALID,
	EXPORTER,
	IMPORTER
}

enum class DistributionMode {
	ROUND_ROBIN,
	FILL_FIRST
}