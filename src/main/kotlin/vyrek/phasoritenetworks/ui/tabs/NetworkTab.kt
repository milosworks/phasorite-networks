package vyrek.phasoritenetworks.ui.tabs

import io.wispforest.owo.ui.component.BoxComponent
import io.wispforest.owo.ui.component.ColorPickerComponent
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.network.chat.Component
import vyrek.phasoritenetworks.common.networks.NetworkUserAccess
import vyrek.phasoritenetworks.common.searchBy
import vyrek.phasoritenetworks.networking.PNEndecsData
import vyrek.phasoritenetworks.networking.PutType
import vyrek.phasoritenetworks.ui.*
import java.util.*
import kotlin.uuid.Uuid

enum class SortMethod {
	All,
	Owned
}

class NetworkTab(private val screen: UIScreen) : BaseTab(screen) {
	companion object {
		const val HEIGHT = 188
	}

	override fun build(root: FlowLayout) {
		val create = root.button("button:network-create").onPress {
			if (!activeTab.isNetworkSubtab()) buildNetworkTabForm(
				root,
				Tabs.NETWORK_CREATE,
				null
			)
		}

		val quantity = root.label("label:network-quantity")
		val scroll = root.flowLayout("flow-layout:container-scroll")
		scroll.clearChildren()

		val accessibleNetworks = menu.accessibleNetworks.filter { it.id != menu.network?.id }
			.also { quantity.text(Component.literal(it.size.toString())) }
		if (accessibleNetworks.isNotEmpty()) buildNetworkTabScroll(root, scroll, accessibleNetworks)

		val sortMethod = root.label("label:network-sort")
		val search = root.textArea("text-area:network-search").apply {
			onChanged().subscribe { v ->
				scroll.clearChildren()

				if (v.isEmpty()) {
					buildNetworkTabScroll(
						root,
						scroll,
						filterNetworks(
							accessibleNetworks,
							SortMethod.valueOf(sortMethod.text().string),
							userUuid = menu.player.uuid
						).also { quantity.text(Component.literal(it.size.toString())) }
					)
					return@subscribe
				}

				val filtered = filterNetworks(
					accessibleNetworks,
					SortMethod.valueOf(sortMethod.text().string),
					v,
					menu.player.uuid
				).also { quantity.text(Component.literal(it.size.toString())) }
				if (filtered.isEmpty()) return@subscribe

				buildNetworkTabScroll(root, scroll, filtered)
			}
		}

		sortMethod.textClickHandler {
			when (sortMethod.text().string) {
				SortMethod.All.name -> {
					sortMethod.text(Component.literal(SortMethod.Owned.name))

					scroll.clearChildren()
					buildNetworkTabScroll(
						root,
						scroll,
						filterNetworks(
							accessibleNetworks,
							SortMethod.Owned,
							search.value,
							menu.player.uuid
						).also { quantity.text(Component.literal(it.size.toString())) }
					)
				}

				SortMethod.Owned.name -> {
					sortMethod.text(Component.literal(SortMethod.All.name))

					scroll.clearChildren()
					buildNetworkTabScroll(
						root,
						scroll,
						filterNetworks(
							accessibleNetworks,
							SortMethod.All,
							search.value
						).also { quantity.text(Component.literal(it.size.toString())) }
					)
				}
			}

			true
		}

		val container = root.flowLayout("flow-layout:container-buttons")
		val emptyContainer = root.flowLayout("flow-layout:container-empty")
		val delete = root.button("button:network-delete").onPress {
			if (menu.network == null) return@onPress

			buildNetworkTabDelete(root)
		}
		val edit = root.button("button:network-edit")
		val disconnect = root.button("button:network-disconnect").onPress {
			if (menu.network == null) return@onPress

			menu.disconnectNetwork()
		}
		container.removeChild(delete)
		container.removeChild(edit)
		container.removeChild(disconnect)
//		emptyContainer.sizing(Sizing.fixed(0), Sizing.fixed(0))

		val network = menu.network ?: return
		container.removeChild(create)

		container.children(
			mutableListOf(
				delete,
				edit.onPress {
					buildNetworkTabForm(root, Tabs.NETWORK_EDIT, network)
				},
				disconnect,
				create
			).apply {
				takeIf { menu.player.uuid != network.owner }?.removeFirst()
				takeIf {
					menu.player.uuid != network.owner &&
							network.members[menu.player.uuid]?.access != NetworkUserAccess.ADMIN.ordinal
				}?.removeAt(1)
			}
		)
		emptyContainer.sizing(Sizing.fixed(16), Sizing.fixed(16))
	}

	private fun filterNetworks(
		networks: List<PNEndecsData.ClientNetworkData>,
		method: SortMethod,
		name: String? = null,
		userUuid: UUID? = null
	): List<PNEndecsData.ClientNetworkData> {
		val filteredNetworks = when (method) {
			SortMethod.All -> networks
			SortMethod.Owned -> userUuid?.let { uuid -> networks.filter { it.owner == uuid } } ?: emptyList()
		}

		return name?.let { filteredNetworks.searchBy(it) { data -> data.name } } ?: filteredNetworks
	}

	private fun buildNetworkTabScroll(
		root: FlowLayout,
		container: FlowLayout,
		list: List<PNEndecsData.ClientNetworkData>,
	) {
		for (n in list) {
			container.child(
				screen.uiModel.expandTemplate(
					FlowLayout::class, "tab:network:data", mutableMapOf(
						"name" to n.name,
						"id" to n.id.toString(),
						"color" to Color.ofArgb(n.color).asHexString(true)
					)
				)
			)

			container.button("button:${n.id}-connect").onPress {
				if (n.password != "") {
					buildNetworkTabPassword(root, n)
				} else {
					menu.connectNetwork(n.id, "")
				}
			}
		}
	}

	private fun buildNetworkTabDelete(component: FlowLayout) {
		activeTab = Tabs.NETWORK_DELETE

		component.child(
			popupWithBackground(
				"background:vanilla-translucent",
				screen.uiModel.expandTemplate(
					FlowLayout::class, "tab:network:delete", mutableMapOf("name" to menu.network!!.name)
				)
			)
		)

		component.button("button:close").onPress {
			screen.buildNetworkTab(component)
		}

		component.button("button:network-delete").onPress {
			menu.deleteNetwork()
		}
	}

	private fun buildNetworkTabPassword(component: FlowLayout, data: PNEndecsData.ClientNetworkData) {
		activeTab = Tabs.NETWORK_PASSWORD
		var tries = 0

		component.child(
			popupWithBackground(
				"background:vanilla-translucent",
				screen.uiModel.expandTemplate(FlowLayout::class, "tab:network:password")
			)
		)

		component.button("button:close").onPress {
			screen.buildNetworkTab(component)
		}

		val pwd = component.textArea("text-area:network-password")

		component.button("button:network-enter").onPress {
			if (tries >= 3) return@onPress screen.buildNetworkTab(component)

			if (pwd.value != data.password) {
				tries++
				return@onPress displayError(component, "Incorrect password, try again. Tries left: ${3 - tries}")
			}

			screen.buildNetworkTab(component)

			menu.connectNetwork(data.id, pwd.value)
		}
	}

	private fun buildNetworkTabForm(component: FlowLayout, tab: Tabs, data: PNEndecsData.ClientNetworkData?) {
		activeTab = tab

		component.child(
			screen.uiModel.expandTemplate(FlowLayout::class, "background:vanilla-translucent").also {
				it.flowLayout("flow-layout:container-background")
					.child(
						screen.uiModel.expandTemplate(
							FlowLayout::class, "tab:network:form", mutableMapOf(
								"name" to (data?.name ?: "${menu.player().name.getString(12)}'s Network"),
								"private" to (data?.private?.toString() ?: "true"),
								"password" to (data?.password ?: ""),
								"color" to if (data != null)
									String.format(
										"#%08X",
										(data.color and 0xFFFFFFFF.toInt())
									)
								else "#00000000",
								"form_type" to activeTab.name.split("_")[1].lowercase()
									.replaceFirstChar { c -> c.uppercase() }
							)
						)
					)
			}
		)

		component.button("button:close").onPress {
			screen.buildNetworkTab(component)
		}

		val name = component.textArea("text-area:network-name")
			.also {
				it.onChanged().subscribe { t ->
					if (t.length > 4) clearError(component)
				}
			}

		val passwordContainer = component.flowLayout("flow-layout:container-password")
		val collapsible = component.childById(CollapsibleContainer::class, "collapsible:container-password").also {
			it.onToggled().subscribe { toggle ->
				if (data != null && data.password != "" && toggle) {
					component.textArea("text-area:network-password").apply {
						text(data.password)
					}
				}
			}
		}
		val passwordContainerChildren = passwordContainer.children().toList()
		if (data == null || data.private) passwordContainer.clearChildren()

		component.checkbox("checkbox:network-private").onChanged { value ->
			if (!value) passwordContainer.children(passwordContainerChildren)
			else passwordContainer.clearChildren()
		}

		val colorPicker = component.childById(ColorPickerComponent::class, "color-picker:network-color").also {
			it.onChanged().subscribe { color ->
				component.childById(BoxComponent::class, "box:network-color").color(color).fill(true)
			}
		}
		if (data != null) colorPicker.selectedColor(Color.ofArgb(data.color))

		component.button("button:form-type")
			.onPress {
				if (name.value.length <= 4) {
					displayError(component, "Name shouldn't be less or equal than 4.")
					return@onPress
				}

				val private = component.checkbox("checkbox:network-private")
				if (!collapsible.expanded()) collapsible.toggleExpansion()

				val pwd =
					if (private.selected()) "" else component.textArea("text-area:network-password").value

				menu.putNetwork(
					if (activeTab == Tabs.NETWORK_CREATE) PutType.CREATE else PutType.UPDATE,
					name.value,
					colorPicker.selectedColor(),
					private.selected(),
					pwd,
					id = if (data?.id != null) data.id else Uuid.NIL
				)

				screen.buildNetworkTab(component)
			}
	}
}