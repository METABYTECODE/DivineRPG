package divinerpg.entities.vanilla.nether;

import divinerpg.entities.base.EntityDivineMonster;
import divinerpg.entities.projectile.arrows.EdenArrow;
import divinerpg.entities.projectile.arrows.InfernoArrow;
import divinerpg.registries.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityWildfire extends EntityDivineMonster implements RangedAttackMob {
    public EntityWildfire(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }
    @Override public boolean isAggressive() {return true;}
    @Override
    protected void registerGoals() {
    	super.registerGoals();
        goalSelector.addGoal(0, new RangedAttackGoal(this, this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue(), 3, (float)getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
    }
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if(isAlive() && getTarget() != null) {
            InfernoArrow abstractarrow = new InfernoArrow(level(), this, new ItemStack(ItemRegistry.inferno_arrow.get()), new ItemStack(ItemRegistry.inferno_bow.get()));
            abstractarrow.powerMultiplier = 2.5F;
            double d0 = target.getX() - getX(), d1 = target.getY(0.3333333333333333) - abstractarrow.getY(), d2 = target.getZ() - getZ(), d3 = Math.sqrt(d0 * d0 + d2 * d2);
            abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, 14F - (level().getDifficulty().getId() << 2));
            playSound(SoundEvents.SKELETON_SHOOT, 1F, 1F / (getRandom().nextFloat() * .4F + .8F));
            level().addFreshEntity(abstractarrow);
        }
    }
    @Override
    public boolean fireImmune() {
    	return true;
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.WILDFIRE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.WILDFIRE_HURT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.WILDFIRE_HURT.get();
    }
}
