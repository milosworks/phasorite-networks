package xyz.milosworks.phasoritenetworks.common.networks

object NetworkConstants {
	const val DEFAULT_PRIORITY = 1
	const val DEFAULT_LIMIT = 100_000

	const val ID = "network_id"
	const val NAME = "name"
	const val COMPONENT_NAME = "component_name"
	const val PRIORITY = "priority"
	const val OVERRIDE_MODE = "override_mode"
	const val RAW_LIMIT = "raw_limit"
	const val LIMITLESS_MODE = "limitless_mode"
	const val PN_OWNER = "pn_owner"
	const val USER = "user_uuid"
	const val COLOR = "color"
	const val MEMBERS = "members"
	const val PASSWORD = "password"
	const val PRIVATE = "private"
	const val THROUGHPUT = "throughput"
	const val NETWORKS = "networks"
	const val MEMBER_TYPE = "member_type"
	const val TRANSFER = "transfer"
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