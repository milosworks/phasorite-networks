package vyrek.phasoritenetworks.block

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

open class PhasoriteBlock(props: Properties) : Block(props) {
	override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
		if (!level.isClientSide) return

		val pos = hit.blockPos
		level.playSound(
			null,
			pos,
			SoundEvents.AMETHYST_BLOCK_HIT,
			SoundSource.BLOCKS,
			1.2f,
			0.6f + level.random.nextFloat() * 1.4f
		)
		level.playSound(
			null,
			pos,
			SoundEvents.AMETHYST_BLOCK_CHIME,
			SoundSource.BLOCKS,
			1.2f,
			0.6f + level.random.nextFloat() * 1.4f
		)
	}
}