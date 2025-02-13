package divinerpg.blocks.base;

import divinerpg.DivineRPG;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Supplier;

public class SimplePortalBlock extends PortalBlock {
	public final Block frameBlock;
	public SimplePortalBlock(ResourceKey<Level> rootDimension, String frameTag, Block frameBlock, ResourceLocation particle) {
		super(Properties.ofFullCopy(Blocks.NETHER_PORTAL), rootDimension, TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, frameTag)), particle);
		this.frameBlock = frameBlock;
	}
	@Override
	public BlockPos placePortal(ServerLevel level, BlockPos pos, Axis axis) {
		return placeVanillaLookingPortal(level, pos, frameBlock.defaultBlockState(), defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis), axis);
	}
}