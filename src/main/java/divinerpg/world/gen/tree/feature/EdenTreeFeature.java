package divinerpg.world.gen.tree.feature;

import com.mojang.serialization.*;
import divinerpg.registries.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;

import java.util.*;
import java.util.function.*;

public class EdenTreeFeature extends DivineTreeFeature {

    public EdenTreeFeature(Codec<BlockStateFeatureConfig> codec, Supplier<SaplingBlock> saplingBlock) {
        super(codec, saplingBlock);
    }

    @Override
    protected boolean gen(ISeedReader world, Random rand, BlockPos pos) {
        int random = rand.nextInt(1) + 1;
        int treeHeight = 12;
        int extraHeight = treeHeight + random;

        if (!heightCheck(world, pos, extraHeight + 4, 1))
            return false;

        if (!canSustain(world, pos))
            return false;

        BlockPos.Mutable mut = new BlockPos.Mutable().set(pos.below());
        BlockState log = BlockRegistry.edenLog.defaultBlockState();
        BlockState leaves = BlockRegistry.edenLeaves.defaultBlockState();


        //Tree trunk
        for (int i = 0; i < extraHeight; i++) {
            setBlock(world, mut.move(Direction.UP), log);
        }
        chanceSetBlock(world, pos.offset(1, 0, 0), log, 1);
        chanceSetBlock(world, pos.offset(0, 0, 1), log, 1);
        chanceSetBlock(world, pos.offset(-1, 0, 0), log, 1);
        chanceSetBlock(world, pos.offset(0, 0, -1), log, 1);

        //Leaves
        setBlock(world, pos.offset(1, treeHeight - 3, 0), leaves);
        setBlock(world, pos.offset(1, treeHeight - 2, 0), leaves);
        setBlock(world, pos.offset(1, treeHeight - 1, 0), leaves);
        setBlock(world, pos.offset(1, treeHeight, 0), leaves);
        setBlock(world, pos.offset(1, treeHeight + 1, 0), leaves);
        setBlock(world, pos.offset(1, treeHeight + 2, 0), leaves);

        setBlock(world, pos.offset(-1, treeHeight - 3, 0), leaves);
        setBlock(world, pos.offset(-1, treeHeight - 2, 0), leaves);
        setBlock(world, pos.offset(-1, treeHeight - 1, 0), leaves);
        setBlock(world, pos.offset(-1, treeHeight, 0), leaves);
        setBlock(world, pos.offset(-1, treeHeight + 1, 0), leaves);
        setBlock(world, pos.offset(-1, treeHeight + 2, 0), leaves);

        setBlock(world, pos.offset(0, treeHeight - 3, 1), leaves);
        setBlock(world, pos.offset(0, treeHeight - 2, 1), leaves);
        setBlock(world, pos.offset(0, treeHeight - 1, 1), leaves);
        setBlock(world, pos.offset(0, treeHeight, 1), leaves);
        setBlock(world, pos.offset(0, treeHeight + 1, 1), leaves);

        setBlock(world, pos.offset(0, treeHeight - 3, -1), leaves);
        setBlock(world, pos.offset(0, treeHeight - 2, -1), leaves);
        setBlock(world, pos.offset(0, treeHeight - 1, -1), leaves);
        setBlock(world, pos.offset(0, treeHeight, -1), leaves);
        setBlock(world, pos.offset(0, treeHeight + 1, -1), leaves);
        setBlock(world, pos.offset(0, treeHeight + 2, -1), leaves);

        setBlock(world, pos.offset(0, treeHeight, 0), leaves);
        setBlock(world, pos.offset(0, treeHeight + 1, 0), leaves);
        setBlock(world, pos.offset(0, treeHeight + 2, 0), leaves);
        setBlock(world, pos.offset(0, treeHeight + 3, 0), leaves);
        
        return true;
    }


}