package vyrek.phasoritenetworks.ui

import io.wispforest.owo.ui.base.BaseUIModelHandledScreen
import io.wispforest.owo.ui.base.BaseUIModelScreen.DataSource
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.CheckboxComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.component.TextAreaComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.ui.tabs.ComponentTab
import vyrek.phasoritenetworks.ui.tabs.ComponentsTab
import vyrek.phasoritenetworks.ui.tabs.NetworkTab
import vyrek.phasoritenetworks.ui.tabs.StatisticsTab
import java.text.NumberFormat
import kotlin.reflect.KClass
import io.wispforest.owo.ui.core.Component as OwoComponent

val units = arrayOf("FE/t", "kFE/t", "MFE/t", "GFE/t", "TFE/t", "PFE/t")

fun <T : OwoComponent> ParentComponent.childById(clazz: KClass<T>, id: String): T = this.childById(clazz.java, id)
fun <T : OwoComponent> UIModel.expandTemplate(
	expectedClass: KClass<T>,
	name: String,
	parameters: Map<String, String> = mutableMapOf()
): T = this.expandTemplate(expectedClass.java, name, parameters)

fun ParentComponent.textArea(id: String): TextAreaComponent = this.childById(TextAreaComponent::class, id)
fun ParentComponent.checkbox(id: String): CheckboxComponent = this.childById(CheckboxComponent::class, id)
fun ParentComponent.flowLayout(id: String): FlowLayout = this.childById(FlowLayout::class, id)
fun ParentComponent.button(id: String): ButtonComponent = this.childById(ButtonComponent::class, id)
fun ParentComponent.label(id: String): LabelComponent = this.childById(LabelComponent::class, id)

fun formatStr(num: Int): String = NumberFormat.getNumberInstance().format(num)

fun parseDouble(num: Double): String {
	var result = num
	var unitIndex = 0

	while (result >= 1000 && unitIndex < units.size - 1) {
		result /= 1000
		unitIndex++
	}

	return String.format("%.${if (result % 1 == 0.0) 0 else 2}f%s", result, units[unitIndex])
}

enum class Tabs {
	COMPONENT,
	NETWORK,
	NETWORK_DELETE,
	NETWORK_CREATE,
	NETWORK_EDIT,
	NETWORK_PASSWORD,
	STATISTICS,
	COMPONENTS,
	MEMBERS;

	fun isNetworkSubtab(): Boolean {
		return this == NETWORK_CREATE || this == NETWORK_EDIT || this == NETWORK_PASSWORD || this == NETWORK_DELETE
	}
}

const val SIDE_BUTTONS_OFFSET = 10

class UIScreen(menu: UIMenu, inventory: Inventory, title: Component) :
	BaseUIModelHandledScreen<FlowLayout, UIMenu>(
		menu,
		inventory,
		title,
		FlowLayout::class.java,
		DataSource.asset(PhasoriteNetworks.id("ui"))
	) {
	val uiModel: UIModel get() = model
	var activeTab = Tabs.COMPONENT

	override fun build(rootComponent: FlowLayout) {
		buildComponentTab(rootComponent)
	}

	private fun buildComponentTab(root: FlowLayout) {
		activeTab = Tabs.COMPONENT

		root.clearChildren()
		buildSideTabs(root, ComponentTab.HEIGHT)
		root.child(
			model.expandTemplate(
				FlowLayout::class, "tab:component", mutableMapOf(
					"name" to menu.name.ifEmpty { menu.defaultName },
					"limit" to formatStr(menu.limit),
					"limitlessMode" to menu.limitlessMode.toString(),
					"priority" to formatStr(menu.priority),
					"overrideMode" to menu.overrideMode.toString(),
					"id" to menu.blockId
				)
			)
		)
		buildEmptySideTabs(root)

		ComponentTab(this).build(root)
	}

	fun buildNetworkTab(root: FlowLayout) {
		activeTab = Tabs.NETWORK

		root.clearChildren()
		buildSideTabs(root, NetworkTab.HEIGHT)
		root.child(
			model.expandTemplate(
				FlowLayout::class, "tab:network", mutableMapOf(
					"name" to (menu.network?.name ?: "No Selected Network"),
					"id" to (menu.network?.id?.toString() ?: ""),
					"color" to (if (menu.network != null) Color.ofArgb(menu.network!!.color)
						.asHexString(true) else "#7D7D7D")
				)
			)
		)
		buildEmptySideTabs(root)

		NetworkTab(this).build(root)
	}

	private fun buildNetworkStatisticsTab(root: FlowLayout) {
		activeTab = Tabs.STATISTICS

		val network = menu.network ?: return
		val extra = network.extra ?: return

		root.clearChildren()
		buildSideTabs(root, StatisticsTab.HEIGHT)
		root.child(
			model.expandTemplate(
				FlowLayout::class, "tab:network-statistics", mutableMapOf(
					"importers" to extra.importers.toString(),
					"exporters" to extra.exporters.toString(),
					"input" to parseDouble(extra.importedEnergy.toDouble()),
					"output" to parseDouble(extra.exportedEnergy.toDouble())
				)
			)
		)
		buildEmptySideTabs(root)

		StatisticsTab(this).build(root)
	}

	private fun buildNetworkComponentsTab(root: FlowLayout) {
		activeTab = Tabs.COMPONENTS

		if (menu.network == null) return

		root.clearChildren()
		buildSideTabs(root, ComponentsTab.HEIGHT)
		root.child(
			model.expandTemplate(
				FlowLayout::class, "tab:network-components"
			)
		)
		buildEmptySideTabs(root)

		ComponentsTab(this).build(root)
	}

	private fun buildNetworkMembersTab(root: FlowLayout) {

	}

	private fun buildSideTabs(component: FlowLayout, vertical: Int) {
		component.child(
			model.expandTemplate(FlowLayout::class, "side-tabs")
				.sizing(Sizing.content(), Sizing.fixed(vertical - SIDE_BUTTONS_OFFSET))
		)

		component.childById(ButtonComponent::class, "button:component-tab").onPress {
			if (activeTab.isNetworkSubtab()) return@onPress

			buildComponentTab(component)
		}

		component.childById(ButtonComponent::class, "button:network-tab").onPress {
			if (activeTab.isNetworkSubtab()) return@onPress

			buildNetworkTab(component)
		}

		component.childById(ButtonComponent::class, "button:network-statistics-tab").run {
			onPress {
				if (activeTab.isNetworkSubtab()) return@onPress

				buildNetworkStatisticsTab(component)
			}

			if (menu.network == null)
				component.childById(FlowLayout::class, "flow-layout:side-tabs").removeChild(this)
		}

		component.childById(ButtonComponent::class, "button:network-components").run {
			onPress {
				if (activeTab.isNetworkSubtab()) return@onPress

				buildNetworkComponentsTab(component)
			}

			if (menu.network == null)
				component.childById(FlowLayout::class, "flow-layout:side-tabs").removeChild(this)
		}

		component.childById(ButtonComponent::class, "button:network-members").run {
			onPress {
				if (activeTab.isNetworkSubtab()) return@onPress

				buildNetworkMembersTab(component)
			}

			if (menu.network == null)
				component.childById(FlowLayout::class, "flow-layout:side-tabs").removeChild(this)
		}
	}

	private fun buildEmptySideTabs(component: FlowLayout) {
		component.child(Containers.verticalFlow(Sizing.fixed(20), Sizing.content()))
	}

	fun updateNetworkTab() {
		buildNetworkTab(uiAdapter.rootComponent)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (keyCode == 69) return false

		return super.keyPressed(keyCode, scanCode, modifiers)
	}
}