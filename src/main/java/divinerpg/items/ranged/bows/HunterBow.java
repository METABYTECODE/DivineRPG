package divinerpg.items.ranged.bows;

import divinerpg.entities.projectile.DivineArrow;
import divinerpg.entities.projectile.arrows.HunterArrow;
import divinerpg.items.ranged.ItemBow;
import divinerpg.registries.ItemRegistry;
import divinerpg.util.LocalizeUtils;
import divinerpg.util.RarityList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HunterBow extends ItemBow {
    int effectSeconds;
    public HunterBow() {
        super(new Properties(), 1125, 72000, 1.2F, null, RarityList.GREEN);
        effectSeconds = 2;
    }
    @Override public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        switch(arrow) {
            case HunterArrow h -> {
                h.powerMultiplier = 1.2F;
                return arrow;
            } case Arrow a -> a.addEffect(new MobEffectInstance(MobEffects.POISON, effectSeconds * 20, 3));
            case DivineArrow a -> a.addEffect(new MobEffectInstance(MobEffects.POISON, effectSeconds * 20, 3));
            default -> {}
        } return arrow;
    }
    @Override public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return new ItemStack(ItemRegistry.hunter_arrow.get());
    }
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.poison(effectSeconds));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}