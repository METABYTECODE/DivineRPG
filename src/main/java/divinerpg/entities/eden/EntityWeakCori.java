package divinerpg.entities.eden;

import divinerpg.entities.base.EntityDivineFlyingMob;
import divinerpg.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.*;

public class EntityWeakCori extends EntityDivineFlyingMob implements RangedAttackMob {

    public EntityWeakCori(EntityType<? extends EntityDivineFlyingMob> type, Level worldIn) {
        super(type, worldIn);
    }
    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float range) {
        if (isAlive() && getTarget() != null && !level().isClientSide) {
            ThrowableProjectile projectile = EntityRegistry.CORI_SHOT.get().create(level());
            projectile.setOwner(this);
            projectile.setPos(getEyePosition());
            double d0 = getTarget().getX() - this.getX();
            double d1 = getTarget().getY(0.3333333333333333D) - projectile.getY();
            double d2 = getTarget().getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            projectile.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 0.8F);
            this.level().addFreshEntity(projectile);
        }
    }

    @Override
    public int getMaxSpawnClusterSize() {return 1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.CORI_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.CORI_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.CORI_HURT.get();
    }

    public static boolean weakCoriSpawnRule(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return pos.getY() > 128 && randomIn.nextBoolean();
    }
}
