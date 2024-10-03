package vyrek.phasoritenetworks

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import vyrek.phasoritenetworks.init.PhasoriteNetworksBlocks
import vyrek.phasoritenetworks.init.PhasoriteNetworksCreativeTabs
import vyrek.phasoritenetworks.init.PhasoriteNetworksEntities
import vyrek.phasoritenetworks.init.PhasoriteNetworksItems

@Mod(PhasoriteNetworks.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object PhasoriteNetworks {
	const val ID = "phasoritenetworks"
	val LOGGER: Logger = LogManager.getLogger(ID)

	init {
//		register(PhasoriteNetworksBlocks)
//		register(PhasoriteNetworksEntities)
//		register(PhasoriteNetworksCreativeTabs)
//		register(PhasoriteNetworksItems)
//		register(PhasoriteNetworksDataComponents)
		PhasoriteNetworksBlocks.init(MOD_BUS)
		PhasoriteNetworksCreativeTabs.init(MOD_BUS)
		PhasoriteNetworksEntities.init(MOD_BUS)
		PhasoriteNetworksItems.init(MOD_BUS)
	}

	fun identifier(path: String): ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(ID, path)
	}

	@SubscribeEvent
	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		LOGGER.info("Registering capabilities")

		PhasoriteNetworksEntities.registerCapabilities(event)
	}
}