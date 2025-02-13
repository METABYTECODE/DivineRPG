package divinerpg.blocks.vanilla;

import java.util.Optional;
import divinerpg.blocks.base.BlockMod;
import divinerpg.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

public class BlockHellfireSponge extends BlockMod {
	public BlockHellfireSponge() {
		super(Block.Properties.ofFullCopy(Blocks.SPONGE).mapColor(MapColor.FIRE));
	}
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState s, boolean b) {
		if(tryRemoveWater(level, pos.above(), 64) | tryRemoveWater(level, pos.below(), 64) | tryRemoveWater(level, pos.north(), 64) | tryRemoveWater(level, pos.south(), 64) | tryRemoveWater(level, pos.east(), 64) | tryRemoveWater(level, pos.west(), 64)) {
			level.setBlock(pos, BlockRegistry.coldHellfireSponge.get().defaultBlockState(), UPDATE_ALL);
			level.playLocalSound(pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1F, 1F, false);
		}
	}
	protected boolean tryRemoveWater(Level level, BlockPos pos, int distance) {
		if(distance > 0) {
			distance--;
			boolean b = false;
			BlockState state = level.getBlockState(pos);
			if(state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN) || state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
				b = true;
			} else if(state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)) {
				state.onDestroyedByPlayer(level, pos, null, true, level.getFluidState(pos));
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
				b = true;
			} else {
				Optional<Boolean> o = state.getOptionalValue(BlockStateProperties.WATERLOGGED);
				if(o.isPresent() && o.get()) {
					level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), UPDATE_ALL);
					b = true;
				}
			}
			if(b) {
				level.addParticle(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0, 0.1, 0);
				tryRemoveWater(level, pos.above(), distance);
				tryRemoveWater(level, pos.below(), distance);
				tryRemoveWater(level, pos.north(), distance);
				tryRemoveWater(level, pos.south(), distance);
				tryRemoveWater(level, pos.east(), distance);
				tryRemoveWater(level, pos.west(), distance);
			}
			return b;
		} return false;
	}
}
