package divinerpg.entities.iceika.groglin;

import divinerpg.entities.base.EntityDivineMerchant;
import divinerpg.registries.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;

public class GroglinSharlatan extends Groglin implements RangedAttackMob {
	public GroglinSharlatan(EntityType<? extends Groglin> type, Level worldIn) {
		super(type, worldIn);
		if(!worldIn.isClientSide()) setData(AttachmentRegistry.IMPORTANT.attachment, true);
	}
	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.serenade_of_ice.get()));
	}
	@Override protected void registerGoals() {
		super.registerGoals();
        goalSelector.addGoal(0, new RangedAttackGoal(this, getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue(), 10, (float)getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
	}
	@Override public void performRangedAttack(LivingEntity e, float f) {
		LivingEntity target = getTarget();
		if(isAlive() && target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 1, false, true, true));
            target.setTicksFrozen(target.getTicksFrozen() + 10);
            if(getRandom().nextBoolean()) target.hurt(damageSources().indirectMagic(this, this), 1);
        }
	}
	@Override protected void updateTrades() {
		MerchantOffers merchantoffers = getOffers();
		DivineTrades[] tradetrades = new DivineTrades[]{
                new EntityDivineMerchant.DivineTrades(new ItemStack(ItemRegistry.olivine.get(), 6), PotionContents.createItemStack(Items.POTION, PotionRegistry.GROG), random.nextInt(7), 2),
                new EntityDivineMerchant.DivineTrades(new ItemStack(ItemRegistry.olivine.get(), 4), PotionContents.createItemStack(Items.POTION, PotionRegistry.TEA), random.nextInt(7), 2),
                new EntityDivineMerchant.DivineTrades(new ItemStack(ItemRegistry.olivine.get(), 15), PotionContents.createItemStack(Items.POTION, PotionRegistry.LONG_WARMTH), random.nextInt(7), 2),
                new EntityDivineMerchant.DivineMapTrades(new ItemStack(ItemRegistry.olivine.get(), 25), new ItemStack(ItemRegistry.raw_wolpertinger_meat.get(), 5), "filled_map.whale_skull", WHALE_SKULL, MapDecorationTypes.OCEAN_MONUMENT, 25),
                new EntityDivineMerchant.DivineTrades(new ItemStack(ItemRegistry.olivine.get(), 16), new ItemStack(ItemRegistry.raw_wolpertinger_meat.get(), 4), new ItemStack(BlockRegistry.frostedAllure.get()), random.nextInt(7), 8)
        };
        addOffersFromItemListings(merchantoffers, tradetrades, 3);
	}
}