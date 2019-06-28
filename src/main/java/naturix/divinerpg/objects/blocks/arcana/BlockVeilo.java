package naturix.divinerpg.objects.blocks.arcana;

import naturix.divinerpg.registry.ModItems;
import naturix.divinerpg.registry.ModSeeds;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockVeilo extends BlockArcanaCrop {

	public BlockVeilo(String name) {
		super(name, 3);
	} 
	@Override
	protected Item getSeed()
    {
        return ModSeeds.veiloSeeds;
    }
	@Override
    protected Item getCrop()
    {
        return ModItems.veilo;
    }
}