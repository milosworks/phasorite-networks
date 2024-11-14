package vyrek.phasoritenetworks.client.ui.tabs

import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.FlowLayout
import net.minecraft.network.chat.Component
import vyrek.phasoritenetworks.client.ui.UIScreen
import vyrek.phasoritenetworks.client.ui.flowLayout
import vyrek.phasoritenetworks.client.ui.label
import vyrek.phasoritenetworks.client.ui.textArea

fun <E : Enum<E>> E.next(): E {
	val entries = this.declaringJavaClass.enumConstants
	return entries[(this.ordinal + 1) % entries.size]
}

abstract class BaseScrollTab<E : Enum<E>, T>(screen: UIScreen) : BaseTab(screen) {
	abstract var sortedBy: E
	open var searchBy: String? = null

	abstract val scrollData: List<T>

	abstract fun filterData(): List<T>

	lateinit var scroll: FlowLayout
	private lateinit var quantity: LabelComponent

	override fun build(root: FlowLayout) {
		scroll = root.flowLayout("flow-layout:container-scroll")
		quantity = root.label("label:quantity").apply {
			text(Component.literal(scrollData.size.toString()))
		}
		root.label("label:sort").apply {
			textClickHandler {
				scroll.clearChildren()
				sortedBy = sortedBy.next()

				text(Component.literal("§n${sortedBy.name}"))

				buildScroll()

				return@textClickHandler true
			}
		}
		root.textArea("text-area:search").apply {
			onChanged().subscribe { v ->
				scroll.clearChildren()

				searchBy = v.ifEmpty { null }

				buildScroll()
			}
		}

		buildScroll()
	}

	fun buildScroll() {
		scroll.clearChildren()

		filterData().also {
			quantity.text(Component.literal(it.size.toString()))
		}.forEachIndexed { index, data ->
			buildScrollData(index, data)
		}
	}

	abstract fun buildScrollData(i: Int, data: T)
}