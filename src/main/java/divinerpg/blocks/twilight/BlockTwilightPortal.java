package divinerpg.blocks.twilight;

import divinerpg.blocks.base.SimplePortalBlock;
import divinerpg.world.placement.Surface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BlockTwilightPortal extends SimplePortalBlock {
    public BlockTwilightPortal(ResourceKey<Level> rootDimension, String frameTag, Block frameBlock, ResourceLocation particle) {
        super(rootDimension, frameTag, frameBlock, particle);
    }
    @Override
    public BlockPos applyPlacementLocationPreference(ServerLevel level, Entity entity, BlockPos pos) {
        return level.dimension() == rootDimension
                ? new BlockPos(pos.getX(), Surface.getSurface(Surface.Surface_Type.HIGHEST_GROUND, Surface.Mode.FULL, 127, 250, 0, level, level.getRandom(), pos.getX(), pos.getZ()), pos.getZ())
                : super.applyPlacementLocationPreference(level, entity, pos);
    }
}