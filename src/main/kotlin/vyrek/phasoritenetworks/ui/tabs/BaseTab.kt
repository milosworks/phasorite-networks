package vyrek.phasoritenetworks.ui.tabs

import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import net.minecraft.network.chat.Component
import vyrek.phasoritenetworks.ui.*

abstract class BaseTab(private val screen: UIScreen) {
	val menu: UIMenu get() = screen.menu
	var activeTab
		get() = screen.activeTab
		set(value) {
			screen.activeTab = value
		}

	abstract fun build(root: FlowLayout)

	fun popupWithBackground(backgroundId: String, popup: FlowLayout): FlowLayout {
		return screen.uiModel.expandTemplate(FlowLayout::class, backgroundId).apply {
			flowLayout("flow-layout:container-background").child(popup)
		}
	}

	fun displayError(component: FlowLayout, txt: String) {
		component.childById(LabelComponent::class, "label:error")
			.text(Component.literal(txt))
			.margins(Insets.top(5).withBottom(5))

	}

	fun clearError(component: FlowLayout) {
		component.childById(LabelComponent::class, "label:error")
			.text(Component.empty())
			.margins(Insets.none())
	}
}