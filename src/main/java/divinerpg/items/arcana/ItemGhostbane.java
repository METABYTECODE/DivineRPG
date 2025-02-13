package divinerpg.items.arcana;

import divinerpg.attachments.Arcana;
import divinerpg.enums.EntityStats;
import divinerpg.items.base.ItemMod;
import divinerpg.registries.EntityRegistry;
import divinerpg.util.LocalizeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.*;
import java.util.List;

public class ItemGhostbane extends ItemMod {
    public ItemGhostbane() {
        super(new Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
        arcanaConsumedUse = 200;
        cooldown = 60;
    }
    @Override
    protected InteractionResultHolder<ItemStack> arcanicUse(Level level, Player player, InteractionHand hand) {
    	if(level instanceof ServerLevel) EntityRegistry.WRAITH.get().spawn((ServerLevel)level, ItemStack.EMPTY, player, player.blockPosition(), MobSpawnType.MOB_SUMMONED, true, false).tame(player);
    	return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    @Override public InteractionResult useOn(UseOnContext context) {
    	if(context.getPlayer() instanceof ServerPlayer pl) {
    		float amount = Arcana.getAmount(pl);
    		if(arcanaConsumedUse != 0 && amount >= arcanaConsumedUse) {
    			Arcana.modifyAmount(pl, -arcanaConsumedUse);
    			pl.getCooldowns().addCooldown(this, cooldown);
    			BlockPos pos = context.getClickedPos();
    			if(!context.getLevel().getBlockState(pos).getCollisionShape(context.getLevel(), pos).isEmpty()) pos = pos.relative(context.getClickedFace());
    			EntityRegistry.WRAITH.get().spawn((ServerLevel)context.getLevel(), ItemStack.EMPTY, pl, pos, MobSpawnType.MOB_SUMMONED, true, false).tame(pl);
    			return InteractionResult.SUCCESS;
    		}
    	} return InteractionResult.PASS;
    }
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.summoned(EntityRegistry.WRAITH.get()));
        tooltip.add(LocalizeUtils.summonedDamage((int)EntityStats.WRAITH.getAttackDamage()));
        tooltip.add(LocalizeUtils.summonedHealth((int)EntityStats.WRAITH.getHealth()));
        tooltip.add(LocalizeUtils.summonedDespawn());
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}