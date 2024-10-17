package vyrek.phasoritenetworks.ui

import io.wispforest.owo.ui.base.BaseUIModelHandledScreen
import io.wispforest.owo.ui.base.BaseUIModelScreen.DataSource
import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.commands.arguments.blocks.BlockStateParser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import vyrek.phasoritenetworks.PhasoriteNetworks
import java.text.NumberFormat
import kotlin.reflect.KClass
import io.wispforest.owo.ui.core.Component as OwoComponent

fun <T : OwoComponent> ParentComponent.childById(clazz: KClass<T>, id: String): T = this.childById(clazz.java, id)
fun <T : OwoComponent> UIModel.expandTemplate(
	expectedClass: KClass<T>,
	name: String,
	parameters: Map<String, String> = mutableMapOf()
): T = this.expandTemplate(expectedClass.java, name, parameters)

fun filterStrToInt(str: String, allowNegative: Boolean) = str.filter { it.isDigit() || (allowNegative && it == '-') }
fun parseStr(str: String): Int {
	val s = if (str.contains('-')) "-${str.filter { it.isDigit() }}" else str
	return (s.toDoubleOrNull() ?: 0).toInt()
}

fun formatStr(num: Int): String = NumberFormat.getNumberInstance().format(num)

class UIScreen(menu: UIMenu, inventory: Inventory, title: Component) :
	BaseUIModelHandledScreen<FlowLayout, UIMenu>(
		menu,
		inventory,
		title,
		FlowLayout::class.java,
		DataSource.asset(PhasoriteNetworks.identifier("ui"))
	) {
	private var createNetworkActive = false

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (keyCode == 69) return false

		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun build(rootComponent: FlowLayout) {
//		buildComponentTab(rootComponent)
		buildNetworkTab(rootComponent)
	}

	private fun buildComponentTab(component: FlowLayout) {
		component.clearChildren()

		component.child(
			model.expandTemplate(
				FlowLayout::class, "component_tab", mutableMapOf(
					"name" to menu.name,
					"limit" to formatStr(menu.limit),
					"priority" to formatStr(menu.priority),
					"id" to menu.id
				)
			)
		)

		component.childById(TextAreaComponent::class, "name")
			.also { name ->
				name.focusGained().subscribe {
					if (name.value == menu.defaultName) name.text("")
				}
				name.focusLost().subscribe {
					if (name.value == "") {
						name.text(menu.defaultName)

						menu.name = ""
						menu.updateEntityData()
					}
				}
				name.onChanged().subscribe {
					if (name.value != menu.defaultName && name.value != "" && menu.name != name.value) {
						menu.name = name.value
						menu.updateEntityData()
					}
				}
			}

		component.childById(TextAreaComponent::class, "limit").also { component ->
			component.onChanged().subscribe {
				val result = filterStrToInt(component.value, false).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.limit = result
				menu.updateEntityData()

				component.text(formatStr(result))
			}
		}

		component.childById(TextAreaComponent::class, "priority").also { component ->
			component.onChanged().subscribe {
				if (!component.value.contains('-')) component.setCharacterLimit(3)
				else component.setCharacterLimit(4)

				val result = filterStrToInt(component.value, true).let { filtered ->
					if (filtered.isNotEmpty()) parseStr(filtered) else 0
				}

				menu.priority = result
				menu.updateEntityData()

				component.text(formatStr(result))
			}
		}

		component.childById(CheckboxComponent::class, "override")
			.checked(menu.overrideMode)
			.also {
				it.onChanged { value ->
					menu.overrideMode = value
					menu.updateEntityData()
				}
			}

		component.childById(CheckboxComponent::class, "limitless")
			.checked(menu.limitlessMode)
			.also {
				it.onChanged { value ->
					menu.limitlessMode = value
					menu.updateEntityData()
				}
			}

		component.childById(FlowLayout::class, "block")
			.clearChildren()
			.child(
				Components.block(
					BlockStateParser.parseForBlock(
						BuiltInRegistries.BLOCK.asLookup(),
						menu.id,
						false
					).blockState
				).sizing(Sizing.fixed(40), Sizing.fixed(40))
			)
	}

	private fun buildNetworkTab(component: FlowLayout) {
		component.clearChildren()

		component.child(model.expandTemplate(FlowLayout::class, "network_tab"))

		component.childById(FlowLayout::class, "scroll-container")
			.surface(Surface.outline(0xFF7D7D7D.toInt()))
		component.childById(FlowLayout::class, "name-container")
			.surface(Surface.outline(0xFF7D7D7D.toInt()))

		component.childById(ButtonComponent::class, "create-button").onPress {
			if (!createNetworkActive) component.also {
				buildNetworkTabCreate(it)
				createNetworkActive = true
			}
		}
	}

	private fun buildNetworkTabCreate(component: FlowLayout) {
		println(menu.player().uuid)
		component.child(model.expandTemplate(FlowLayout::class, "network_tab_create"))

		component.childById(ButtonComponent::class, "close-create").onPress {
			buildNetworkTab(component)
			createNetworkActive = false
		}

		val colorPicker = component.childById(ColorPickerComponent::class, "color-picker").also {
			it.onChanged().subscribe { color ->
				component.childById(BoxComponent::class, "color-box").color(color).fill(true)
			}
		}

		component.childById(ButtonComponent::class, "button-create").onPress {
			val name = component.childById(TextAreaComponent::class, "network-name")
			menu.createNetwork(name.value, colorPicker.selectedColor())
		}

//		component.childById(DropdownComponent::class, "dropdown")
	}
}
