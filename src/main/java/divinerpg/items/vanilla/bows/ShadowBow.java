package divinerpg.items.vanilla.bows;

import divinerpg.entities.projectile.DivineArrow;
import divinerpg.entities.projectile.arrows.ShadowArrow;
import divinerpg.items.base.ItemBow;
import divinerpg.registries.ItemRegistry;
import divinerpg.util.LocalizeUtils;
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

public class ShadowBow extends ItemBow {
    public ShadowBow() {
        super(new Properties(), 1225, 36000, 1F, null, 0x696969);
    }
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        switch(arrow) {
            case ShadowArrow h -> {
                h.powerMultiplier = 1.2F;
                h.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1));
                return arrow;
            } case Arrow a -> a.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1));
            case DivineArrow a -> a.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1));
            default -> {}
        } return arrow;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.blind(4));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return new ItemStack(ItemRegistry.shadow_arrow.get());
    }
}