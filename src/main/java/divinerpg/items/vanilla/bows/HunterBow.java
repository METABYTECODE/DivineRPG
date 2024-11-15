package divinerpg.items.vanilla.bows;

import divinerpg.entities.projectile.DivineArrow;
import divinerpg.entities.projectile.arrows.HunterArrow;
import divinerpg.items.base.ItemBow;
import divinerpg.registries.ItemRegistry;
import divinerpg.util.LocalizeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HunterBow extends ItemBow {
    public HunterBow() {
        super(new Properties(), 1125, 72000, 1.2F, null, 0xAA00);
    }
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        switch(arrow) {
            case HunterArrow h -> {
                h.powerMultiplier = 1.2F;
                return arrow;
            } case Arrow a -> a.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 3));
            case DivineArrow a -> a.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 3));
            default -> {}
        } return arrow;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.poison(2));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return new ItemStack(ItemRegistry.hunter_arrow.get());
    }
}
