package xyz.milosworks.phasoritenetworks.common.components

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import xyz.milosworks.phasoritenetworks.common.Translations
import xyz.milosworks.phasoritenetworks.init.PNComponents
import kotlin.uuid.Uuid

class PhasoriteComponentItem(block: Block, props: Properties) : BlockItem(block, props) {
	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val tag = stack.get(PNComponents.COMPONENT_DATA) ?: return super.appendHoverText(
			stack,
			context,
			tooltipComponents,
			tooltipFlag
		)

		if (tag.networkId != Uuid.NIL) {
			tooltipComponents.add(
				Component.literal("${Translations.NETWORK.string}: ").append(
					Component.literal(tag.networkName).setStyle(
						Style.EMPTY.withColor(tag.color)
					)
				)
			)
		}

		tooltipComponents.addAll(
			arrayOf(
				Component.literal("${Translations.LIMIT.string}: ${ChatFormatting.GRAY}${tag.rawLimit}"),
				Component.literal("${Translations.PRIORITY.string}: ${ChatFormatting.GRAY}${tag.priority}")
			)
		)
	}
}