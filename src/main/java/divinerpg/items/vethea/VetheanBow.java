package divinerpg.items.vethea;

import divinerpg.items.base.ItemBow;
import divinerpg.registries.SoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;
import java.util.function.Supplier;

public class VetheanBow extends ItemBow {
    public VetheanBow(Properties properties, int uses, int useDuration, float speedScale, Supplier<Item> infinityArrow, Integer nameColor) {
        super(properties, uses, useDuration, speedScale, infinityArrow, nameColor);
    }
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving instanceof Player player) {
            ItemStack itemstack = player.getProjectile(stack);
            if(infinityArrow != null && (itemstack.isEmpty() || itemstack.is(infinityArrow.get()))) itemstack = new ItemStack(infinityArrow.get());
            if(itemstack.isEmpty()) return;
            int i = EventHooks.onArrowLoose(stack, level, player, getUseDuration(stack, entityLiving) - timeLeft, true);
            if(i < 0) return;
            float f = getPowerForTime(i);
            if(f >= .1F) {
                List<ItemStack> list = draw(stack, itemstack, player);
                if(level instanceof ServerLevel serverlevel && !list.isEmpty()) shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3F * speedScale, 1F, f == 1F, null);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.VETHEA_BOW, SoundSource.PLAYERS, 1F, 1F / (level.getRandom().nextFloat() * .4F + 1.2F) + f * .5F);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }
}
