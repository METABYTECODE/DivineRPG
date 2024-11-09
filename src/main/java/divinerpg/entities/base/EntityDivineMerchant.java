package divinerpg.entities.base;

import divinerpg.DivineRPG;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.*;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class EntityDivineMerchant extends Villager {
    VillagerProfession profession;

    public EntityDivineMerchant(EntityType<? extends EntityDivineMerchant> type, Level level, VillagerProfession profession) {
        super(type, level);
        ((GroundPathNavigation) getNavigation()).setCanOpenDoors(true);
        this.profession = profession;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isSleeping() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                boolean hasOffers = !getOffers().isEmpty(); // Check if there are offers

                if (hand == InteractionHand.MAIN_HAND) {
                    player.awardStat(Stats.TALKED_TO_VILLAGER); // Award stat for talking to the villager
                    if (hasOffers && canTrade(player)) { // Check if trading can start
                        setTradingPlayer(player); // Set the trading player
                        openTradingScreen(player, Component.translatable("entity.divinerpg." + profession.name()), getVillagerData().getLevel()); // Open the trading screen
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide); // Return success based on the client side
        } else {
            return super.mobInteract(player, hand); // Call the superclass method for other interactions
        }
    }

    @Override
    public boolean canRestock() { return true; }
    @Override
    public boolean showProgressBar() { return false; }
    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
    @Override
    protected void stopTrading() {
        super.stopTrading();
        resetSpecialPrices();
    }

    private void resetSpecialPrices() {
        for (MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.resetSpecialPriceDiff();
        }
    }

    private boolean canTrade(Player player) {
        return player.isAlive() && !player.isSleeping();
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        if (this.getTradingPlayer() != player) {
            if (player != null && canTrade(player)) {
                super.setTradingPlayer(player);
            }
        }
    }

    public abstract String[] getChatMessages();

    public static class DivineTrades implements VillagerTrades.ItemListing {
        protected ItemStack input1, input2;
        private ItemStack output;
        protected int xp, stock;

        public DivineTrades(ItemStack input1, ItemStack input2, ItemStack output, int stock, int xp) {
            this.xp = xp;
            this.stock = stock + 1;
            this.output = output;
            this.input1 = input1;
            this.input2 = input2;
        }

        public DivineTrades(ItemStack input1, ItemStack output, int stock, int xp) {
            this(input1, null, output, stock, xp); // Pass null for input2 if not provided
        }

        @Override
        public MerchantOffer getOffer(Entity tradeEnt, RandomSource rand) {
            if (input2 != null) {
                return new MerchantOffer(new ItemCost(input1.getItem(), input1.getCount()), Optional.of(new ItemCost(input2.getItem(), input2.getCount())), output, stock, xp, 0F);
            } else {
                return new MerchantOffer(new ItemCost(input1.getItem(), input1.getCount()), output, stock, xp, 0F);
            }
        }
    }

    public static class DivineMapTrades extends DivineTrades {
        private final String displayName;
        private final TagKey<Structure> destination;
        private final Holder<MapDecorationType> destinationType;

        public DivineMapTrades(ItemStack input1, ItemStack input2, String displayName, TagKey<Structure> destination, Holder<MapDecorationType> destinationType, int xp) {
            super(input1, input2, null, 1, xp);
            this.displayName = displayName;
            this.destination = destination;
            this.destinationType = destinationType;
        }

        public DivineMapTrades(ItemStack input1, String displayName, TagKey<Structure> destination, Holder<MapDecorationType> destinationType, int xp) {
            this(input1, null, displayName, destination, destinationType, xp);
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource rand) {
            ItemStack itemstack = MapItem.create(trader.level(), trader.blockPosition().getX(), trader.blockPosition().getZ(), (byte)2, true, true);

            if (input2 != null) {
                return new MerchantOffer(new ItemCost(input1.getItem(), input1.getCount()), Optional.of(new ItemCost(input2.getItem(), input2.getCount())), itemstack, stock, xp, 0f);
            } else {
                return new MerchantOffer(new ItemCost(input1.getItem(), input1.getCount()), itemstack, stock, xp, 0f);
            }
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType type) {
        return !(level.getBiome(blockPosition()).is(Tags.Biomes.IS_MUSHROOM) || level.getBiome(blockPosition()).is(Biomes.DEEP_DARK));
    }
}
