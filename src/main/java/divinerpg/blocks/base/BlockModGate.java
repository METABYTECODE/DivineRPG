package divinerpg.blocks.base;

import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import static net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE;

public class BlockModGate extends FenceGateBlock {
    public BlockModGate(MapColor color, WoodType type) {super(type, Properties.ofFullCopy(OAK_FENCE_GATE).mapColor(color));}
    @Override public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction face) {return 20;}
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction face) {return 5;}
}