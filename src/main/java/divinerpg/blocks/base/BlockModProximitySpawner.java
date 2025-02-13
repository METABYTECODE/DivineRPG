package divinerpg.blocks.base;

import com.mojang.serialization.MapCodec;
import divinerpg.block_entities.block.ProximitySpawnerBlockEntity;
import divinerpg.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class BlockModProximitySpawner extends BaseEntityBlock {
	public static final MapCodec<BlockModProximitySpawner> CODEC = simpleCodec(BlockModProximitySpawner::new);
	@Override public MapCodec<BlockModProximitySpawner> codec() {return CODEC;}
	public BlockModProximitySpawner(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.ENABLED, false));
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.ENABLED);
	}
	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState s, LevelAccessor level, BlockPos pos, BlockPos p) {
		if(level.getBlockEntity(pos) instanceof ProximitySpawnerBlockEntity spawner) {
			boolean enabled = spawner.entityName != null;
			if(state.getValue(BlockStateProperties.ENABLED) != enabled) state.setValue(BlockStateProperties.ENABLED, enabled);
		}
		return state;
	}
	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if(player.isCreative() && level.getBlockEntity(pos) instanceof ProximitySpawnerBlockEntity entity) {
			if(stack == null || stack.isEmpty()) entity.entityName = null;
			else if(stack.getItem() instanceof SpawnEggItem spawnegg) entity.entityName = spawnegg.getType(null).toShortString();
			else return ItemInteractionResult.FAIL;
			boolean enabled = entity.entityName != null;
			if(state.getValue(BlockStateProperties.ENABLED) != enabled) level.setBlock(pos, state.setValue(BlockStateProperties.ENABLED, enabled), UPDATE_ALL);
			return ItemInteractionResult.SUCCESS;
		} return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return BlockEntityRegistry.PROXIMITY_SPAWNER.get().create(pos, state);}
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, BlockEntityRegistry.PROXIMITY_SPAWNER.get(), ProximitySpawnerBlockEntity::serverTick);
	}
}