package divinerpg.items.ranged.staffs;

import divinerpg.registries.*;
import divinerpg.util.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Evernight extends VetheanStaff {
    public Evernight() {
        super(EntityRegistry.EVERNIGHT_SHOT::value, 80, 115F);
        tooltips.add(LocalizeUtils.onUseDam(19));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        if(result.getResult().consumesAction() && !player.isCreative()) player.hurt(level.damageSources().source(DamageRegistry.ARCANA.getKey()), 19);
        return result;
    }
}