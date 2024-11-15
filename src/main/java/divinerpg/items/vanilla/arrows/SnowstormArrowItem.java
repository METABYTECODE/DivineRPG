package divinerpg.items.vanilla.arrows;

import divinerpg.entities.projectile.arrows.SnowstormArrow;
import divinerpg.items.base.ItemDivineArrow;
import divinerpg.util.LocalizeUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SnowstormArrowItem extends ItemDivineArrow {
    public SnowstormArrowItem() {
        super(new Properties());
    }
    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return new SnowstormArrow(level, shooter, ammo.copyWithCount(1), weapon);
    }
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new SnowstormArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(LocalizeUtils.explosiveShots());
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}