package divinerpg.client.screen;

import divinerpg.DivineRPG;
import divinerpg.client.menu.OceanfireFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OceanfireFurnaceScreen extends InfiniFurnaceScreen<OceanfireFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "textures/gui/oceanfire_furnace.png");
    public static final ResourceLocation OCEAN_FIRE_SPRITE = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "ocean_fire");
    public OceanfireFurnaceScreen(OceanfireFurnaceMenu menu, Inventory inv, Component c) {
        super(menu, inv, c, TEXTURE, 1980988, 1980988, OCEAN_FIRE_SPRITE);
    }
}