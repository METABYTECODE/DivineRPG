package naturix.divinerpg.objects.items.base;

import java.util.List;

import javax.annotation.Nullable;

import naturix.divinerpg.DivineRPG;
import naturix.divinerpg.registry.DRPGCreativeTabs;
import naturix.divinerpg.registry.ModItems;
import naturix.divinerpg.utils.IHasModel;
import naturix.divinerpg.utils.TooltipLocalizer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModHoe extends ItemHoe implements IHasModel {

    private String name;

    public ItemModHoe(ToolMaterial material, String name) {
        super(material);
        this.name = name;
        setRegistryName(name);
        setUnlocalizedName(name);
        setCreativeTab(DRPGCreativeTabs.tools);

        ModItems.ITEMS.add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> infoList, ITooltipFlag flagIn) {
        if (stack.getMaxDamage() > 0) {
            infoList.add(TooltipLocalizer.usesRemaining(stack.getMaxDamage() - stack.getItemDamage()));
        } else {
            infoList.add(TooltipLocalizer.infiniteUses());
        }
    }

    @Override
    public void registerModels() {
        DivineRPG.proxy.registerItemRenderer(this, 0, name);
    }
}