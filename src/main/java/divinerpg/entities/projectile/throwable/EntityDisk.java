package divinerpg.entities.projectile.throwable;

import divinerpg.DivineRPG;
import divinerpg.entities.projectile.DivineThrownItem;
import divinerpg.items.ranged.ItemThrowable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
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
    @Override public void onHitEntity(EntityHitResult result) {
        Entity entity1 = getOwner(), entity = result.getEntity();
        if(entity1.equals(entity) && entity1 instanceof Player p) {
            if(!p.isCreative()) p.addItem(getItem());
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
}