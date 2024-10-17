package vyrek.phasoritenetworks

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import vyrek.phasoritenetworks.init.*
import vyrek.phasoritenetworks.networking.NetworkingChannels
import vyrek.phasoritenetworks.ui.UIScreen

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
		PhasoriteNetworksEntities.init(MOD_BUS)
		PhasoriteNetworksItems.init(MOD_BUS)
		PhasoriteNetworksCreativeTabs.init(MOD_BUS)
		PhasoriteNetworksMenus.init(MOD_BUS)

		NetworkingChannels.init()
	}

	fun identifier(path: String): ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(ID, path)
	}

	@SubscribeEvent
	fun onClientSetup(event: FMLClientSetupEvent) {
		LOGGER.info("Phasorite Networks Client initiated up")
	}

	@SubscribeEvent
	fun onCommonSetup(event: FMLCommonSetupEvent) {
		LOGGER.info("Phasorite Networks initiated up")
	}

	@SubscribeEvent
	fun registerScreens(event: RegisterMenuScreensEvent) {
		LOGGER.info("Registering screens")

		event.register(PhasoriteNetworksMenus.UI.get(), ::UIScreen)
	}

	@SubscribeEvent
	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		LOGGER.info("Registering capabilities")

		PhasoriteNetworksEntities.registerCapabilities(event)
	}
}