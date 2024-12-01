package divinerpg.items.vanilla;

import divinerpg.items.ranged.ItemThrowable;
import divinerpg.util.FoodList;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemTomato extends ItemThrowable {
    public ItemTomato() {
        super(new Properties().food(FoodList.TOMATO), .5F);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(player.isShiftKeyDown()) return super.use(world, player, hand);
        else if(player.canEat(stack.getFoodProperties(player).canAlwaysEat())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else return InteractionResultHolder.pass(stack);
    }
}