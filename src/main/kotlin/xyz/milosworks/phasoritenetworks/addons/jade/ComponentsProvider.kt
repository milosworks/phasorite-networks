package xyz.milosworks.phasoritenetworks.addons.jade

import io.wispforest.owo.ui.core.Color
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.IServerDataProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig
import xyz.milosworks.phasoritenetworks.PhasoriteNetworks
import xyz.milosworks.phasoritenetworks.client.ui.formatStr
import xyz.milosworks.phasoritenetworks.client.ui.parseLong
import xyz.milosworks.phasoritenetworks.client.ui.tabs.ComponentsTab
import xyz.milosworks.phasoritenetworks.common.Translations
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentEntity
import xyz.milosworks.phasoritenetworks.common.networks.NetworkConstants
import xyz.milosworks.phasoritenetworks.init.PNBlocks

enum class ComponentsProvider : IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
		val data = accessor.serverData
		if (!data.contains(NetworkConstants.NAME)) return

		val op = if (accessor.block == PNBlocks.PHASORITE_EXPORTER) "-" else "+"
		val componentColor =
			if (accessor.block == PNBlocks.PHASORITE_EXPORTER) ComponentsTab.EXPORTER_COLOR else ComponentsTab.IMPORTER_COLOR
		val color = Color.ofArgb(data.getInt(NetworkConstants.COLOR))

		tooltip.add(
			Component.literal("${Translations.NETWORK.string}: "),
		)
		tooltip.append(
			Component.literal(data.getString(NetworkConstants.NAME)).withColor(color.rgb()),
		)

		val throughput =
			if (accessor.player.isShiftKeyDown) Translations.energySuffix(formatStr(data.getLong(NetworkConstants.THROUGHPUT))).string
			else parseLong(data.getLong(NetworkConstants.THROUGHPUT))
		tooltip.add(
			Component.literal("${Translations.NETWORK.string}: "),
		)
		tooltip.append(
			Component.literal("$op$throughput")
				.withColor(componentColor.toInt())
		)
	}

	override fun appendServerData(tag: CompoundTag, accessor: BlockAccessor) {
		val entity = accessor.blockEntity as PhasoriteComponentEntity
		if (!entity.network.isValid) return

		tag.putString(NetworkConstants.NAME, entity.network.name)
		tag.putInt(NetworkConstants.COLOR, entity.network.color)
		tag.putLong(NetworkConstants.THROUGHPUT, entity.transferHandler.throughput)
	}

	override fun getUid(): ResourceLocation {
		return PhasoriteNetworks.id("components_provider")
	}
}