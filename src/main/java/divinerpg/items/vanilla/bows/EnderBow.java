package divinerpg.items.vanilla.bows;

import divinerpg.entities.projectile.DivineArrow;
import divinerpg.entities.projectile.arrows.EnderArrow;
import divinerpg.items.base.ItemBow;
import divinerpg.registries.ItemRegistry;
import divinerpg.registries.MobEffectRegistry;
import divinerpg.util.LocalizeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderBow extends ItemBow {
    public EnderBow() {
        super(new Properties(), 0, 72000, 1.2F, ItemRegistry.ender_arrow, 8073150);
    }
    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        if(arrow instanceof DivineArrow a) {
            a.addEffect(new MobEffectInstance(MobEffectRegistry.ENDER_ATTACHMENT, 1));
            if(a instanceof EnderArrow) a.powerMultiplier = 1.2F;
        } return arrow;
    }
    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return new ItemStack(ItemRegistry.ender_arrow.get());
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.teleportAttached());
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}