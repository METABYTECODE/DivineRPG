package divinerpg.items.vanilla.arrows;

import divinerpg.entities.projectile.arrows.InfernoArrow;
import divinerpg.items.base.ItemDivineArrow;
import divinerpg.util.LocalizeUtils;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfernoArrowItem extends ItemDivineArrow {
    public InfernoArrowItem() {
        super(new Properties());
    }
    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new InfernoArrow(level, shooter, ammo.copyWithCount(1), weapon);
    }
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractArrow arrow = new InfernoArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(LocalizeUtils.burn(12));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}