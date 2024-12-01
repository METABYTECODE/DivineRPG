package divinerpg.entities.projectile.bullet;

import divinerpg.entities.boss.EntityKitra;
import divinerpg.entities.projectile.DivineThrowableProjectile;
import divinerpg.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BoneBomb extends DivineThrowableProjectile {
    public BoneBomb(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        baseDamage = 16F;
    }
    @Override
    public void onHitEntity(EntityHitResult result) {
        if(!(result.getEntity() instanceof EntityKitra)) super.onHitEntity(result);
    }
    @Override
    public void onHit(HitResult result) {
        super.onHit(result);
        if(result.getType() != HitResult.Type.MISS) {
            for(int i = 0; i < 64; i++) {
                double motionX = (random.nextDouble() - .5) * 2D, motionY = (random.nextDouble() - .5) * 2D, motionZ = (random.nextDouble() - .5) * 2D;
                BoneFragment e = EntityRegistry.BONE_FRAGMENT.get().create(level());
                e.setOwner(getOwner());
                e.setPos(result.getLocation());
                e.shoot(motionX, motionY, motionZ, 1F, 0F);
                level().addFreshEntity(e);
            } discard();
        }
    }
    @Override
    public void tick() {
        super.tick();
        noPhysics = isInWater();
        double radius = getBbWidth() * 1.5;
        AABB aabb = new AABB(getX() - radius, getY() - radius, getZ() - radius, getX() + radius, getY() + radius, getZ() + radius);
        BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
            BlockState blockState = level().getBlockState(blockPos);
            if(blockState.is(BlockTags.ICE)) level().destroyBlock(blockPos, true);
        });
    }
}