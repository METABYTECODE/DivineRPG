package divinerpg.entities.vethea;

import divinerpg.entities.projectile.arrows.DarvenArrow;
import divinerpg.registries.ItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;

public class EntityTwins extends EntityDuo implements RangedAttackMob {
    public EntityTwins(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new RangedAttackGoal(this, 0.25F, 5, 64.0F));
    }
    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }
    @Override
    public void performRangedAttack(LivingEntity target, float par2) {
        if(isAlive() && getTarget() != null && (isFast || abilityCoolDown % 4 == 0)) {
            DarvenArrow abstractarrow = new DarvenArrow(level(), this, new ItemStack(ItemRegistry.darven_arrow.get()), new ItemStack(ItemRegistry.darven_bow.get()));
            abstractarrow.powerMultiplier = 2.5F;
            double d0 = target.getX() - getX(), d1 = target.getY(0.3333333333333333) - abstractarrow.getY(), d2 = target.getZ() - getZ(), d3 = Math.sqrt(d0 * d0 + d2 * d2);
            abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, 14F - (level().getDifficulty().getId() << 2));
            playSound(SoundEvents.SKELETON_SHOOT, 1F, 1F / (getRandom().nextFloat() * .4F + .8F));
            level().addFreshEntity(abstractarrow);
        }
    }
}