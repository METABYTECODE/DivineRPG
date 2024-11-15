package divinerpg.entities.apalachia;


import divinerpg.entities.base.*;
import divinerpg.entities.projectile.arrows.WildwoodArrow;
import divinerpg.registries.*;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;

public class EntityEnchantedArcher extends EntityDivineMonster implements RangedAttackMob {


    public EntityEnchantedArcher(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }
    @Override public boolean isAggressive() {return true;}
    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, null));
        goalSelector.addGoal(0, new RangedAttackGoal(this, 1, 15, 60, 15));
    }

    @Override
    public int getArmorValue() {
        return 10;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if(isAlive() && getTarget() != null) {
            WildwoodArrow abstractarrow = new WildwoodArrow(level(), this, new ItemStack(ItemRegistry.wildwood_arrow.get()), new ItemStack(ItemRegistry.apalachia_bow.get()));
            abstractarrow.powerMultiplier = 1.2F;
            double d0 = target.getX() - getX(), d1 = target.getY(0.3333333333333333) - abstractarrow.getY(), d2 = target.getZ() - getZ(), d3 = Math.sqrt(d0 * d0 + d2 * d2);
            abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, 14F - (level().getDifficulty().getId() << 2));
            playSound(SoundEvents.SKELETON_SHOOT, 1F, 1F / (getRandom().nextFloat() * .4F + .8F));
            level().addFreshEntity(abstractarrow);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.ARCHER.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.HIGH_HIT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.HIGH_HIT.get();
    }

}
