package divinerpg.items.ranged.shooter;

import divinerpg.entities.projectile.DivineThrowableProjectile;
import divinerpg.items.ranged.ItemRangedWeapon;
import divinerpg.registries.EntityRegistry;
import divinerpg.registries.ItemRegistry;
import divinerpg.registries.SoundRegistry;
import divinerpg.util.LocalizeUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VetheanCannon extends ItemRangedWeapon {
    final float damage;
    public VetheanCannon(float damage) {
        super("acid", ItemRegistry.acid::toStack, EntityRegistry.CANNON_SHOT::value);
        sound = SoundRegistry.BLITZ.get();
        arcanaConsumedUse = 10;
        this.damage = damage;
        tooltips.add(LocalizeUtils.rangedDam(damage));
    }
    @Override
    protected DivineThrowableProjectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        DivineThrowableProjectile p = (DivineThrowableProjectile) super.createProjectile(level, shooter, weapon, ammo, isCrit);
        p.baseDamage = damage;
        return p;
    }
}
