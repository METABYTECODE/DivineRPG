package divinerpg.compat.jei.category;


import divinerpg.DivineRPG;
import divinerpg.recipe.ArcaniumExtractorRecipe;
import divinerpg.registries.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ArcaniumExtractorCategory
        implements IRecipeCategory<ArcaniumExtractorRecipe>
{

    public static final ResourceLocation ARCANIUM_EXTRACTOR = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "textures/gui/jei/arcanium_extractor.png");
    public static final RecipeType<ArcaniumExtractorRecipe> ARCANIUM_EXTRACTOR_TYPE = RecipeType.create(DivineRPG.MODID, "arcanium_extractor", ArcaniumExtractorRecipe.class);

    private final IDrawable back, icon;

    public ArcaniumExtractorCategory(IGuiHelper helper) {
        this.back = helper.createDrawable(ARCANIUM_EXTRACTOR, 1, 1, 167, 78);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.arcaniumExtractor.get()));
    }


    @Override
    public RecipeType<ArcaniumExtractorRecipe> getRecipeType() {
        return RecipeType.create(DivineRPG.MODID, "arcanium_extractor", ArcaniumExtractorRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable(BlockRegistry.arcaniumExtractor.get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return back;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("resource")
	@Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArcaniumExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 12).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.CATALYST, 51, 48).addItemStack(new ItemStack(ItemRegistry.collector.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 30).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

}
