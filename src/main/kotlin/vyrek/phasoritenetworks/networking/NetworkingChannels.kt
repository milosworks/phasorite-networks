package vyrek.phasoritenetworks.networking

import io.wispforest.owo.network.ClientAccess
import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.ServerAccess
import vyrek.phasoritenetworks.PhasoriteNetworks
import vyrek.phasoritenetworks.common.components.PhasoriteComponentEntity
import kotlin.reflect.KClass

fun <R : Record> OwoNetChannel.registerServerbound(
	messageClass: KClass<R>,
	handler: (R, ServerAccess) -> Unit
) = this.registerServerbound(messageClass.java, handler)

fun <R : Record> OwoNetChannel.registerClientbound(
	messageClass: KClass<R>,
	handler: (R, ClientAccess) -> Unit
) = this.registerClientbound(messageClass.java, handler)

fun <R : Any> OwoNetChannel.ServerHandle.send(msg: R) = this.send(msg as Record)

object NetworkingChannels {
	val CHANNEL: OwoNetChannel = OwoNetChannel.create(PhasoriteNetworks.identifier("main"))

	fun init() {
		CHANNEL.registerServerbound(NetworkingPackets.UpdateComponentData::class) { msg, access ->
			val entity = access.player.serverLevel().getBlockEntity(msg.pos) as? PhasoriteComponentEntity
			if (entity != null) {
				if (msg.name != entity.name) entity.name = msg.name
				if (msg.priority != entity.priority) entity.priority = msg.priority
				if (msg.overrideMode != entity.overrideMode) entity.overrideMode = msg.overrideMode
				if (msg.limit != entity.rawLimit) entity.rawLimit = msg.limit
				if (msg.limitlessMode != entity.limitlessMode) entity.limitlessMode = msg.limitlessMode

				entity.setChanged()
			}
		}

		CHANNEL.registerServerbound(NetworkingPackets.CreateNetwork::class) { msg, access ->
			val entity = access.player.serverLevel().getBlockEntity(msg.pos) as? PhasoriteComponentEntity
			if (entity != null) {
				println("Create network")
			}
		}
	}
}