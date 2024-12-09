package divinerpg.entities.projectile.throwable;

import divinerpg.DivineRPG;
import divinerpg.entities.projectile.DivineThrownItem;
import divinerpg.items.ranged.ItemThrowable;
import divinerpg.registries.ItemRegistry;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

public class EntityDisk extends DivineThrownItem {
    public EntityDisk(EntityType<? extends DivineThrownItem> type, Level world) {super(type, world);}
    @Override public void tick() {
        super.tick();
        Entity owner = getOwner();
        if(owner != null && (tickCount & 7) == 7) {
            Vec3 vec3 = owner.getEyePosition().subtract(position());
            setPosRaw(getX(), getY() + vec3.y * 0.03, getZ());
            if(level().isClientSide) yOld = getY();
            addDeltaMovement(vec3.scale(0.25));
            playSound(SoundEvents.ARROW_SHOOT, 0.5F, 1F);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if(getOwner() == null) super.onHitBlock(result);
        else {
            BlockState blockstate = level().getBlockState(result.getBlockPos());
            blockstate.onProjectileHit(level(), blockstate, result, this);
            Vec3 mv = getDeltaMovement();
            double x = mv.x, y = mv.y, z = mv.z;
            if(result.getDirection() == Direction.DOWN || result.getDirection() == Direction.UP) lerpMotion(x * 0.6, y * -0.6, z * 0.6);
            else if(result.getDirection() == Direction.EAST || result.getDirection() == Direction.WEST) lerpMotion(x * -0.6, y * 0.6, z * 0.6);
            else if(result.getDirection() == Direction.NORTH || result.getDirection() == Direction.SOUTH) lerpMotion(x * 0.6, y * 0.6, z * -0.6);
        }
    }

    @Override public void onHitEntity(EntityHitResult result) {
        Entity entity1 = getOwner(), entity = result.getEntity();
        if(entity1.equals(entity) && entity1 instanceof Player p) {
            if(canPickup) p.addItem(getItem());
            discard();
            return;
        } ItemStack item = getItem();
        float f = (float)getDeltaMovement().length(), d0 = item.isEmpty() || !(item.getItem() instanceof ItemThrowable t) ? 1F : t.damage;
        DamageSource damagesource = getDamageSource(result);
        if(getWeaponItem() != null && level() instanceof ServerLevel s) d0 = EnchantmentHelper.modifyDamage(s, getWeaponItem(), entity, damagesource, d0);
        int j = Mth.ceil(Mth.clamp(f * d0, 0D, Integer.MAX_VALUE));
        if(entity1 instanceof LivingEntity livingentity1) livingentity1.setLastHurtMob(entity);
        if(entity.hurt(damagesource, j)) {
            if(isOnFire()) entity.igniteForSeconds(5F);
            if(entity instanceof LivingEntity livingentity) {
                if(level() instanceof ServerLevel serverlevel1) EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, getWeaponItem());
                doPostHurtEffects(livingentity);
            }
        } deflect(ProjectileDeflection.REVERSE, entity, getOwner(), false);
        setDeltaMovement(getDeltaMovement().scale(.2));
    }
    @Override public boolean isNoGravity() {return true;}
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.teaker_disk.get();
    }
}