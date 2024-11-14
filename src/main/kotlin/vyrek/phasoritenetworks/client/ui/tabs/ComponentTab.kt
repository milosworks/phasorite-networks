package vyrek.phasoritenetworks.client.ui.tabs

import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Sizing
import vyrek.phasoritenetworks.client.ui.*

fun parseStr(str: String): Int {
	val s = if (str.contains('-')) "-${str.filter { it.isDigit() }}" else str
	return (s.toDoubleOrNull() ?: 0).toInt()
}

fun filterStrToInt(str: String, allowNegative: Boolean) = str.filter { it.isDigit() || (allowNegative && it == '-') }

class ComponentTab(screen: UIScreen) : BaseTab(screen) {
	override var height = 201

	override fun build(root: FlowLayout) {
		root.textArea("text-area:component-name")
			.apply {
				focusGained().subscribe {
					if (!isBlockOwner()) return@subscribe

					if (value == menu.defaultName) text("")
				}
				focusLost().subscribe {
					if (value == "") {
						if (!isBlockOwner()) return@subscribe

						text(menu.defaultName)

						menu.name = ""
						menu.updateComponentData()
					}
				}
				onChanged().subscribe {
					if (!isBlockOwner()) {
						text(menu.name.takeIf { it.isNotEmpty() } ?: menu.defaultName)
						return@subscribe
					}

					if (value != menu.defaultName && value != "" && menu.name != value) {
						menu.name = value
						menu.updateComponentData()
					}
				}
			}

		root.textArea("text-area:component-limit").apply {
			onChanged().subscribe {
				if (!canPlayerEdit()) {
					text(formatStr(menu.limit))

					return@subscribe
				}

				val result = filterStrToInt(value, false).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.limit = result
				menu.updateComponentData()

				text(formatStr(result))
			}
		}

		root.textArea("text-area:component-priority").apply {
			onChanged().subscribe { preValue ->
				if (!canPlayerEdit()) {
					text(formatStr(menu.priority))

					return@subscribe
				}

				var value = preValue
				if (!value.contains('-') && value.length == 4) value = value.take(3)

				val result = filterStrToInt(value, true).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.priority = result
				menu.updateComponentData()

				text(formatStr(result))
			}
		}

		root.checkbox("checkbox:component-override").apply {
			onChanged { value ->
				if (!isBlockOwner()) {
					checked(menu.overrideMode)

					return@onChanged
				}

				menu.overrideMode = value
				menu.updateComponentData()
			}
		}


		root.checkbox("checkbox:component-limitless").apply {
			onChanged { value ->
				if (!isBlockOwner()) {
					checked(menu.limitlessMode)

					return@onChanged
				}

				menu.limitlessMode = value
				menu.updateComponentData()
			}
		}

		val entity = menu.player.level().getBlockEntity(menu.pos)!!
		val item = entity.blockState.block.asItem().defaultInstance.also { i ->
			entity.saveToItem(i, menu.player.level().registryAccess())
		}

		root.flowLayout("flow-layout:container-item").apply {
			child(Components.item(item).sizing(Sizing.fill(), Sizing.fill()))
		}
	}
}