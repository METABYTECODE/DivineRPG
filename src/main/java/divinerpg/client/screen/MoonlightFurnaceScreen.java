package divinerpg.client.screen;

import divinerpg.DivineRPG;
import divinerpg.client.menu.MoonlightFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MoonlightFurnaceScreen extends DivineFurnaceScreen<MoonlightFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "textures/gui/moonlight_furnace.png");
    public static final ResourceLocation MOONLIGHT_FLAME_SPRITE = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "moonlight_flame");
    public MoonlightFurnaceScreen(MoonlightFurnaceMenu menu, Inventory inv, Component c) {
        super(menu, inv, c, TEXTURE, 2565186, 2565186, MOONLIGHT_FLAME_SPRITE);
    }
}