package divinerpg.blocks.base;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.common.IShearable;

import static net.minecraft.tags.BlockTags.SAND;
import static net.minecraft.world.level.block.Blocks.SHORT_GRASS;
import static net.minecraft.world.level.block.SoundType.CROP;

public class BlockModGrass extends BushBlock implements IShearable {
    private final boolean canGrowOnSand;
    public BlockModGrass(MapColor color, boolean canGrowOnSand) {
        super(Properties.ofFullCopy(SHORT_GRASS).mapColor(color).sound(CROP).offsetType(OffsetType.XZ));
        this.canGrowOnSand = canGrowOnSand;
    }
    public BlockModGrass(MapColor color) {this(color, false);}
    @Override protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return canGrowOnSand ? super.mayPlaceOn(state, level, pos) || state.is(SAND) : super.mayPlaceOn(state, level, pos);
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(2, 0, 2, 14, 13, 14);
    }
    @Override public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction face) {return 100;}
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction face) {return 60;}
	@Override protected MapCodec<? extends BushBlock> codec() {
		//TODO: Auto-generated method stub
		return null;
	}
}