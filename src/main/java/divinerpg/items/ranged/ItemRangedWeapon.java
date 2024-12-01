package divinerpg.items.ranged;

import divinerpg.DivineRPG;
import divinerpg.attachments.Arcana;
import divinerpg.entities.projectile.DivineThrownItem;
import divinerpg.util.LocalizeUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;


public class ItemRangedWeapon extends ProjectileWeaponItem {
    public static final ResourceLocation GENERIC = ResourceLocation.withDefaultNamespace("textures/particle/generic_0.png");

    public final TagKey<Item> ammoType;
    public SoundEvent sound;
    public float power = 3F;
    public int arcanaConsumedUse, cooldown;
    public final Supplier<EntityType<? extends Projectile>> projectileType;
    public Integer nameColor = null;
    protected boolean infinite, breakable = true;
    protected final List<Component> tooltips = new ArrayList<>();
    public final Supplier<ItemStack> defaultItem;

    public ItemRangedWeapon(Supplier<EntityType<? extends Projectile>> projectileType, int uses) {
        this(null, Items.ARROW::getDefaultInstance, projectileType, uses);
    }
    public ItemRangedWeapon(Properties properties, Supplier<EntityType<? extends Projectile>> projectileType) {
        super(properties);
        this.ammoType = null;
        infinite = true;
        this.projectileType = projectileType;
        defaultItem = Items.ARROW::getDefaultInstance;
    }
    public ItemRangedWeapon(Supplier<EntityType<? extends Projectile>> projectileType) {
        this((String) null, Items.ARROW::getDefaultInstance, projectileType);
    }
    public ItemRangedWeapon(@Nullable String ammoType, Supplier<ItemStack> defaultItem, Supplier<EntityType<? extends Projectile>> projectileType) {
        super(new Item.Properties().stacksTo(1));
        this.ammoType = ammoType == null ? null : TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, ammoType));
        infinite = ammoType == null;
        breakable = false;
        this.projectileType = projectileType;
        this.defaultItem = defaultItem;
    }
    public ItemRangedWeapon(@Nullable String ammoType, Supplier<ItemStack> defaultItem, Supplier<EntityType<? extends Projectile>> projectileType, int uses) {
        super(new Item.Properties().stacksTo(1).durability(uses));
        this.ammoType = ammoType == null ? null : TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, ammoType));
        infinite = ammoType == null;
        this.projectileType = projectileType;
        this.defaultItem = defaultItem;
    }
    public ItemRangedWeapon withTooltip(Component tip) {
        tooltips.add(tip);
        return this;
    }
    public ItemRangedWeapon withTooltips(List<Component> tooltip) {
        tooltips.addAll(tooltip);
        return this;
    }
    public ItemRangedWeapon withSound(SoundEvent s) {
        sound = s;
        return this;
    }
    public ItemRangedWeapon withPower(float pow) {
        power = pow;
        return this;
    }
    public ItemRangedWeapon withCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }
    public ItemRangedWeapon arcanaUse(int arcana) {
        arcanaConsumedUse = arcana;
        return this;
    }
    public ItemRangedWeapon nameColor(int c) {
        nameColor = c;
        return this;
    }
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        if(!breakable) stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
        return stack;
    }
    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        Projectile p = projectileType.get().create(level);
        if(p instanceof DivineThrownItem t) t.setItem(ammo);
        p.setOwner(shooter);
        p.setPos(shooter.getEyePosition().add(0D, -0.15, 0D));
        return p;
    }
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return defaultItem.get();
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack ammo = findAmmo(player);
        if(ammo != null && !ammo.isEmpty() && Arcana.getAmount(player) >= arcanaConsumedUse) {
            if(!level.isClientSide) {
                shoot((ServerLevel) level, player, player.getUsedItemHand(), stack, List.of(ammo),  power, 1F, false, null);
                if(arcanaConsumedUse > 0) Arcana.modifyAmount(player, -arcanaConsumedUse);
            } if(!player.isCreative()) ammo.shrink(1);
            if(cooldown > 0) player.getCooldowns().addCooldown(this, cooldown);
            player.awardStat(Stats.ITEM_USED.get(this));
            if(this instanceof ItemThrowable) {
                player.playSound(sound != null ? sound : SoundEvents.ARROW_SHOOT, .5F, .4F / (player.getRandom().nextFloat() * .4F + .8F));
                return InteractionResultHolder.success(stack);
            } player.playSound(sound != null ? sound : SoundEvents.ARROW_SHOOT, 1, 1);
            return InteractionResultHolder.consume(stack);
        } return InteractionResultHolder.pass(stack);
    }
    protected ItemStack findAmmo(Player player) {
        ItemStack shootable = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(ammoType == null) return getDefaultCreativeAmmo(player, shootable);
        ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(player, (item) -> item.is(ammoType));
        if(!itemstack.isEmpty()) return CommonHooks.getProjectile(player, shootable, itemstack);
        else {
            for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack1 = player.getInventory().getItem(i);
                if(itemstack1.is(ammoType)) return CommonHooks.getProjectile(player, shootable, itemstack1);
            } return CommonHooks.getProjectile(player, shootable, player.getAbilities().instabuild ? getDefaultCreativeAmmo(player, shootable) : ItemStack.EMPTY);
        }
    }
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return this::isOfTag;
    }
    private boolean isOfTag(ItemStack stack) {
        return ammoType != null && stack.is(ammoType);
    }
    @Override public int getDefaultProjectileRange() {return 15;}
    @Override protected void shootProjectile(LivingEntity shooter, Projectile projectile, int i, float velocity, float inaccuracy, float angle, @Nullable LivingEntity livingEntity1) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
    }
    @Override
    public Component getName(ItemStack pStack) {
        return nameColor != null ? ((MutableComponent) super.getName(pStack)).withColor(nameColor) : super.getName(pStack);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        /*
            if(bulletType.getBulletSpecial() == BulletType.BulletSpecial.RETURN) tooltip.add(LocalizeUtils.returnsToSender());
         */
        tooltip.addAll(tooltips);
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        if(arcanaConsumedUse > 0) tooltip.add(LocalizeUtils.arcanaConsumed(arcanaConsumedUse));
        if(infinite) tooltip.add(LocalizeUtils.infiniteAmmo());
        else if(ammoType != null) tooltip.add(LocalizeUtils.ammo(ammoType));
    }
}