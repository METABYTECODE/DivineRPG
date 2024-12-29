package divinerpg.entities.base;

import divinerpg.registries.AttachmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityDivineTameable extends TamableAnimal implements NeutralMob {
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	protected final float healthIncrease;
    protected EntityDivineTameable(EntityType<? extends TamableAnimal> type, Level worldIn, float healthIncrease) {
        super(type, worldIn);
        this.healthIncrease = healthIncrease;
        setTame(false, false);
    }
    public boolean isMeat(ItemStack stack) {return stack.is(ItemTags.MEAT);}
    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new LeapAtTargetGoal(this, .4F));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        goalSelector.addGoal(6, new FollowOwnerGoal(this, 1, 10, 2));
//      goalSelector.addGoal(7, new BreedGoal(this, 1));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
    }
    @Override public float getWalkTargetValue(BlockPos pos, LevelReader reader) {return 0;}
    @Override public boolean hurt(DamageSource source, float amount) {
        if(isInvulnerableTo(source)) return false;
        else {
            Entity entity = source.getEntity();
            if(!level().isClientSide()) setOrderedToSit(false);
            if(entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) amount = (amount + 1) / 2;
            return super.hurt(source, amount);
        }
    }
    @Override public void setTame(boolean tamed, boolean applyTamingSideEffects) {
        super.setTame(tamed, applyTamingSideEffects);
        if(isTame()) {
            AttributeInstance attribute = getAttribute(Attributes.MAX_HEALTH);
            attribute.setBaseValue(attribute.getValue() * healthIncrease);
        }
    }
    @Override public void aiStep() {
        super.aiStep();
        if(!level().isClientSide()) updatePersistentAnger((ServerLevel)level(), true);
    }
    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if(level().isClientSide()) {
            AttachmentRegistry.COLOR.requestAttachment(this, null);
            AttachmentRegistry.ANGRY.requestAttachment(this, null);
        }
    }
    public DyeColor getCollarColor() {return DyeColor.byId(AttachmentRegistry.COLOR.get(this));}
    public void setCollarColor(DyeColor color) {AttachmentRegistry.COLOR.set(this, color.getId());}
    @Override public boolean isFood(ItemStack item) {return isMeat(item);}
    protected boolean isTamingFood(ItemStack item) {return isMeat(item);}
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if(level().isClientSide()) return isOwnedBy(player) ? InteractionResult.CONSUME : InteractionResult.PASS;
        if(isTame()) {
            if(isFood(itemstack) && getHealth() < getMaxHealth()) {
                itemstack.consume(1, player);
                heal((float) item.getFoodProperties(itemstack, this).nutrition());
                gameEvent(GameEvent.EAT, this);
                return InteractionResult.SUCCESS;
            }
            if(item instanceof DyeItem dyeitem) {
                if(isOwnedBy(player)) {
                    DyeColor dyecolor = dyeitem.getDyeColor();
                    if(dyecolor != getCollarColor()) {
                        setCollarColor(dyecolor);
                        itemstack.consume(1, player);
                        return InteractionResult.SUCCESS;
                    } return super.mobInteract(player, hand);
                }
            }
            InteractionResult actionresulttype = super.mobInteract(player, hand);
            if((!actionresulttype.consumesAction() || isBaby()) && isOwnedBy(player)) {
                setOrderedToSit(!isOrderedToSit());
                jumping = false;
                navigation.stop();
                setTarget(null);
                return InteractionResult.SUCCESS;
            }
        } else if(isTamingFood(itemstack) && !isAngry() && !player.isCreative()) {
            itemstack.shrink(1);
            if(random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                tame(player);
                navigation.stop();
                setTarget(null);
                setOrderedToSit(true);
                level().broadcastEntityEvent(this, (byte)7);
            } else level().broadcastEntityEvent(this, (byte)6);
            return InteractionResult.SUCCESS;
        } else if(player.isCreative()) tame(player);
        return super.mobInteract(player, hand);
    }
    @Override public boolean wantsToAttack(LivingEntity entity, LivingEntity entity1) {
        if(!(entity instanceof Creeper) && !(entity instanceof Ghast)) {
            return switch (entity) {
                case EntityDivineTameable pet -> !pet.isTame() || pet.getOwner() != entity1;
                case Player player when entity1 instanceof Player && !((Player) entity1).canHarmPlayer(player) -> false;
                case AbstractHorse abstractHorse when abstractHorse.isTamed() -> false;
                default -> !(entity instanceof TamableAnimal) || !((TamableAnimal) entity).isTame();
            };
        } else return false;
    }
    @Override public boolean canBeLeashed() {return !isAngry() && super.canBeLeashed();}
    @Override public int getRemainingPersistentAngerTime() {return AttachmentRegistry.ANGER_TIME.get(this);}
    @Override public void setRemainingPersistentAngerTime(int i) {AttachmentRegistry.ANGER_TIME.set(this, i);}
    @Override public void startPersistentAngerTimer() {setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(random));}
    @Override @Nullable public UUID getPersistentAngerTarget() {
        if(AttachmentRegistry.ANGRY.get(this)) return AttachmentRegistry.ANGRY_AT.get(this);
        return null;
    }
    @Override
    public boolean isAngry() {
        return AttachmentRegistry.ANGRY.get(this);
    }
    @Override public void setPersistentAngerTarget(@Nullable UUID id) {
        AttachmentRegistry.ANGRY_AT.set(this, id == null ? AttachmentRegistry.zero : id);
        AttachmentRegistry.ANGRY.set(this, id != null);
    }
	@Override public AgeableMob getBreedOffspring(ServerLevel s, AgeableMob a) {return null;}
    @Override public boolean canMate(Animal animal) {return false;}
    @Override public boolean removeWhenFarAway(double distanceToClosestPlayer) {return !isTame();}
}