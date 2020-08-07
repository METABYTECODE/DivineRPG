package divinerpg.objects.entities.entity;

import divinerpg.DivineRPG;
import divinerpg.utils.LocalizeUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class EntityDivineVillager extends EntityCreature implements INpc, IMerchant {
    private EntityPlayer customer;
    private MerchantRecipeList buyingList;

    public EntityDivineVillager(World world) {
        super(world);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player) {
        this.customer = player;
    }

    @Nullable
    public EntityPlayer getCustomer() {
        return this.customer;
    }

    @Nullable
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        if(buyingList == null) {
            this.buyingList = getRecipeList();
        }
        return buyingList;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if (flag) {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        } else if (this.isEntityAlive() && /*!this.isTrading() &&*/ !player.isSneaking()) {
            if (this.buyingList == null) {
                this.buyingList = getRecipeList();
            }
            if (!this.world.isRemote && !this.buyingList.isEmpty()) {
                extraInteract(player);
                this.setCustomer(player);
                player.openGui(DivineRPG.instance, getGuiId(), this.world, getEntityId(), 0, 0);
            } else if (this.buyingList.isEmpty()) {
                return super.processInteract(player, hand);
            }
            return true;
        } else {
            return super.processInteract(player, hand);
        }
    }

    protected final void extraInteract(EntityPlayer player) {
        String[] chatMessages = this.getChatMessages();

        ITextComponent message = new TextComponentString("");
        ITextComponent npcName = LocalizeUtils.getClientSideTranslation(player, String.format("entity.%s.name", EntityList.getEntityString(this)));
        npcName.getStyle().setColor(TextFormatting.AQUA);
        message.appendSibling(npcName);
        message.appendText(": ");
        message.appendSibling(LocalizeUtils.getClientSideTranslation(player, chatMessages[rand.nextInt(chatMessages.length)]));
        player.sendMessage(message);
    }

    @SideOnly(Side.CLIENT)
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {
    }

    public void useRecipe(MerchantRecipe recipe) {
    }

    public void verifySellingItem(ItemStack var1) {
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public BlockPos getPos() {
        return this.getPosition();
    }

    protected abstract int getGuiId();
    protected abstract String[] getChatMessages();
    public abstract MerchantRecipeList getRecipeList();
}