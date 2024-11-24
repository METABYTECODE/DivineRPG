package divinerpg.blocks.base;

import net.minecraft.world.level.material.MapColor;

import static net.minecraft.world.level.block.Blocks.SEA_LANTERN;

public class BlockModUnbreakable extends BlockMod {
    public BlockModUnbreakable(MapColor color) {super(color, -1, 3600000);}
    public BlockModUnbreakable(Properties properties) {super(properties.strength(-1, 3600000));}
    public BlockModUnbreakable(MapColor color, int lightLevel) {this(Properties.ofFullCopy(SEA_LANTERN).mapColor(color).lightLevel((state) -> lightLevel));}
}