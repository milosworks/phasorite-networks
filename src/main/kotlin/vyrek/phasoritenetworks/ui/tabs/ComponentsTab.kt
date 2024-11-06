package vyrek.phasoritenetworks.ui.tabs

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Sizing
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.ComponentType
import vyrek.phasoritenetworks.ui.UIScreen
import vyrek.phasoritenetworks.ui.expandTemplate
import vyrek.phasoritenetworks.ui.flowLayout
import vyrek.phasoritenetworks.ui.label

class ComponentsTab(private val screen: UIScreen) : BaseTab(screen) {
	companion object {
		const val HEIGHT = 152
	}

	override fun build(root: FlowLayout) {
		val container = root.flowLayout("flow-layout:container-scroll")
		val entities =
			menu.network!!.components.map { menu.player.level().getBlockEntity(it)!! as PhasoriteComponentEntity }

		val quantity = root.label("label:component-quantity")
		val sortMethod = root.label("label:component-sort")

		buildComponentsTabScroll(container, entities)
	}

	private fun buildComponentsTabScroll(container: FlowLayout, list: List<PhasoriteComponentEntity>) {
		for ((i, component) in list.withIndex()) {
			container.child(
				screen.uiModel.expandTemplate(
					FlowLayout::class, "tab:network-components:data", mutableMapOf(
						"i" to "$i",
						"color" to (if (component.componentType == ComponentType.EXPORTER) "#612020" else "#2F6120"),
						"name" to component.name.ifEmpty { component.defaultName }
					)
				).apply {
					mouseDown().subscribe { _, _, _ ->
						//Outline component
						return@subscribe true
					}
				}
			)

			val item = component.blockState.block.asItem().defaultInstance.also { i ->
				component.saveToItem(i, menu.player.level().registryAccess())
			}
			container.flowLayout("flow-layout:container-item-$i").apply {
				child(Components.item(item).sizing(Sizing.fill(), Sizing.fill()))
			}
		}
	}
}