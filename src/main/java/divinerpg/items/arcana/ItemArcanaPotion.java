package divinerpg.items.arcana;

import divinerpg.attachments.Arcana;
import divinerpg.items.base.ItemModFood;
import divinerpg.util.LocalizeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.*;
import java.util.List;

public class ItemArcanaPotion extends ItemModFood {
    protected int amountToAdd;
    public ItemArcanaPotion(FoodProperties list, int amountToAdd) {
        super(new Properties().food(list));
        this.amountToAdd = amountToAdd;
    }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    	if(!worldIn.isClientSide()) {
    		if(Arcana.hasArcana(entityLiving)) Arcana.modifyAmount(entityLiving, amountToAdd);
    		if(entityLiving instanceof ServerPlayer player) {
    			player.awardStat(Stats.ITEM_USED.get(this));
                stack.consume(1, player);
			}
    	} return stack;
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) {return UseAnim.DRINK;}
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {return 20;}
    @Override public boolean isFoil(ItemStack stack) {return true;}
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.arcanaRegen(amountToAdd));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}