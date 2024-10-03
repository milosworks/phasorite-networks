package vyrek.phasoritenetworks.init

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.common.networks.NetworksData

@EventBusSubscriber(modid = PhasoriteNetworks.ID, bus = EventBusSubscriber.Bus.GAME)
object PhasoriteNetworksEvents {
	@SubscribeEvent
	fun onPostServerTick(event: ServerTickEvent.Post) {
		NetworksData.get().networks.forEach { network ->
			network.onTick()
		}
	}
}