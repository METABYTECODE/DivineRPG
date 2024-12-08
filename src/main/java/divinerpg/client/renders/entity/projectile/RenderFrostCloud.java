package divinerpg.client.renders.entity.projectile;

import divinerpg.entities.projectile.EntityFrostCloud;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class RenderFrostCloud extends EntityRenderer<EntityFrostCloud> {
    public RenderFrostCloud(EntityRendererProvider.Context manager) {super(manager);}
    @Override public ResourceLocation getTextureLocation(EntityFrostCloud cloud) {return null;}
}