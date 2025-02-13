package divinerpg.entities.vanilla.overworld;

import divinerpg.entities.base.EntityDivineMonster;
import divinerpg.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.*;

public class EntityJungleDramcryx extends EntityDivineMonster {

    public EntityJungleDramcryx(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }
    @Override public boolean isAggressive() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.DRAMCRYX.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.DRAMCRYX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.DRAMCRYX_HURT.get();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0.0F;
    }
}
