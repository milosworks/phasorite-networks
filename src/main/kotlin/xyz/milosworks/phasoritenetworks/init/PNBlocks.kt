package xyz.milosworks.phasoritenetworks.init

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.milosworks.phasoritenetworks.PhasoriteNetworks
import xyz.milosworks.phasoritenetworks.block.*
import xyz.milosworks.phasoritenetworks.common.components.PhasoriteComponentItem
import net.minecraft.world.item.Item.Properties as ItemProperties
import net.minecraft.world.level.block.state.BlockBehaviour.Properties.of as props

object PNBlocks {
	private val BLOCKS: DeferredRegister<Block> = DeferredRegister.createBlocks(PhasoriteNetworks.ID)

	val PHASORITE_EXPORTER by block(
		"phasorite_exporter",
		::PhasoriteComponentItem,
	) { PhasoriteExporterBlock(Properties.PHASORITE_COMPONENT) }
	val PHASORITE_IMPORTER by block(
		"phasorite_importer",
		::PhasoriteComponentItem
	) { PhasoriteImporterBlock(Properties.PHASORITE_COMPONENT) }

	val PHASORITE_BLOCK by block("phasorite_block") { PhasoriteBlock(Properties.PHASORITE_BLOCK) }
	val BUDDING_PHASORITE_BLOCK by block("budding_phasorite") { BuddingPhasoriteBlock(Properties.PHASORITE_BLOCK.randomTicks()) }

	val SMALL_PHASORITE_BUD by block("small_phasorite_bud") {
		PhasoriteClusterBlock(
			3.0,
			4.0,
			Properties.PHASORITE_CLUSTERS.sound(SoundType.SMALL_AMETHYST_BUD).lightLevel { 2 })
	}
	val MEDIUM_PHASORITE_BUD by block("medium_phasorite_bud") {
		PhasoriteClusterBlock(
			4.0,
			3.0,
			Properties.PHASORITE_CLUSTERS.sound(SoundType.MEDIUM_AMETHYST_BUD).lightLevel { 3 })
	}
	val LARGE_PHASORITE_BUD by block("large_phasorite_bud") {
		PhasoriteClusterBlock(
			5.0, 3.0, Properties.PHASORITE_CLUSTERS.sound(SoundType.LARGE_AMETHYST_BUD).lightLevel { 5 }
		)
	}
	val PHASORITE_CLUSTER by block("phasorite_cluster") {
		PhasoriteClusterBlock(
			7.0,
			3.0,
			Properties.PHASORITE_CLUSTERS.sound(SoundType.AMETHYST_CLUSTER).lightLevel { 6 })
	}
	val CHARGED_PHASORITE_CLUSTER by block("charged_phasorite_cluster") {
		ChargedPhasoriteClusterBlock(
			7.0,
			3.0,
			Properties.PHASORITE_CLUSTERS.sound(SoundType.AMETHYST_CLUSTER).lightLevel { 8 })
	}

	private fun <T : Block> block(
		id: String,
		itemSupplier: ((T, ItemProperties) -> BlockItem)? = { block, props -> BlockItem(block, props) },
		supplier: () -> T
	): DeferredHolder<Block, T> {
		val blockHolder = BLOCKS.register(id, supplier)

		PNItems.ITEMS.register(id) { ->
			val block = blockHolder.get()

			return@register itemSupplier?.invoke(block, ItemProperties()) ?: BlockItem(block, ItemProperties())
		}

		return blockHolder
	}

	object Properties {
		val PHASORITE_COMPONENT = props().mapColor(MapColor.COLOR_BLACK).strength(1.5F).explosionResistance(10.0F)
			.sound(SoundType.DEEPSLATE)
			.requiresCorrectToolForDrops()

		val PHASORITE_BLOCK = props().mapColor(MapColor.COLOR_BLACK).strength(2F)
			.sound(SoundType.AMETHYST)
			.requiresCorrectToolForDrops()

		val PHASORITE_CLUSTERS =
			props().mapColor(MapColor.COLOR_GRAY).strength(1.8F).forceSolidOn()
				.requiresCorrectToolForDrops()
	}

	fun init(event: IEventBus) {
		BLOCKS.register(event)
	}
}