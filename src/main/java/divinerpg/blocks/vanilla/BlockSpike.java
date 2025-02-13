package divinerpg.blocks.vanilla;

import divinerpg.blocks.base.BlockMod;
import divinerpg.registries.DamageRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockSpike extends BlockMod {
    private final boolean isHot;
    public BlockSpike(boolean isHot, MapColor color) {
        super(Block.Properties.of().mapColor(color).strength(5, 6).requiresCorrectToolForDrops().sound(SoundType.METAL));
        this.isHot = isHot;
    }
    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entityIn){
        if(entityIn instanceof LivingEntity) {
            if(isHot) {
                entityIn.hurt(level.damageSources().source(DamageRegistry.SPIKE.getKey()), 8);
                entityIn.igniteForSeconds(10);
            } else entityIn.hurt(level.damageSources().source(DamageRegistry.SPIKE.getKey()), 5);
        }
    }
}