package divinerpg.entities.goals;

import divinerpg.entities.vanilla.overworld.EntityMiner;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class MoveToChestGoal extends Goal {
    private final EntityMiner miner;
    private BlockPos chestLocation;
    private Container chestInventory;
    private final double speed;

    public MoveToChestGoal(EntityMiner miner, BlockPos chestLocation, double speed) {
        this.miner = miner;
        this.chestLocation = chestLocation;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return chestLocation != null && !miner.getNavigation().isDone() && miner.distanceToSqr(chestLocation.getX(), chestLocation.getY(), chestLocation.getZ()) > 2.0D;
    }

    @Override
    public void start() {
        miner.getNavigation().moveTo(chestLocation.getX(), chestLocation.getY(), chestLocation.getZ(), speed);
    }

    @Override
    public void stop() {
        if (chestLocation != null) {
            storeItemsInChest();
        }
    }

    private void storeItemsInChest() {
        if (chestInventory != null) {
            for (int i = 0; i < miner.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = miner.getInventory().getItem(i);
                if (!itemStack.isEmpty()) {
                    for (int j = 0; j < chestInventory.getContainerSize(); j++) {
                        ItemStack chestStack = chestInventory.getItem(j);
                        if (chestStack.isEmpty() || (chestStack.is(itemStack.getItem()) && chestStack.getCount() < chestStack.getMaxStackSize())) {
                            chestInventory.setItem(j, itemStack.copy());
                            miner.getInventory().setItem(i, ItemStack.EMPTY);
                            break;
                        }
                    }
                }
            }
        }
    }
}
