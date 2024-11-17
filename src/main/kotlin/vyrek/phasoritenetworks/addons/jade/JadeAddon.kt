package vyrek.phasoritenetworks.addons.jade

import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin
import vyrek.phasoritenetworks.common.components.PhasoriteComponentBlock

@WailaPlugin
class JadeAddon : IWailaPlugin {
	override fun register(registration: IWailaCommonRegistration) {
		registration.registerBlockDataProvider(
			ComponentsProvider.INSTANCE,
			PhasoriteComponentBlock::class.java
		)
	}

	override fun registerClient(registration: IWailaClientRegistration) {
		registration.registerBlockComponent(
			ComponentsProvider.INSTANCE,
			PhasoriteComponentBlock::class.java
		)
	}
}