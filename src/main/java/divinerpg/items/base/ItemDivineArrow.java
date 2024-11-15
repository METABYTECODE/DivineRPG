package divinerpg.items.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;

public class ItemDivineArrow extends ArrowItem {
    public ItemDivineArrow(Properties properties) {
        super(properties);
    }
    public boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity livingEntity) {
        return bow.getItem() instanceof ItemBow b && b.infinityArrow != null && b.infinityArrow.get() == this;
    }
}