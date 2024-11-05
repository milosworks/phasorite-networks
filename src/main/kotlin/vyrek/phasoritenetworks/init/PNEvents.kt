package vyrek.phasoritenetworks.init

import io.wispforest.owo.ui.core.Color
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.PhasoriteNetworks.LOGGER
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import vyrek.phasoritenetworks.common.networks.NetworksData
import vyrek.phasoritenetworks.networking.PNChannels
import vyrek.phasoritenetworks.ui.UIScreen

@EventBusSubscriber(modid = PhasoriteNetworks.ID, bus = EventBusSubscriber.Bus.GAME)
object PNGameEvents {
	@SubscribeEvent
	fun onPostServerTick(event: ServerTickEvent.Post) {
		NetworksData.get().networks.values.forEach { network ->
			network.onTick(event.server)
		}
	}
}

@EventBusSubscriber(modid = PhasoriteNetworks.ID, bus = EventBusSubscriber.Bus.MOD)
object PNEvents {
	@SubscribeEvent
	fun onCommonSetup(event: FMLCommonSetupEvent) {
		LOGGER.info("Phasorite Networks initiated up")
	}

	@SubscribeEvent
	fun registerScreens(event: RegisterMenuScreensEvent) {
		LOGGER.info("Registering screens")

		event.register(PNMenus.UI.get(), ::UIScreen)
	}

	@SubscribeEvent
	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		LOGGER.info("Registering capabilities")

		PNEntities.registerCapabilities(event)
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	fun onClientSetup(event: FMLClientSetupEvent) {
		LOGGER.info("Phasorite Networks Client initiated up")

		PNChannels.clinit()
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	fun registerBlockColorHandlers(event: RegisterColorHandlersEvent.Block) {
		event.register(
			{ _, blockAndTintGetter, blockPos, _ ->
				if (blockPos == null) return@register -1

				val entity =
					blockAndTintGetter?.getBlockEntity(blockPos) as? PhasoriteComponentEntity ?: return@register -1
				if (entity.network.isValid && entity.network.color != Color.WHITE.argb())
					return@register entity.network.color

				-1
			},
			PNBlocks.PHASORITE_EXPORTER,
			PNBlocks.PHASORITE_IMPORTER
		)
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	fun registerItemColorHandlers(event: RegisterColorHandlersEvent.Item) {
		event.register(
			{ stack, _ ->
				val data = stack.get(PNComponents.COMPONENT_DATA) ?: return@register -1

				if (data.color != Color.WHITE.argb()) return@register data.color

				-1
			},
			PNBlocks.PHASORITE_IMPORTER,
			PNBlocks.PHASORITE_EXPORTER
		)
	}
}