package divinerpg.entities.goals;

import divinerpg.entities.vanilla.overworld.EntityMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class FindOreGoal extends Goal {
    private final Monster miner;
    private final double speedModifier;
    private BlockPos targetOrePos;
    private BlockPos chestPos;

    private int miningTime;
    private static final int MAX_MINING_TIME = 100;

    private MoveToChestGoal moveToChestGoal;

    public FindOreGoal(Monster miner, double speedModifier, BlockPos chestPos) {
        this.miner = miner;
        this.speedModifier = Math.max(speedModifier, 0.5);
        this.chestPos = chestPos;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.miner.getTarget() != null || !this.miner.isAlive()) {
            return false;
        }
        targetOrePos = findClosestOre();
        return targetOrePos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return targetOrePos != null
                && this.miner.isAlive()
                && this.miner.level().getBlockState(targetOrePos).is(BlockTags.create(ResourceLocation.parse("c:ores")));
    }

    @Override
    public void start() {
        if (targetOrePos != null) {
            Path path = this.miner.getNavigation().createPath(targetOrePos, 1);
            if (path != null) {
                this.miner.getNavigation().moveTo(path, speedModifier);
            } else {
                targetOrePos = null;
            }
        }
    }

    @Override
    public void stop() {
        this.miner.getNavigation().stop();
        targetOrePos = null;
        miningTime = 0;
    }

    @Override
    public void tick() {
        if (targetOrePos != null) {
            double distanceToOre = this.miner.distanceToSqr(Vec3.atCenterOf(targetOrePos));
            this.miner.getLookControl().setLookAt(targetOrePos.getX() + 0.5, targetOrePos.getY() + 0.5, targetOrePos.getZ() + 0.5);

            if (distanceToOre < 9.0D) {
                if (miningTime == 0) {
                    mineOre(targetOrePos);
                } else if (miningTime < MAX_MINING_TIME) {
                    mineOre(targetOrePos);
                }
            } else if (!this.miner.getNavigation().isInProgress()) {
                this.miner.getNavigation().moveTo(targetOrePos.getX(), targetOrePos.getY(), targetOrePos.getZ(), speedModifier);
            }
        } else {
            if (chestPos != null && moveToChestGoal == null) {
                moveToChestGoal = new MoveToChestGoal((EntityMiner) miner, chestPos, speedModifier);
                moveToChestGoal.start();
            }
        }
    }

    private void mineOre(BlockPos pos) {
        if (this.miner.level().getBlockState(pos).is(BlockTags.create(ResourceLocation.parse("c:ores")))) {
            Block block = this.miner.level().getBlockState(pos).getBlock();
            float blockHardness = block.defaultDestroyTime();
            int requiredMiningTime = (int) (blockHardness * 10);
            if (miningTime < requiredMiningTime) {
                this.miner.swing(InteractionHand.MAIN_HAND);
                this.miner.level().levelEvent(2001, pos, Block.getId(this.miner.level().getBlockState(pos)));
                miningTime++;
            } else {
                block.dropResources(this.miner.level().getBlockState(pos), this.miner.level(), pos, null, this.miner, this.miner.getMainHandItem());
                this.miner.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                miningTime = 0;
                stop();
            }
        }
    }

    public BlockPos findClosestOre() {
        List<BlockPos> visibleOres = getNearbyBlocks(this.miner, BlockTags.create(ResourceLocation.parse("c:ores")),
                this.miner.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE).getValue());
        if (visibleOres.isEmpty()) {
            return null;
        }
        visibleOres.sort(Comparator.comparingDouble(pos -> pos.distSqr(new Vec3i((int) this.miner.position().x, (int) this.miner.position().y, (int) this.miner.position().z)))); // Sorting by distance to eye position
        for (BlockPos orePos : visibleOres) {
            Vec3 start = this.miner.position().add(0, this.miner.getEyeHeight(), 0);
            Vec3 end = Vec3.atCenterOf(orePos);
            if (start.distanceTo(end) <= 4.0D) {
                BlockHitResult result = this.miner.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.miner));
                if (result.getType() == BlockHitResult.Type.BLOCK && result.getBlockPos().equals(orePos)) {
                    if (isOreAccessible(orePos)) {
                        return orePos;
                    }
                }
            }
        }
        return null;
    }

    private boolean isOreAccessible(BlockPos orePos) {
        Vec3 start = this.miner.position();
        Vec3 end = Vec3.atCenterOf(orePos);
        BlockHitResult result = this.miner.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.miner));
        return result.getType() == BlockHitResult.Type.BLOCK && result.getBlockPos().equals(orePos) && start.distanceTo(end) <= 4.0D;
    }

    public static List<BlockPos> getNearbyBlocks(Entity entity, TagKey<Block> tag, double range) {
        Level level = entity.level();
        BlockPos entityPos = entity.blockPosition();
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int intRange = (int) Math.ceil(range);
        for (int x = -intRange; x <= intRange; x++) {
            for (int y = -intRange; y <= intRange; y++) {
                for (int z = -intRange; z <= intRange; z++) {
                    mutablePos.set(entityPos.getX() + x, entityPos.getY() + y, entityPos.getZ() + z);
                    if (level.getBlockState(mutablePos).is(tag)) {
                        blocks.add(mutablePos.immutable());
                    }
                }
            }
        }
        return blocks;
    }
}
