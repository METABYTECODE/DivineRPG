package divinerpg.items.vanilla;

import divinerpg.items.base.ItemMod;
import divinerpg.util.LocalizeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.api.distmarker.*;
import java.util.List;

public class ItemTeleportationCrystal extends ItemMod {
    public ItemTeleportationCrystal() {super(new Properties().durability(10));}
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!world.isClientSide && player instanceof ServerPlayer s) {
            BlockPos respawnPos = ((ServerPlayer)player).getRespawnPosition();
            if(respawnPos != null) {
                if(!player.isCreative()) stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                player.getCooldowns().addCooldown(this, 160);
                player.awardStat(Stats.ITEM_USED.get(this));
                player.changeDimension(s.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.PLAY_PORTAL_SOUND));
                return InteractionResultHolder.success(stack);
            } else {
                Component message = LocalizeUtils.clientMessage(ChatFormatting.RED, "teleport.no_respawn");
                player.displayClientMessage(message, true);
                return InteractionResultHolder.fail(stack);
            }
        } return super.use(world, player, hand);
    }
    @OnlyIn(Dist.CLIENT)
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(LocalizeUtils.i18n("teleport.respawn_point"));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}