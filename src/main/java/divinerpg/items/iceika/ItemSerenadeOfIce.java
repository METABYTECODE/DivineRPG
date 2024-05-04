package divinerpg.items.iceika;

import divinerpg.enums.BulletType;
import divinerpg.items.base.ItemModRanged;
import divinerpg.registries.SoundRegistry;
import divinerpg.util.LocalizeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.*;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSerenadeOfIce extends ItemModRanged {
    public ItemSerenadeOfIce() {super(BulletType.SERENADE_OF_ICE_SHOT, SoundRegistry.SERENADE.get(), 100, 0);}
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.slow(5));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}