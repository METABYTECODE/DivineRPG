package divinerpg.items.vanilla.arrows;

import divinerpg.entities.projectile.arrows.KarosArrow;
import divinerpg.items.base.ItemDivineArrow;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class KarosArrowItem extends ItemDivineArrow {
    public KarosArrowItem() {
        super(new Properties());
    }
    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new KarosArrow(level, shooter, ammo.copyWithCount(1), weapon);
    }
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractArrow arrow = new KarosArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
}