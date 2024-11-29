package divinerpg.entities.vanilla.overworld;

import divinerpg.entities.base.EntityDivineMonster;
import divinerpg.entities.goals.FindOreGoal;
import divinerpg.entities.goals.MoveToChestGoal;
import divinerpg.entities.goals.MoveToItemGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class EntityMiner extends EntityDivineMonster {
    private static final Predicate<Difficulty> HARD_DIFFICULTY_PREDICATE = (difficulty) -> difficulty == Difficulty.HARD;
    private final Container inventory = new SimpleContainer(36);
    private final Random random = new Random();

    public EntityMiner(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new BreakDoorGoal(this, HARD_DIFFICULTY_PREDICATE));
        goalSelector.addGoal(3, new FindOreGoal(this, 1.0D, null));
        goalSelector.addGoal(4, new MoveToChestGoal(this, 1.0D));
        goalSelector.addGoal(2, new MoveToItemGoal(this, 1.0D));
        goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D, 100));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            float f = (float) this.level().getDifficulty().getId();
            if (getMainHandItem().isEmpty() && isOnFire() && random.nextFloat() < f * 0.3F) {
                entityIn.setRemainingFireTicks(2 * (int) f);
            }
        }
        return flag;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (this.random.nextInt(7) == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_PICKAXE));
        } else if (this.random.nextInt(5) == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
        } else if (this.random.nextInt(3) == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_PICKAXE));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_PICKAXE));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isAlive()) {
            if (isSunBurnTick()) handleSunBurn();

            if (!this.level().isClientSide) {
                for (ItemEntity itemEntity : level().getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(5.0D))) {
                    if (itemEntity != null && this.distanceTo(itemEntity) < 1.0D) {
                        pickUpDroppedItem(itemEntity);
                        break;
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
        populateDefaultEquipmentSlots(getRandom(), difficultyIn);
        populateDefaultEquipmentEnchantments(level, getRandom(), difficultyIn);
        return spawnDataIn;
    }

    public boolean addItemToInventory(ItemStack stack) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack existingStack = inventory.getItem(i);
            if (existingStack.isEmpty() || (existingStack.is(stack.getItem()) && existingStack.getCount() < existingStack.getMaxStackSize())) {
                inventory.setItem(i, stack);
                return true;
            }
        }
        return false;
    }

    public void pickUpDroppedItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (!stack.isEmpty()) {
            if (!addItemToInventory(stack)) {
                this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack));
            } else {
                itemEntity.discard();
            }
        }
    }

    @Override
    public void onItemPickup(ItemEntity itemEntity) {
        if (this.isAlive()) {
            pickUpDroppedItem(itemEntity);
        }
    }

    public Container getInventory() {
        return inventory;
    }

    private void handleSunBurn() {
        ItemStack itemstack = getItemBySlot(EquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            if (itemstack.isDamageableItem()) {
                itemstack.setDamageValue(itemstack.getDamageValue() + random.nextInt(2));
                if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                    onEquippedItemBroken(itemstack.getItem(), EquipmentSlot.HEAD);
                    setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
        } else {
            setRemainingFireTicks(8);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY(), this.getZ(), stack);
                level().addFreshEntity(itemEntity);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

}