package divinerpg.client.menu;

import divinerpg.registries.MenuTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeType;

public class MoonlightFurnaceMenu extends AbstractFurnaceMenu {
    public MoonlightFurnaceMenu(int i, Inventory inventory) {
        super(MenuTypeRegistry.MOONLIGHT_FURNACE.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, i, inventory);
    }
    public MoonlightFurnaceMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(MenuTypeRegistry.MOONLIGHT_FURNACE.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, id, inventory, container, data);

    }
    public MoonlightFurnaceMenu(int i, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(i, playerInventory);
    }
}