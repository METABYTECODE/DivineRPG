package divinerpg.entities.vanilla.overworld;

import divinerpg.entities.base.EntityDivineMonster;
import divinerpg.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import javax.annotation.Nullable;

public class EntityAridWarrior extends EntityDivineMonster implements RangedAttackMob {
    public EntityAridWarrior(EntityType<? extends Monster> type, Level worldIn) {super(type, worldIn);}
    @Override public boolean isAggressive() {return true;}
    @Override protected void registerGoals() {
    	super.registerGoals();
        goalSelector.addGoal(0, new RangedBowAttackGoal<>(this, getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue(), 5, (float)getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
    }
    @Override protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.shadow_bow.get()));
    }
    @Nullable
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data) {
        RandomSource random = level.getRandom();
        populateDefaultEquipmentSlots(random, difficulty);
        populateDefaultEquipmentEnchantments(level, random, difficulty);
        return data;
    }
    @Override public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if(isAlive() && getTarget() != null) {
            ItemStack weapon = getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof BowItem));
            ItemStack itemstack1 = getProjectile(weapon);
            AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack1, distanceFactor, weapon);
            abstractarrow.setBaseDamage(1.5);
            Item var7 = weapon.getItem();
            if(var7 instanceof ProjectileWeaponItem weaponItem) abstractarrow = weaponItem.customArrow(abstractarrow, itemstack1, weapon);
            double d0 = target.getX() - getX(), d1 = target.getY(0.3333333333333333) - abstractarrow.getY(), d2 = target.getZ() - getZ(), d3 = Math.sqrt(d0 * d0 + d2 * d2);
            abstractarrow.shoot(d0, d1 + d3 * 0.2, d2, 1.6F, 14F - (level().getDifficulty().getId() << 2));
            playSound(SoundEvents.SKELETON_SHOOT, 1F, 1F / (getRandom().nextFloat() * .4F + .8F));
            level().addFreshEntity(abstractarrow);
        }
    }
    @Override protected SoundEvent getAmbientSound() {return SoundRegistry.ARID_WARRIOR.get();}
    @Override protected SoundEvent getHurtSound(DamageSource source) {return SoundRegistry.ARID_WARRIOR_HURT.get();}
    @Override protected SoundEvent getDeathSound() {return SoundRegistry.ARID_WARRIOR_HURT.get();}
    @Override public float getWalkTargetValue(BlockPos pos, LevelReader reader) {return 0;}
}