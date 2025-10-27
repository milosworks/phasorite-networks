package xyz.milosworks.phasoritenetworks

import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.milosworks.phasoritenetworks.init.*
import xyz.milosworks.phasoritenetworks.networking.PNChannels

@Mod(PhasoriteNetworks.ID)
object PhasoriteNetworks {
    const val ID = "phasoritenetworks"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        PNBlocks.init(MOD_BUS)
        PNEntities.init(MOD_BUS)
        PNItems.init(MOD_BUS)
        PNCreativeTabs.init(MOD_BUS)
        PNMenus.init(MOD_BUS)
        PNComponents.init(MOD_BUS)

        PNChannels.init()
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ID, path)
    }
}