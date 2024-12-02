package divinerpg.entities.projectile.magic;

import divinerpg.entities.projectile.DivineThrowableProjectile;
import divinerpg.util.DamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class DivineArcanaProjectile extends DivineThrowableProjectile {
    public DivineArcanaProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level, float damage) {
        super(entityType, level, damage);
    }
    public DivineArcanaProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }
    @Override
    public DamageSource getDamageSource(EntityHitResult result) {
        return damageSources().source(DamageSources.ARCANA, getOwner());
    }
}