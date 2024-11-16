package vyrek.phasoritenetworks.client.ui

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
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.client.ui.tabs.*
import vyrek.phasoritenetworks.common.networks.ComponentType
import java.text.NumberFormat
import kotlin.math.abs
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import io.wispforest.owo.ui.core.Component as OwoComponent

val units = arrayOf("FE/t", "kFE/t", "MFE/t", "GFE/t", "TFE/t", "PFE/t", "EFE/t")

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

fun formatStr(num: Long): String = NumberFormat.getNumberInstance().format(num)
fun formatStr(num: Int) = formatStr(num.toLong())
fun parseLong(num: Long): String {
	var result = num.toDouble()
	var unitIndex = 0

	while (result >= 1000 && unitIndex < units.size - 1) {
		result /= 1000
		unitIndex++
	}

	return String.format("%.${if (abs(result % 1) < 0.01) 0 else 2}f%s", result, units[unitIndex])
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
	companion object {
		/**
		 * Id of the tabs linked to their class.
		 */
		val buttonMappings = arrayOf(
			Tabs.COMPONENT to ::ComponentTab,
			Tabs.NETWORK to ::NetworkTab,
			Tabs.STATISTICS to ::StatisticsTab,
			Tabs.COMPONENTS to ::ComponentsTab,
			Tabs.MEMBERS to ::MembersTab
		)

		/**
		 * Tabs that can be shown when the component doesn't have a network.
		 */
		val tabsWithoutNetwork = arrayOf(
			Tabs.COMPONENT,
			Tabs.NETWORK
		)
	}

	val rootComponent: FlowLayout get() = uiAdapter.rootComponent
	val uiModel: UIModel get() = model
	var activeTab = Tabs.COMPONENT

	/**
	 * The tab that is shown when you open the screen
	 */
	override fun build(root: FlowLayout) {
		buildTab(root, Tabs.COMPONENT, ::ComponentTab)
	}

	/**
	 * Build a tab from the tabs directory.
	 * @param root The root component of the current screen.
	 * @param tab The tab to build from the enum.
	 * @param tabClass The tab class from the tabs directory.
	 * @see BaseTab
	 */
	fun buildTab(
		root: FlowLayout,
		tab: Tabs,
		tabClass: KFunction1<UIScreen, BaseTab>,
	) {
		activeTab = tab
		root.clearChildren()

		val instance = tabClass(this)

		buildSideTabs(root, instance.height)
		root.child(
			model.expandTemplate(
				FlowLayout::class,
				"tab:${tab.name.lowercase().replace('_', '-')}",
				generateTemplateParams(tab)
			)
		)
		buildEmptySideTabs(root)
		instance.build(root)
	}

	/**
	 * Generate the template params for the specified tab.
	 */
	fun generateTemplateParams(tab: Tabs): MutableMap<String, String> {
		return when (tab) {
			Tabs.COMPONENT -> mutableMapOf(
				"name" to menu.name.ifEmpty { menu.defaultName },
				"limit" to formatStr(menu.limit),
				"limitlessMode" to menu.limitlessMode.toString(),
				"priority" to formatStr(menu.priority),
				"overrideMode" to menu.overrideMode.toString(),
				"id" to menu.blockId,
				"throughput" to if (menu.clientEntity.componentType == ComponentType.EXPORTER) "-" else "+" +
						parseLong(menu.throughput),
				"rawThroughput" to formatStr(menu.throughput),
				"throughputColor" to Color.ofArgb((if (menu.clientEntity.componentType == ComponentType.EXPORTER) ComponentsTab.EXPORTER_COLOR else ComponentsTab.IMPORTER_COLOR).toInt())
					.asHexString(false)
			)

			Tabs.NETWORK -> mutableMapOf(
				"name" to (menu.network?.name ?: "No Selected Network"),
				"id" to (menu.network?.id?.toString() ?: ""),
				"color" to (if (menu.network != null) Color.ofArgb(menu.network!!.color)
					.asHexString(true) else "#7D7D7D")
			)

			Tabs.NETWORK_CREATE, Tabs.NETWORK_EDIT -> {
				val network = menu.network.takeIf { tab != Tabs.NETWORK_CREATE }

				mutableMapOf(
					"name" to (network?.name ?: "${menu.player().name.getString(12)}'s Network"),
					"private" to (network?.private?.toString() ?: "true"),
					"password" to (network?.password ?: ""),
					"color" to (network?.color?.let { Color.ofArgb(it).asHexString(true) } ?: "#00000000"),
					"formType" to activeTab.name.split("_")[1].lowercase()
						.replaceFirstChar { c -> c.uppercase() }
				)
			}

			Tabs.STATISTICS -> {
				val extra = menu.network!!.extra!!

				mutableMapOf(
					"importers" to extra.importers.toString(),
					"exporters" to extra.exporters.toString(),
					"input" to parseLong(extra.importedEnergy),
					"rawInput" to formatStr(extra.importedEnergy),
					"output" to parseLong(extra.exportedEnergy),
					"rawOutput" to formatStr(extra.exportedEnergy)
				)
			}

			else -> mutableMapOf()
		}
	}

	/**
	 * Side buttons for all tabs on all tabs.
	 */
	private fun buildSideTabs(root: FlowLayout, vertical: Int) {
		root.child(
			model.expandTemplate(FlowLayout::class, "side-tabs")
				.sizing(Sizing.content(), Sizing.fixed(vertical - SIDE_BUTTONS_OFFSET))
		)

		buttonMappings.forEach { (tab, tabClass) ->
			root.button("button:${tab.name.lowercase()}").apply {
				onPress { if (!activeTab.isNetworkSubtab()) buildTab(root, tab, tabClass) }

				if (menu.network == null && tab !in tabsWithoutNetwork)
					root.flowLayout("flow-layout:side-tabs").removeChild(this)
			}
		}
	}

	/**
	 * Specific vertical height empty container to center the tab main panel.
	 */
	private fun buildEmptySideTabs(component: FlowLayout) {
		component.child(Containers.verticalFlow(Sizing.fixed(20), Sizing.content()))
	}

	/**
	 * Renders constant throughput of the component if in the component tab.
	 */
	override fun render(vanillaContext: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(vanillaContext, mouseX, mouseY, delta)

		when (activeTab) {
			Tabs.COMPONENT -> {
				rootComponent.label("label:throughput")
					.text(
						Component.literal(
							"${if (menu.clientEntity.componentType == ComponentType.EXPORTER) "-" else "+"}${
								parseLong(
									menu.throughput
								)
							}"
						)
					)
					.tooltip(Component.literal("${formatStr(menu.throughput)} FE/t"))
			}

			else -> {}
		}
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (keyCode == 69) return false

		return super.keyPressed(keyCode, scanCode, modifiers)
	}
}