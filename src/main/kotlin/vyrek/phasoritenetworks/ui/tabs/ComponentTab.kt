package vyrek.phasoritenetworks.ui.tabs

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Sizing
import vyrek.phasoritenetworks.ui.*

fun parseStr(str: String): Int {
	val s = if (str.contains('-')) "-${str.filter { it.isDigit() }}" else str
	return (s.toDoubleOrNull() ?: 0).toInt()
}

fun filterStrToInt(str: String, allowNegative: Boolean) = str.filter { it.isDigit() || (allowNegative && it == '-') }

class ComponentTab(screen: UIScreen) : BaseTab(screen) {
	companion object {
		const val HEIGHT = 201
	}

	override fun build(root: FlowLayout) {
		root.textArea("text-area:component-name")
			.also { name ->
				name.focusGained().subscribe {
					if (name.value == menu.defaultName) name.text("")
				}
				name.focusLost().subscribe {
					if (name.value == "") {
						name.text(menu.defaultName)

						menu.name = ""
						menu.updateComponentData()
					}
				}
				name.onChanged().subscribe {
					if (name.value != menu.defaultName && name.value != "" && menu.name != name.value) {
						menu.name = name.value
						menu.updateComponentData()
					}
				}
			}

		root.textArea("text-area:component-limit").also { com ->
			com.onChanged().subscribe {
				val result = filterStrToInt(com.value, false).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.limit = result
				menu.updateComponentData()

				com.text(formatStr(result))
			}
		}

		root.textArea("text-area:component-priority").also { com ->
			com.onChanged().subscribe { preValue ->
				var value = preValue
				if (!value.contains('-') && value.length == 4) value = value.take(3)

				val result = filterStrToInt(value, true).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.priority = result
				menu.updateComponentData()

				com.text(formatStr(result))
			}
		}

		root.checkbox("checkbox:component-override")
			.onChanged { value ->
				menu.overrideMode = value
				menu.updateComponentData()
			}

		root.checkbox("checkbox:component-limitless")
			.onChanged { value ->
				menu.limitlessMode = value
				menu.updateComponentData()
			}

		val entity = menu.player.level().getBlockEntity(menu.pos)!!
		val item = entity.blockState.block.asItem().defaultInstance.also { i ->
			entity.saveToItem(i, menu.player.level().registryAccess())
		}

		// It wasnt working (syncing) but it does now idk, no touchy.
		root.flowLayout("flow-layout:container-item").apply {
			child(Components.item(item).sizing(Sizing.fill(), Sizing.fill()))
		}
	}
}