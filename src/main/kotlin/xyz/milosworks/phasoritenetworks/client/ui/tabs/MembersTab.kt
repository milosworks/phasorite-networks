package xyz.milosworks.phasoritenetworks.client.ui.tabs

import io.wispforest.owo.ui.container.FlowLayout
import xyz.milosworks.phasoritenetworks.client.ui.*
import xyz.milosworks.phasoritenetworks.common.Translations
import xyz.milosworks.phasoritenetworks.common.networks.NetworkUserAccess
import xyz.milosworks.phasoritenetworks.networking.PNEndecsData

enum class MembersSortMethod {
	All,
	Admin,
	Member
}

class MembersTab(screen: UIScreen) : BaseScrollTab<MembersSortMethod, PNEndecsData.ClientUserData>(screen) {
	companion object {
		//		const val OWNER_COLOR = "#AB3428"
		const val ADMIN_COLOR = "#FD7E35"
		const val MEMBER_COLOR = "#A9A9A9"
	}

	override var sortedBy = MembersSortMethod.All
	override val scrollData = menu.network!!.members.values.toList().sortedWith(compareByDescending { it.access })
	override val height = 177

	override fun buildScrollData(i: Int, data: PNEndecsData.ClientUserData) {
		scroll.child(
			screen.uiModel.expandTemplate(
				FlowLayout::class, "tab:members:data", mutableMapOf(
					"i" to data.uuid.toString(),
					"username" to data.name,
					"uuid" to data.uuid.toString(),
					"access" to Translations.MAKE(
						Translations.relative(
							NetworkUserAccess.entries[data.access].name.lowercase()
								.replaceFirstChar { it.uppercaseChar() }
						)
					).string,
					"accessColor" to when (NetworkUserAccess.entries[data.access]) {
						NetworkUserAccess.ADMIN -> ADMIN_COLOR
						NetworkUserAccess.MEMBER -> MEMBER_COLOR
					}
				)
			).apply {
				mouseDown().subscribe { _, _, _ ->
					if (!canPlayerEdit()) return@subscribe true
					if (NetworkUserAccess.entries[data.access] == NetworkUserAccess.ADMIN &&
						menu.player.uuid != menu.network!!.owner
					) return@subscribe true

					buildManageTab(data)

					return@subscribe true
				}
			}
		)
	}

	private fun buildManageTab(data: PNEndecsData.ClientUserData) {
		val nextAccess = NetworkUserAccess.entries[data.access].next()

		screen.rootComponent.child(
			screen.uiModel.expandTemplate(FlowLayout::class, "background:vanilla-translucent").apply {
				flowLayout("flow-layout:container-background")
					.child(
						screen.uiModel.expandTemplate(
							FlowLayout::class, "tab:members:manage", mutableMapOf(
								"username" to data.name,
								"uuid" to data.uuid.toString(),
								"access" to NetworkUserAccess.entries[data.access].name.lowercase()
									.replaceFirstChar { it.uppercaseChar() },
								"accessColor" to when (NetworkUserAccess.entries[data.access]) {
									NetworkUserAccess.ADMIN -> ADMIN_COLOR
									NetworkUserAccess.MEMBER -> MEMBER_COLOR
								},
								"nextAccess" to nextAccess.name.lowercase().replaceFirstChar { it.uppercaseChar() }
							)
						)
					)
			}
		)

		val root = screen.rootComponent

		root.button("button:close").onPress {
			screen.buildTab(root, Tabs.MEMBERS, ::MembersTab)
		}

		root.button("button:kick").onPress {
			menu.kickFromNetwork(data.uuid)
		}

		root.button("button:ownership").onPress {
			menu.passOwnership(data.uuid)
		}.apply { if (menu.network!!.owner != menu.player.uuid) remove() }

		root.button("button:access").onPress {
			menu.setAccess(data.uuid, nextAccess)
		}.apply { if (menu.network!!.owner != menu.player.uuid) remove() }
	}

	override fun filterData(): List<PNEndecsData.ClientUserData> {
		val filteredData = when (sortedBy) {
			MembersSortMethod.All -> scrollData
			MembersSortMethod.Admin -> scrollData.filter { data -> data.access == NetworkUserAccess.ADMIN.ordinal }
			MembersSortMethod.Member -> scrollData.filter { data -> data.access == NetworkUserAccess.MEMBER.ordinal }
		}

//		searchBy?.let { filteredData.searchBy(it) {(_, data) -> data.name} }
		return filteredData
	}
}