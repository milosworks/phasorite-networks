package xyz.milosworks.phasoritenetworks.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import io.wispforest.owo.ui.core.Color
import net.minecraft.client.Camera
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

operator fun Color.component1(): Float = red
operator fun Color.component2(): Float = green
operator fun Color.component3(): Float = blue
operator fun Color.component4(): Float = alpha

/**
Handler to highlight blocks outlines

CR: Heavily inspired by Glodblocks' Glodium library highlight renderer
 */
class Highlight :
	RenderType("", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 0, false, false, {}, {}) {
	companion object {
		const val BASE_DURATION_MS = 1000
		val OUTLINE_COLOR: Color = Color.RED

		private val LINE = LineStateShard(OptionalDouble.of(3.0))

		@Suppress("INACCESSIBLE_TYPE")
		private val BLOCK_HIGHLIGHT_LINE: RenderType = create(
			"block_highlight_line",
			DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 65536, false, false,
			CompositeState.builder()
				.setLineState(LINE)
				.setTransparencyState(TransparencyStateShard.GLINT_TRANSPARENCY)
				.setTextureState(NO_TEXTURE)
				.setDepthTestState(NO_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(NO_LIGHTMAP)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.createCompositeState(false)
		)

		val handler = Highlight()
	}

	// Could change to ObjectHeapPriorityQueue
	private val blocksToRender = PriorityQueue<BlockRenderData>(compareBy { it.time })

	/**
	 * Add a block to render
	 */
	fun add(pos: BlockPos, time: Long, color: Int?) {
		blocksToRender.add(BlockRenderData(pos, time, color?.let { Color.ofRgb(it) } ?: OUTLINE_COLOR))
	}

	/**
	 * Called on every tick on RenderLevelStageEvent after particles
	 * @see xyz.milosworks.phasoritenetworks.init.PNGameEvents.renderLevelStage
	 */
	fun onTick(stack: PoseStack, source: MultiBufferSource.BufferSource, camera: Camera) {
		if (blocksToRender.isEmpty()) return
		checkValid()

		RenderSystem.disableDepthTest()
		RenderSystem.enableBlend()

		for (block in blocksToRender) {
			drawOutline(AABB(block.pos), block.color, stack, source, camera)
		}

		source.endBatch()
		RenderSystem.enableDepthTest()
		RenderSystem.enableBlend()
		RenderSystem.defaultBlendFunc()
	}

	/**
	 * Draws an outline around the given axis-aligned bounding box (AABB) to visually highlight it.
	 */
	private fun drawOutline(
		box: AABB,
		color: Color,
		stack: PoseStack,
		source: MultiBufferSource.BufferSource,
		camera: Camera
	) {
		if (!camera.isInitialized) return

		val (r, g, b, a) = color

		// Calculate the position adjusted by the camera's position
		val cameraPos = camera.position.reverse()
		val aabb = box.move(cameraPos)

		// Create the corner points for the top and bottom planes of the AABB at maxZ and minZ
		val cornersMaxZ = listOf(
			Vec3(aabb.maxX, aabb.maxY, aabb.maxZ), // Top-right
			Vec3(aabb.maxX, aabb.minY, aabb.maxZ), // Bottom-right
			Vec3(aabb.minX, aabb.minY, aabb.maxZ), // Bottom-left
			Vec3(aabb.minX, aabb.maxY, aabb.maxZ)  // Top-left
		)

		val cornersMinZ = listOf(
			Vec3(aabb.maxX, aabb.maxY, aabb.minZ), // Top-right (minZ)
			Vec3(aabb.maxX, aabb.minY, aabb.minZ), // Bottom-right (minZ)
			Vec3(aabb.minX, aabb.minY, aabb.minZ), // Bottom-left (minZ)
			Vec3(aabb.minX, aabb.maxY, aabb.minZ)  // Top-left (minZ)
		)

		// Retrieve the buffer for drawing lines
		val buf = source.getBuffer(BLOCK_HIGHLIGHT_LINE)

		// Render the top and bottom rectangles
		renderBox(buf, stack, cornersMaxZ, r, g, b, a)
		renderBox(buf, stack, cornersMinZ, r, g, b, a)

		// Connect the corresponding corners of the top and bottom rectangles
		for (i in cornersMaxZ.indices) {
			renderLine(buf, stack, cornersMaxZ[i], cornersMinZ[i], r, g, b, a)
		}
	}

	/**
	 * Helper function to draw a box (rectangle) given its corners
	 */
	private fun renderBox(
		buf: VertexConsumer,
		stack: PoseStack,
		corners: List<Vec3>,
		r: Float,
		g: Float,
		b: Float,
		a: Float
	) {
		// Iterate through each corner and draw a line to the next, looping back to the first
		for (i in corners.indices) {
			renderLine(buf, stack, corners[i], corners[(i + 1) % corners.size], r, g, b, a)
		}
	}

	/**
	 * Function to draw a line between two points with specified color and transparency
	 */
	private fun renderLine(
		buf: VertexConsumer,
		pose: PoseStack,
		from: Vec3,
		to: Vec3,
		r: Float,
		g: Float,
		b: Float,
		a: Float
	) {
		// Get the last transformation matrix from the pose stack
		val mat = pose.last().pose()
		// Calculate the normal vector for shading purposes
		val normal = from.subtract(to)

		// Add the first vertex to the buffer with position, color, and normal
		buf.addVertex(mat, from.x.toFloat(), from.y.toFloat(), from.z.toFloat())
			.setColor(r, g, b, a)
			.setNormal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())

		// Add the second vertex similarly
		buf.addVertex(mat, to.x.toFloat(), to.y.toFloat(), to.z.toFloat())
			.setColor(r, g, b, a)
			.setNormal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
	}

	private fun checkValid() {
		while (blocksToRender.isNotEmpty() && System.currentTimeMillis() > blocksToRender.peek().time) {
			blocksToRender.poll()
		}
	}

	private data class BlockRenderData(
		val pos: BlockPos,
		val time: Long,
		val color: Color
	)
}