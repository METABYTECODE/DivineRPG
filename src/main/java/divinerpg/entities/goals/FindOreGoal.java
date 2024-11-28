package divinerpg.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    private boolean isStuck;
    private boolean isMiningTunnel;
    private BlockPos tunnelTargetPos;

    public FindOreGoal(Monster miner, double speedModifier, BlockPos chestPos) {
        this.miner = miner;
        this.speedModifier = Math.max(speedModifier, 0.5);
        this.chestPos = chestPos;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.miner.getTarget() != null || !this.miner.isAlive()) {
            return false;
        }
        targetOrePos = findClosestOre();
        return targetOrePos != null || (chestPos != null && isStuck);
    }

    @Override
    public boolean canContinueToUse() {
        return (targetOrePos != null && this.miner.level().getBlockState(targetOrePos).is(BlockTags.create(ResourceLocation.parse("c:ores"))))
                || isMiningTunnel;
    }

    @Override
    public void start() {
        isStuck = false;
        isMiningTunnel = false;
        moveToTargetOre();
    }

    @Override
    public void stop() {
        this.miner.getNavigation().stop();
        targetOrePos = null;
        miningTime = 0;
        isMiningTunnel = false;
        isStuck = false;
    }

    @Override
    public void tick() {
        if (targetOrePos != null) {
            double distanceToOre = this.miner.distanceToSqr(Vec3.atCenterOf(targetOrePos));
            this.miner.getLookControl().setLookAt(targetOrePos.getX() + 0.5, targetOrePos.getY() + 0.5, targetOrePos.getZ() + 0.5);

            if (distanceToOre < 9.0D) {
                mineOre(targetOrePos);
            } else if (!this.miner.getNavigation().isInProgress()) {
                isStuck = checkIfStuck();
                if (isStuck) {
                    targetOrePos = null;
                    tunnelTargetPos = chestPos;
                    startTunnelMining();
                } else {
                    moveToTargetOre();
                }
            }
        } else if (isMiningTunnel) {
            mineTunnel();
        }
        if(isStuck){
            tryToEscape();
        }
    }

    private boolean checkIfStuck() {
        BlockPos minerPos = this.miner.blockPosition();
        int freeSpaceCount = 0;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = minerPos.offset(x, y, z);
                    if (this.miner.level().getBlockState(checkPos).isAir()) {
                        freeSpaceCount++;
                    }
                }
            }
        }

        boolean hasFreeSpaceAbove = this.miner.level().getBlockState(minerPos.above()).isAir();

        if (freeSpaceCount < 5 && hasFreeSpaceAbove) {
            tryToEscape();
            return false;
        }

        return freeSpaceCount < 5;
    }

    private void tryToEscape() {
        BlockPos minerPos = this.miner.blockPosition();
        BlockPos abovePos = minerPos.above();
        if (this.miner.distanceToSqr(Vec3.atCenterOf(abovePos)) < 2.0D) {
            if (this.miner.level().getBlockState(abovePos).isAir()) {
                Block blockToPlace = (this.miner.level().getMinBuildHeight() > 0 && minerPos.getY() >= 0) ? Blocks.COBBLESTONE : Blocks.COBBLED_DEEPSLATE;

                this.miner.jumpFromGround();
                this.miner.level().setBlock(minerPos, blockToPlace.defaultBlockState(), 3);
                isStuck = false;
            }
        }
    }

    private void moveToTargetOre() {
        if (targetOrePos != null) {
            this.miner.getNavigation().moveTo(targetOrePos.getX(), targetOrePos.getY(), targetOrePos.getZ(), speedModifier);
        }
    }

    private void startTunnelMining() {
        if (tunnelTargetPos != null) {
            isMiningTunnel = true;
        }
    }

    private void mineTunnel() {
        BlockPos minerPos = this.miner.blockPosition();
        Vec3 direction = Vec3.atLowerCornerOf(tunnelTargetPos.subtract(minerPos)).normalize();
        int dx = (int) Math.signum(direction.x);
        int dy = (int) Math.signum(direction.y);
        int dz = (int) Math.signum(direction.z);

        BlockPos nextPos = minerPos.offset(dx, dy, dz);
        if (this.miner.level().getBlockState(nextPos).isAir()) {
            this.miner.getNavigation().moveTo(Vec3.atCenterOf(nextPos).x, Vec3.atCenterOf(nextPos).y, Vec3.atCenterOf(nextPos).z, speedModifier);
        } else {
            mineBlock(nextPos);
        }
    }

    private void mineOre(BlockPos pos) {
        if (this.miner.level().getBlockState(pos).is(BlockTags.create(ResourceLocation.parse("c:ores")))) {
            Block block = this.miner.level().getBlockState(pos).getBlock();
            float hardness = block.defaultDestroyTime();
            if (miningTime < hardness * 10) {
                miningTime++;
                this.miner.swing(InteractionHand.MAIN_HAND);
                this.miner.level().levelEvent(2001, pos, Block.getId(this.miner.level().getBlockState(pos)));
            } else {
                block.dropResources(this.miner.level().getBlockState(pos), this.miner.level(), pos, null, this.miner, this.miner.getMainHandItem());
                this.miner.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                miningTime = 0;
            }
        }
    }

    private void mineBlock(BlockPos pos) {
        if (!this.miner.level().getBlockState(pos).isAir()) {
            Block block = this.miner.level().getBlockState(pos).getBlock();
            float hardness = block.defaultDestroyTime();
            if (miningTime < hardness * 10) {
                miningTime++;
                this.miner.swing(InteractionHand.MAIN_HAND);
                this.miner.level().levelEvent(2001, pos, Block.getId(this.miner.level().getBlockState(pos)));
            } else {
                this.miner.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                miningTime = 0;
            }
        }
    }

    private BlockPos findClosestOre() {
        double searchRadius = this.miner.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE).getValue();
        List<BlockPos> nearbyOres = getNearbyBlocks(this.miner, BlockTags.create(ResourceLocation.parse("c:ores")), searchRadius);
        if (nearbyOres.isEmpty()) {
            return null;
        }
        return nearbyOres.stream().min(Comparator.comparingDouble(ore -> this.miner.distanceToSqr(ore.getX(), ore.getY(), ore.getZ()))).orElse(null);
    }

    private List<BlockPos> getNearbyBlocks(Entity entity, TagKey<Block> blockTag, double searchRadius) {
        Level level = entity.level();
        BlockPos entityPos = entity.blockPosition();
        List<BlockPos> blocks = new ArrayList<>();

        for (int x = (int) (entityPos.getX() - searchRadius); x <= entityPos.getX() + searchRadius; x++) {
            for (int y = (int) (entityPos.getY() - searchRadius); y <= entityPos.getY() + searchRadius; y++) {
                for (int z = (int) (entityPos.getZ() - searchRadius); z <= entityPos.getZ() + searchRadius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockState(pos).is(blockTag)) {
                        blocks.add(pos);
                    }
                }
            }
        }
        return blocks;
    }
}
