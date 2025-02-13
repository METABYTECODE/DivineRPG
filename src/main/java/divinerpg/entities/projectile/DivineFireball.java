package divinerpg.entities.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class DivineFireball extends Fireball {
	public byte explosionPower;
    public DivineFireball(EntityType<? extends DivineFireball> type, Level world) {
        super(type, world);
        explosionPower = 0;
    }
    public DivineFireball(EntityType<? extends DivineFireball> type, Level world, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, new Vec3(accelX, accelY, accelZ), world);
        setOwner(shooter);
        explosionPower = 0;
    }
    public DivineFireball(EntityType<? extends DivineFireball> type, Level world, LivingEntity shooter, double accelX, double accelY, double accelZ, byte explosionPower) {
        super(type, shooter, new Vec3(accelX, accelY, accelZ), world);
        setOwner(shooter);
        this.explosionPower = explosionPower;
    }
    @Override
    public void tick() {
        super.tick();
        if(tickCount > 200) kill();
    }
    //Copying LargeFireball code
    protected void onHit(HitResult result) {
    	if(tickCount > 1) {
	        super.onHit(result);
	        if(!level().isClientSide() && explosionPower > 0) {
	           // TODO 1.19.3: The creation of Level.ExplosionInteraction means this code path will fire EntityMobGriefingEvent twice. Should we try and fix it? -SS
	           boolean flag = EventHooks.canEntityGrief(level(), getOwner());
	           level().explode(this, getX(), getY(), getZ(), explosionPower, flag, Level.ExplosionInteraction.MOB);
	           discard();
	        }
    	}
    }
    protected void onHitEntity(EntityHitResult result) {
    	if(tickCount > 1) {
	        super.onHitEntity(result);
	        if(level() instanceof ServerLevel level) {
	           Entity entity = result.getEntity();
	           Entity entity1 = getOwner();
	           DamageSource source = damageSources().fireball(this, entity1);
	           entity.hurt(source, 6F);
	           if(entity1 instanceof LivingEntity) EnchantmentHelper.doPostAttackEffects(level, entity1, source);
	        }
    	}
    }
    public void addAdditionalSaveData(CompoundTag tag) {
       super.addAdditionalSaveData(tag);
       tag.putByte("ExplosionPower", explosionPower);
    }
    public void readAdditionalSaveData(CompoundTag tag) {
       super.readAdditionalSaveData(tag);
       if(tag.contains("ExplosionPower", 99)) explosionPower = tag.getByte("ExplosionPower");
    }
}
