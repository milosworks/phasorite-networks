package vyrek.phasoritenetworks.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.init.PNItems

class PNItemModelProvider(output: PackOutput, fileHelper: ExistingFileHelper) :
	ItemModelProvider(output, PhasoriteNetworks.ID, fileHelper) {
	override fun registerModels() {
		basicItem(PNItems.PHASORITE_DUST)
		basicItem(PNItems.PHASORITE_CRYSTAL)
		basicItem(PNItems.CHARGED_PHASORITE_CRYSTAL)

		basicItem(PNItems.PHASORITE_LENS)
		basicItem(PNItems.PHASORITE_CORE)
		
		basicItem(PNItems.PHASORITE_SEED)
	}
}