package divinerpg.blocks.twilight;

import divinerpg.blocks.base.*;
import divinerpg.registries.*;
import divinerpg.tiles.chests.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.inventory.container.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.*;

public class BlockEdenChest extends BlockModChest {

    public BlockEdenChest(String name) {
        super(name, AbstractBlock.Properties.of(Material.STONE).sound(SoundType.STONE).strength(15, 10000000), () -> TileRegistry.EDEN_CHEST);
    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
        //TODO - create custom eden menu
        return super.getMenuProvider(state, world, pos);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new TileEntityEdenChest();
    }
}