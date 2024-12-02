package divinerpg.entities.projectile.magic;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public class EntityBouncingProjectile extends DivineArcanaProjectile {
    int bounces = 0;
    public EntityBouncingProjectile(EntityType<? extends ThrowableProjectile> type, Level world) {super(type, world);}
    @Override public void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Vec3 mv = getDeltaMovement();
        double x = mv.x, y = mv.y, z = mv.z;
        if(result.getDirection() == Direction.DOWN || result.getDirection() == Direction.UP) setDeltaMovement(x * 0.8, y * -0.8, z * 0.8);
        else if(result.getDirection() == Direction.EAST || result.getDirection() == Direction.WEST) setDeltaMovement(x * -0.8, y * 0.8, z * 0.8);
        else if(result.getDirection() == Direction.NORTH || result.getDirection() == Direction.SOUTH) setDeltaMovement(x * 0.8, y * 0.8, z * -0.8);
        if(bounces > 6) discard();
        bounces++;
    }
}