package divinerpg.entities.projectile.bullet;

import divinerpg.entities.projectile.DivineThrowableProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class WatcherShot extends DivineThrowableProjectile {
    public WatcherShot(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        baseDamage = 4F;
    }
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if(!level().isClientSide()) level().explode(this, xo, yo, zo, 5, false, Level.ExplosionInteraction.MOB);
    }
}