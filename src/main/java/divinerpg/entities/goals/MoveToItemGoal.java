package divinerpg.entities.goals;

import divinerpg.entities.vanilla.overworld.EntityMiner;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;

public class MoveToItemGoal extends Goal {
    private final EntityMiner miner;
    private ItemEntity targetItem;
    private final double speed;

    public MoveToItemGoal(EntityMiner miner, double speed) {
        this.miner = miner;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Find the nearest ItemEntity in range
        ItemEntity closestItem = this.miner.level().getEntitiesOfClass(ItemEntity.class, miner.getBoundingBox().inflate(5.0D))
                .stream().min((item1, item2) -> Double.compare(miner.distanceTo(item1), miner.distanceTo(item2)))
                .orElse(null);

        if (closestItem != null && miner.distanceTo(closestItem) > 1.0D) {
            this.targetItem = closestItem;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        // Start moving towards the item
        miner.getNavigation().moveTo(targetItem, speed);
    }

    @Override
    public boolean canContinueToUse() {
        // Continue as long as the miner is still moving towards the item or until it's picked up
        return targetItem != null && miner.distanceTo(targetItem) > 1.0D && !miner.getNavigation().isDone();
    }

    @Override
    public void stop() {
        // Once the miner has reached the item, pick it up and stop the movement
        if (targetItem != null) {
            miner.pickUpDroppedItem(targetItem);
        }
    }
}
