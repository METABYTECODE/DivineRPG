package divinerpg.entities.projectile.fireball;

import divinerpg.entities.projectile.DivineFireball;
import divinerpg.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EntityScorcherShot extends DivineFireball {
    public EntityScorcherShot(EntityType<? extends DivineFireball> type, Level world) {
        super(type, world);
    }
    public EntityScorcherShot(Level world, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(EntityRegistry.SCORCHER_SHOT.get(), world, shooter, accelX, accelY, accelZ);
        moveTo(shooter.xo, shooter.yo, shooter.zo, shooter.getXRot(), shooter.getYRot());
        setPos(shooter.xo, shooter.yo, shooter.zo);
        double d = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        setDeltaMovement(accelX / d * 0.1D, accelY / d * 0.1D, accelZ / d * 0.1D);
        setOwner(shooter);
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if(tickCount > 1 && level() instanceof ServerLevel level) {
            Entity entity = result.getEntity();
            if(!entity.fireImmune()) {
                Entity entity1 = getOwner();
                int i = entity.getRemainingFireTicks();
                entity.igniteForSeconds(5);
                DamageSource source = damageSources().fireball(this, entity1);
                boolean flag = entity.hurt(source, 5F);
                if(!flag) entity.setRemainingFireTicks(i);
                else if(entity1 instanceof LivingEntity) EnchantmentHelper.doPostAttackEffects(level, entity1, source);
            }
        }
    }
    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide()) {
            xo += (random.nextDouble() - random.nextDouble()) / 3;
            yo += (random.nextDouble() - random.nextDouble()) / 3;
            zo += (random.nextDouble() - random.nextDouble()) / 3;
        }
        if(level().isClientSide()) for(int i = 0; i < 3; i++) level().addParticle(ParticleTypes.PORTAL, getX() + (random.nextDouble() - random.nextDouble()) / 5, getY() + .5 + (random.nextDouble() - random.nextDouble()) / 5, getZ() + (random.nextDouble() - random.nextDouble()) / 5, 0D, 0D, 0D);
    }
}
