package divinerpg.entities.goals;

import divinerpg.entities.vanilla.overworld.EntityMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.EnumSet;

public class MoveToChestGoal extends Goal {
    private final EntityMiner miner;
    private BlockPos chestLocation = null;
    private final double speed;
    private static final double REACH_DISTANCE = 5.0D;

    public MoveToChestGoal(EntityMiner miner, double speed) {
        this.miner = miner;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (chestLocation == null || isChestFull()) {
            chestLocation = findNearestChest();
        }
        return chestLocation != null && miner.distanceToSqr(chestLocation.getX() + 0.5, chestLocation.getY() + 0.5, chestLocation.getZ() + 0.5) > 2.0D;
    }

    @Override
    public void start() {
        if (chestLocation != null) {
            miner.getNavigation().moveTo(chestLocation.getX() + 0.5, chestLocation.getY() + 0.5, chestLocation.getZ() + 0.5, speed);
        }
    }

    @Override
    public void stop() {
        if (chestLocation != null) {
            if (miner.distanceToSqr(chestLocation.getX() + 0.5, chestLocation.getY() + 0.5, chestLocation.getZ() + 0.5) <= REACH_DISTANCE * REACH_DISTANCE) {
                storeItemsInChest();
            }
        }
    }

    private BlockPos findNearestChest() {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        BlockPos origin = miner.blockPosition();
        int searchRadius = 8;

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int z = -searchRadius; z <= searchRadius; z++) {
                for (int y = -5; y <= 5; y++) {
                    checkPos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (miner.level().getBlockState(checkPos).is(Blocks.CHEST)) {
                        return checkPos.immutable();
                    }
                }
            }
        }
        return null;
    }

    private void storeItemsInChest() {
        BlockEntity blockEntity = miner.level().getBlockEntity(chestLocation);
        if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            Container chestInventory = chestBlockEntity;
            for (int i = 0; i < miner.getInventory().getContainerSize(); i++) {
                ItemStack minerStack = miner.getInventory().getItem(i);
                if (!minerStack.isEmpty()) {
                    for (int j = 0; j < chestInventory.getContainerSize(); j++) {
                        ItemStack chestStack = chestInventory.getItem(j);
                        if (chestStack.isEmpty()) {
                            chestInventory.setItem(j, minerStack.copy());
                            miner.getInventory().setItem(i, ItemStack.EMPTY);
                            break;
                        } else if (ItemStack.isSameItemSameComponents(minerStack, chestStack) && chestStack.getCount() < chestStack.getMaxStackSize()) {
                            int transferAmount = Math.min(minerStack.getCount(), chestStack.getMaxStackSize() - chestStack.getCount());
                            chestStack.grow(transferAmount);
                            minerStack.shrink(transferAmount);
                            if (minerStack.isEmpty()) {
                                miner.getInventory().setItem(i, ItemStack.EMPTY);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isChestFull() {
        BlockEntity blockEntity = miner.level().getBlockEntity(chestLocation);
        if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
            for (int i = 0; i < chestBlockEntity.getContainerSize(); i++) {
                if (chestBlockEntity.getItem(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
