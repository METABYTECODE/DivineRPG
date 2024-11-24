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

public class ItemModShovel extends ShovelItem {
	public Optional<Integer> nameColor;
    //Base constructor
    public ItemModShovel(Tier tier, Properties properties) {
        super(tier, properties.attributes(ShovelItem.createAttributes(tier, 0, -3)));
    }
    //Base shovels
    public ItemModShovel(Tier tier) {this(tier, new Properties());}
    //Shovels with custom rarity
    public ItemModShovel(Tier tier, int rarity) {
        this(tier, new Properties());
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