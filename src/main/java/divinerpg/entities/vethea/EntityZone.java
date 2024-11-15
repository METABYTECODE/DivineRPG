package divinerpg.entities.vethea;

import divinerpg.entities.base.EntityDivineMonster;
import divinerpg.entities.projectile.arrows.KarosArrow;
import divinerpg.registries.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityZone extends EntityDivineMonster {

    public EntityZone(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
		this.setHealth(this.getMaxHealth());
    }
    @Override public boolean isAggressive() {return true;}
    @Override
    public void tick() {
        super.tick();

        Player player = level().getNearestPlayer(this, 32);
        if(player != null && !player.isCreative()) {
            this.setTarget(player);

            LivingEntity target = this.getTarget();
            if(!this.level().isClientSide() && target != null && this.tickCount % 40 == 0) {
                this.shootEntity(target);
            }
        }
    }

    private void shootEntity(LivingEntity target) {
        if(isAlive() && getTarget() != null) {
            KarosArrow abstractarrow = new KarosArrow(level(), this, new ItemStack(ItemRegistry.karos_arrow.get()), new ItemStack(ItemRegistry.karos_bow.get()));
            abstractarrow.powerMultiplier = 2.5F;
            double d0 = target.getX() - getX(), d1 = target.getY(0.3333333333333333) - abstractarrow.getY(), d2 = target.getZ() - getZ(), d3 = Math.sqrt(d0 * d0 + d2 * d2);
            abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, 14F - (level().getDifficulty().getId() << 2));
            playSound(SoundEvents.SKELETON_SHOOT, 1F, 1F / (getRandom().nextFloat() * .4F + .8F));
            level().addFreshEntity(abstractarrow);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.ZONE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.ZONE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.ZONE_HURT.get();
    }
}