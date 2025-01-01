package xyz.milosworks.phasoritenetworks.datagen

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import xyz.milosworks.phasoritenetworks.init.PNBlocks
import xyz.milosworks.phasoritenetworks.init.PNComponents
import xyz.milosworks.phasoritenetworks.init.PNItems
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer


class PNLootTableProvider(output: PackOutput, provider: CompletableFuture<HolderLookup.Provider>) :
	LootTableProvider(
		output, emptySet(), listOf(
			SubProviderEntry(::BlockLootProvider, LootContextParamSets.BLOCK)
		), provider
	) {

	class BlockLootProvider(provider: HolderLookup.Provider) :
		BlockLootSubProvider(setOf(), FeatureFlags.DEFAULT_FLAGS, provider) {

		override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
			generate()
			map.forEach(output)
		}

		override fun generate() {
			dropComponent(PNBlocks.PHASORITE_EXPORTER)
			dropComponent(PNBlocks.PHASORITE_IMPORTER)

			createSingleItemTable(PNBlocks.PHASORITE_BLOCK)

			phasoriteBud(PNBlocks.SMALL_PHASORITE_BUD)
			phasoriteBud(PNBlocks.MEDIUM_PHASORITE_BUD)
			phasoriteBud(PNBlocks.LARGE_PHASORITE_BUD)

			clusterDrop(PNBlocks.PHASORITE_CLUSTER, PNItems.PHASORITE_CRYSTAL, UniformGenerator.between(2f, 5f))
			clusterDrop(
				PNBlocks.CHARGED_PHASORITE_CLUSTER,
				PNItems.CHARGED_PHASORITE_CRYSTAL,
				UniformGenerator.between(2f, 4f)
			)

			add(
				PNBlocks.BUDDING_PHASORITE_BLOCK,
				createSingleItemTableWithSilkTouch(PNBlocks.BUDDING_PHASORITE_BLOCK, PNBlocks.PHASORITE_BLOCK)
			)
		}

		private fun phasoriteBud(block: Block) {
			clusterDrop(block, PNItems.PHASORITE_DUST, UniformGenerator.between(1f, 3f))
		}

		private fun clusterDrop(block: Block, item: ItemLike, quantity: NumberProvider) {
			createSilkTouchDispatchTable(
				block,
				LootItem.lootTableItem(item)
					.apply(SetItemCountFunction.setCount(quantity))
					.apply(ApplyBonusCount.addUniformBonusCount(enchantment(Enchantments.FORTUNE)))
					.apply(ApplyExplosionDecay.explosionDecay())
			).also {
				add(block, it)
			}
		}

		private fun dropComponent(
			block: Block,
		) {
			LootTable.lootTable().withPool(
				applyExplosionCondition(
					block, LootPool.lootPool().setRolls(ConstantValue.exactly(1f)).add(
						LootItem.lootTableItem(block).apply(
							CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
								.include(PNComponents.COMPONENT_DATA.get())
								.include(DataComponents.CUSTOM_NAME)
						)
					)
				)
			).also {
				add(block, it)
			}
		}

		private fun enchantment(key: ResourceKey<Enchantment>): Holder.Reference<Enchantment> {
			return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key)
		}
	}
}