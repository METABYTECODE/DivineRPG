package divinerpg.client.renders.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class RenderDivineItemProjectile<T extends Projectile> extends EntityRenderer<T> {
    private final Supplier<Item> item;
    private final ItemRenderer itemRenderer;
    public RenderDivineItemProjectile(EntityRendererProvider.Context context, Supplier<Item> item) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.item = item;
    }
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.tickCount > 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            this.itemRenderer.renderStatic(item.get().getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }
    public ResourceLocation getTextureLocation(Projectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}