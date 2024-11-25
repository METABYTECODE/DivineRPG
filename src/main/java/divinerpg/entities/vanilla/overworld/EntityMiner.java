package divinerpg.entities.vanilla.overworld;

import divinerpg.entities.base.*;
import divinerpg.entities.goals.FindOreGoal;
import divinerpg.entities.goals.MoveToChestGoal;
import divinerpg.entities.goals.MoveToItemGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.*;
import java.util.function.Predicate;

public class EntityMiner extends EntityDivineMonster {
    private static final Predicate<Difficulty> HARD_DIFFICULTY_PREDICATE = (difficulty) -> { return difficulty == Difficulty.HARD; };

    private final Container inventory = new SimpleContainer(36);
    private BlockPos chestLocation = null;
    private boolean hasChest = false;
    private boolean isChestFull = false;

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
        goalSelector.addGoal(2, new FindOreGoal(this, 1.0D, chestLocation));
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
            if (getMainHandItem().isEmpty() && isOnFire() && random.nextFloat() < f * .3F) {
                entityIn.igniteForSeconds(2 * (int) f);
            }
        }
        return flag;
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(getRandom(), difficulty);
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
            boolean flag = isSunBurnTick();
            if(flag) {
                ItemStack itemstack = getItemBySlot(EquipmentSlot.HEAD);
                if(!itemstack.isEmpty()) {
                    if(itemstack.isDamageableItem()) {
                        Item item = itemstack.getItem();
                        itemstack.setDamageValue(itemstack.getDamageValue() + random.nextInt(2));
                        if(itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            onEquippedItemBroken(item, EquipmentSlot.HEAD);
                            setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    flag = false;
                }
                if(flag) igniteForSeconds(8);
            }
            if (!this.level().isClientSide) {
                for (ItemEntity itemEntity : level().getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(5.0D))) {
                    if (itemEntity != null && this.distanceTo(itemEntity) < 1.0D) {
                        goalSelector.addGoal(3, new MoveToItemGoal(this, 1.0D));
                        pickUpDroppedItem(itemEntity);
                        break;
                    }
                }
            }
            BlockPos orePos = new FindOreGoal(this, 1.0D, chestLocation).findClosestOre();
            if (orePos != null) {
                goalSelector.addGoal(4, new FindOreGoal(this, 1.0D, chestLocation));
            } else {
                if (!hasChest) {
                    findChest();
                }
                if (hasChest && chestLocation != null && !isChestFull) {
                    if (!goalSelector.getAvailableGoals().contains(MoveToChestGoal.class)) {
                        goalSelector.addGoal(4, new MoveToChestGoal(this, chestLocation, 1.0D));
                    }
                    storeItemsInChest();
                }
                if (isChestFull) {
                    findNewChest();
                }
            }
        }
    }

    private void findChest() {
        BlockPos pos = blockPosition();
        int searchRadius = 8;
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int z = -searchRadius; z <= searchRadius; z++) {
                for (int y = level().getMinBuildHeight(); y <= level().getMaxBuildHeight(); y++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState blockState = level().getBlockState(checkPos);
                    if (blockState.is(Blocks.CHEST)) {
                        chestLocation = checkPos;
                        hasChest = true;
                        return;
                    }
                }
            }
        }
    }

    private void storeItemsInChest() {
        if (chestLocation != null) {
            Container chestInventory = getChestInventory(chestLocation);
            if (chestInventory != null) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (!itemStack.isEmpty()) {
                        boolean itemStored = false;
                        if (addItemToChest(chestInventory, itemStack)) {
                            inventory.setItem(i, ItemStack.EMPTY);
                            itemStored = true;
                        }
                        if (!itemStored) {
                            isChestFull = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean addItemToChest(Container chestInventory, ItemStack stack) {
        for (int i = 0; i < chestInventory.getContainerSize(); i++) {
            ItemStack chestStack = chestInventory.getItem(i);
            if (chestStack.isEmpty() || (chestStack.is(stack.getItem()) && chestStack.getCount() < chestStack.getMaxStackSize())) {
                chestInventory.setItem(i, stack.copy());
                return true;
            }
        }
        return false;
    }

    private Container getChestInventory(BlockPos pos) {
        BlockEntity blockEntity = level().getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            return chestBlockEntity;
        }
        return null;
    }

    private void findNewChest() {
        chestLocation = null;
        hasChest = false;
        isChestFull = false;
        findChest();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
        populateDefaultEquipmentSlots(difficultyIn);
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
}
