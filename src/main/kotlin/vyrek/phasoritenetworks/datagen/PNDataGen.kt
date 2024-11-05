package vyrek.phasoritenetworks.datagen

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import vyrek.phasoritenetworks.PhasoriteNetworks

@EventBusSubscriber(modid = PhasoriteNetworks.ID, bus = EventBusSubscriber.Bus.MOD)
object PNDataGen {
	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val gen = event.generator
		val out = gen.packOutput
		val helper = event.existingFileHelper
		val provider = event.lookupProvider

		gen.addProvider(event.includeClient(), PNBlockModelProvider(out, helper))
		gen.addProvider(event.includeClient(), PNItemModelProvider(out, helper))
		gen.addProvider(event.includeServer(), PNLootTableProvider(out, provider))
		gen.addProvider(event.includeServer(), PNBlockTagsProvider(out, provider, helper))
		gen.addProvider(event.includeServer(), PNRecipeProvider(out, provider))
	}
}