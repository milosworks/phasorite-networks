package vyrek.phasoritenetworks.datagen

import appeng.core.AppEng
import appeng.recipes.handlers.ChargerRecipeBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.init.PNBlocks
import vyrek.phasoritenetworks.init.PNItems
import java.util.concurrent.CompletableFuture

class PNRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
	RecipeProvider(output, registries) {

	override fun buildRecipes(out: RecipeOutput) {
		resetCraft(out, PNBlocks.PHASORITE_IMPORTER, "phasorite_importer_reset")
		resetCraft(out, PNBlocks.PHASORITE_EXPORTER, "phasorite_exporter_reset")

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PNItems.PHASORITE_SEED)
			.pattern("AR")
			.pattern("RA")
			.define('A', Tags.Items.GEMS_AMETHYST)
			.define('R', Tags.Items.DUSTS_REDSTONE)
			.unlockedBy("has_item", has(Tags.Items.GEMS_AMETHYST))
			.save(out, PhasoriteNetworks.id("phasorite_seed"))

		compactRecipe(out, PNBlocks.PHASORITE_BLOCK, PNItems.PHASORITE_CRYSTAL)

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PNItems.PHASORITE_CORE)
			.pattern("GDG")
			.pattern("PCP")
			.pattern("GDG")
			.define('G', Tags.Items.INGOTS_GOLD)
			.define('D', PNItems.PHASORITE_DUST)
			.define('P', PNItems.PHASORITE_CRYSTAL)
			.define('C', PNItems.CHARGED_PHASORITE_CRYSTAL)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.unlockedBy("has_item", has(PNItems.CHARGED_PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("phasorite_core"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PNItems.PHASORITE_LENS)
			.pattern("GGG")
			.pattern("IDI")
			.pattern("PCP")
			.define('G', Tags.Items.GLASS_BLOCKS)
			.define('D', PNItems.PHASORITE_DUST)
			.define('I', Tags.Items.INGOTS_IRON)
			.define('P', PNItems.PHASORITE_CRYSTAL)
			.define('C', PNItems.CHARGED_PHASORITE_CRYSTAL)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.unlockedBy("has_item", has(PNItems.CHARGED_PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("phasorite_lens"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PNBlocks.PHASORITE_EXPORTER)
			.pattern("PCP")
			.pattern("ROR")
			.pattern("GLG")
			.define('P', PNItems.PHASORITE_CRYSTAL)
			.define('L', PNItems.PHASORITE_LENS)
			.define('R', Tags.Items.DUSTS_REDSTONE)
			.define('O', PNItems.PHASORITE_CORE)
			.define('G', Tags.Items.INGOTS_GOLD)
			.define('C', PNItems.CHARGED_PHASORITE_CRYSTAL)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.unlockedBy("has_item", has(PNItems.CHARGED_PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("phasorite_exporter"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PNBlocks.PHASORITE_IMPORTER)
			.pattern("PLP")
			.pattern("ROR")
			.pattern("ICI")
			.define('P', PNItems.PHASORITE_CRYSTAL)
			.define('L', PNItems.PHASORITE_LENS)
			.define('R', Tags.Items.DUSTS_REDSTONE)
			.define('O', PNItems.PHASORITE_CORE)
			.define('I', Tags.Items.INGOTS_IRON)
			.define('C', PNItems.CHARGED_PHASORITE_CRYSTAL)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.unlockedBy("has_item", has(PNItems.CHARGED_PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("phasorite_importer"))

		SimpleCookingRecipeBuilder.smelting(
			Ingredient.of(PNItems.PHASORITE_CRYSTAL),
			RecipeCategory.MISC,
			PNItems.PHASORITE_DUST,
			.15f,
			200
		)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("dust_from_crystals"))

		SimpleCookingRecipeBuilder.blasting(
			Ingredient.of(PNItems.PHASORITE_CRYSTAL),
			RecipeCategory.MISC,
			PNItems.PHASORITE_DUST,
			.15f,
			100
		)
			.unlockedBy("has_item", has(PNItems.PHASORITE_CRYSTAL))
			.save(out, PhasoriteNetworks.id("blasting_dust_from_crystals"))

		ChargerRecipeBuilder.charge(
			out.withConditions(ModLoadedCondition(AppEng.MOD_ID)),
			PhasoriteNetworks.id("charge_phasorite_crystal"),
			PNItems.PHASORITE_CRYSTAL, PNItems.CHARGED_PHASORITE_CRYSTAL
		)
	}

	private fun compactRecipe(out: RecipeOutput, big: ItemLike, small: ItemLike) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, small, 9)
			.requires(big)
			.unlockedBy("has_item", has(big))
			.unlockedBy("has_item", has(small))
			.save(out, PhasoriteNetworks.id("${getItemName(small)}_from_${getItemName(big)}"))

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, big)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.define('#', small)
			.unlockedBy("has_item", has(big))
			.unlockedBy("has_item", has(small))
			.save(out, PhasoriteNetworks.id("${getItemName(big)}_from_${getItemName(small)}"))
	}

	private fun resetCraft(out: RecipeOutput, block: Block, id: String) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, block)
			.requires(block)
			.unlockedBy("has_item", has(block))
			.save(out, PhasoriteNetworks.id(id))
	}
}