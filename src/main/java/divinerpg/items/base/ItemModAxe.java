package divinerpg.items.base;

import divinerpg.util.LocalizeUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.*;
import java.util.*;

import static net.minecraft.tags.BlockTags.*;
import static net.minecraft.world.item.Items.*;

public class ItemModAxe extends AxeItem {
	public Optional<Integer> nameColor;
    //Base constructor
    public ItemModAxe(Tier tier, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(AxeItem.createAttributes(tier, 0, attackSpeed)));
    }
    //Base axes
    public ItemModAxe(Tier tier, float attackSpeed) {this(tier, attackSpeed, new Properties());}
    //Axes with custom rarity
    public ItemModAxe(Tier tier, float attackSpeed, int rarity) {
        this(tier, attackSpeed, new Properties());
        nameColor = Optional.of(rarity);
    }
	@OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.efficiency((int)getTier().getSpeed()));
        TagKey<Block> tagKey = getTier().getIncorrectBlocksForDrops();
        if(tagKey == INCORRECT_FOR_DIAMOND_TOOL || tagKey == INCORRECT_FOR_NETHERITE_TOOL) tooltip.add(LocalizeUtils.harvestLevel(OBSIDIAN.asItem().getName(stack)));
        else if(tagKey == INCORRECT_FOR_IRON_TOOL) tooltip.add(LocalizeUtils.harvestLevel(DIAMOND.getName(stack)));
        if(stack.getMaxDamage() == 0) stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
    }
	@Override public Component getName(ItemStack pStack) {
    	return nameColor != null && nameColor.isPresent() ? ((MutableComponent) super.getName(pStack)).withColor(nameColor.get()) : super.getName(pStack);
    }
}