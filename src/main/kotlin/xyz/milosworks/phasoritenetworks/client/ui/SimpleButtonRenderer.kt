package xyz.milosworks.phasoritenetworks.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.core.AnimatableProperty
import io.wispforest.owo.ui.core.PositionedRectangle
import net.minecraft.resources.ResourceLocation

fun SimpleButtonRenderer(texture: ResourceLocation, textureSize: Int) =
	ButtonComponent.Renderer { ctx, btn, delta ->
		RenderSystem.enableDepthTest()

		val matrices = ctx.pose()
		matrices.pushPose()
		matrices.translate(btn.x.toDouble(), btn.y.toDouble(), 0.0)
		matrices.scale(btn.width / textureSize.toFloat(), btn.height / textureSize.toFloat(), 0f)

		val visibleArea =
			AnimatableProperty.of(PositionedRectangle.of(0, 0, textureSize, textureSize)).get()

		val bottomEdge = (visibleArea.y() + visibleArea.height()).coerceAtMost(textureSize);
		val rightEdge = (visibleArea.x() + visibleArea.width()).coerceAtMost(textureSize);

		ctx.blit(
			texture,
			visibleArea.x(),
			visibleArea.y(),
			rightEdge - visibleArea.x(),
			bottomEdge - visibleArea.y(),
			0f + visibleArea.x(),
			0f + visibleArea.y(),
			rightEdge - visibleArea.x(),
			bottomEdge - visibleArea.y(),
			textureSize, textureSize
		)

		matrices.popPose();
	}
