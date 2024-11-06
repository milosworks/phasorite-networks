package vyrek.phasoritenetworks.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlags
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
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.neoforged.neoforge.registries.DeferredHolder
import vyrek.phasoritenetworks.init.PNBlocks
import vyrek.phasoritenetworks.init.PNComponents
import vyrek.phasoritenetworks.init.PNItems
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
			selfDropWithNbt(PNBlocks.PHASORITE_EXPORTER, PNComponents.COMPONENT_DATA)
			selfDropWithNbt(PNBlocks.PHASORITE_IMPORTER, PNComponents.COMPONENT_DATA)

			selfDrop(PNBlocks.PHASORITE_BLOCK)

			phasoriteBud(PNBlocks.SMALL_PHASORITE_BUD)
			phasoriteBud(PNBlocks.MEDIUM_PHASORITE_BUD)
			phasoriteBud(PNBlocks.LARGE_PHASORITE_BUD)

			clusterDrop(PNBlocks.PHASORITE_CLUSTER, PNItems.PHASORITE_CRYSTAL, UniformGenerator.between(2f, 5f))
			clusterDrop(
				PNBlocks.CHARGED_PHASORITE_CLUSTER,
				PNItems.CHARGED_PHASORITE_CRYSTAL,
				UniformGenerator.between(1f, 3f)
			)

//			add(PNBlocks.BUDDING_PHASORITE_BLOCK) { b ->
//				createSingleItemTableWithSilkTouch(
//					b,
//					PNBlocks.PHASORITE_BLOCK
//				)
//			}
			add(
				PNBlocks.BUDDING_PHASORITE_BLOCK,
				createSingleItemTableWithSilkTouch(PNBlocks.BUDDING_PHASORITE_BLOCK, PNBlocks.PHASORITE_BLOCK)
			)
		}

		private fun phasoriteBud(block: Block) {
			add(
				block,
				LootTable.lootTable().withPool(
					LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
						.add(
							LootItem.lootTableItem(PNItems.PHASORITE_DUST)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, 2f)))
								.apply(
									ApplyBonusCount.addUniformBonusCount(
										registries.lookupOrThrow(Registries.ENCHANTMENT)
											.getOrThrow(Enchantments.FORTUNE)
									)
								)
								.apply(ApplyExplosionDecay.explosionDecay())
						)
				)
			)
		}

		private fun clusterDrop(block: Block, item: ItemLike, quantity: UniformGenerator) {
			add(
				block,
				LootTable.lootTable().withPool(
					LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
						.add(
							LootItem.lootTableItem(item)
								.apply(SetItemCountFunction.setCount(quantity))
								.apply(
									ApplyBonusCount.addUniformBonusCount(
										registries.lookupOrThrow(Registries.ENCHANTMENT)
											.getOrThrow(Enchantments.FORTUNE)
									)
								)
								.apply(ApplyExplosionDecay.explosionDecay())
						).add(
							LootItem.lootTableItem(PNItems.PHASORITE_DUST)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, 2f)))
								.apply(
									ApplyBonusCount.addUniformBonusCount(
										registries.lookupOrThrow(Registries.ENCHANTMENT)
											.getOrThrow(Enchantments.FORTUNE)
									)
								)
								.apply(ApplyExplosionDecay.explosionDecay())
						)
				)
			)
		}

		private fun selfDrop(
			block: Block
		) {
			add(
				block, LootTable.lootTable().withPool(
					LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
						.add(LootItem.lootTableItem(block).`when`(ExplosionCondition.survivesExplosion()))
				)
			)
		}

		private fun selfDropWithNbt(
			block: Block,
			componentData: DeferredHolder<DataComponentType<*>, out DataComponentType<*>>
		) {
			add(
				block, LootTable.lootTable().withPool(
					LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
						.add(
							LootItem.lootTableItem(block).apply(
								CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
									.include(componentData.get())
							)
						)
						.`when`(ExplosionCondition.survivesExplosion())
				)
			)
		}
	}
}