package divinerpg.entities.vethea;

import com.google.common.collect.ImmutableList;
import divinerpg.DivineRPG;
import divinerpg.entities.base.EntityGifterNPC;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class EntityMysteriousManLayer1 extends EntityGifterNPC {
    public EntityMysteriousManLayer1(EntityType<? extends PathfinderMob> type, Level worldIn) {
    	super(type, worldIn);
    }
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.99F;
    }
    @Override
    protected ItemStack getGift() {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DivineRPG.MODID, "teaker_lump")), 3);
    }
    @Override
    protected ArrayList<String> getMessages() {
        ArrayList<String> messages = new ArrayList<>();
        messages.addAll(ImmutableList.of("message.mysterious_man_layer_1.1", "message.mysterious_man_layer_1.2", "message.mysterious_man_layer_1.3"));
        return messages;
    }
    @Override
    protected String getTranslationName() {
        return "entity.divinerpg.mysterious_man_layer_1";
    }
}