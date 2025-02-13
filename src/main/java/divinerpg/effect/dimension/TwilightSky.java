package divinerpg.effect.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import divinerpg.DivineRPG;
import divinerpg.registries.LevelRegistry;
import divinerpg.registries.MobEffectRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class TwilightSky extends DimensionSpecialEffects {
	public static final ResourceLocation SUN_LOCATION = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "textures/particle/twilight_sun.png"),
			POG_SUN_LOCATION = ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "textures/particle/pog_sun.png");
	@Nullable private VertexBuffer skyBuffer, starBuffer;
	public TwilightSky() {
		super(256F, true, SkyType.NORMAL, false, false);
		
		//create sky
		Tesselator tesselator = Tesselator.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		if(starBuffer != null) starBuffer.close();
		starBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
		MeshData bufferbuilder$renderedbuffer = drawStars(tesselator);
		starBuffer.bind();
		starBuffer.upload(bufferbuilder$renderedbuffer);
		VertexBuffer.unbind();
		tesselator = Tesselator.getInstance();
	    if(skyBuffer != null) skyBuffer.close();
	    skyBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
	    bufferbuilder$renderedbuffer = buildSkyDisc(tesselator, 16F);
	    skyBuffer.bind();
	    skyBuffer.upload(bufferbuilder$renderedbuffer);
	    VertexBuffer.unbind();
	}
	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
		return color.multiply(sunHeight  * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight  * 0.91 + 0.09);
	}
	public boolean isFoggyAt(int i, int ii) {return false;}
	@Override
	public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
		setupFog.run();
		if(!isFoggy) {
			FogType fogtype = camera.getFluidInCamera();
			if(fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !doesMobEffectBlockSky(camera)) {
//				RenderSystem.disableBlend();
				Vec3 vec3 = level.getSkyColor(camera.getPosition(), partialTick);
				float f = (float)vec3.x, f1 = (float)vec3.y, f2 = (float)vec3.z;
				FogRenderer.levelFogColor();
				BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
				RenderSystem.depthMask(false);
				RenderSystem.setShaderColor(f, f1, f2, 1F);
				ShaderInstance shaderinstance = RenderSystem.getShader();
				skyBuffer.bind();
				skyBuffer.drawWithShader(modelViewMatrix, projectionMatrix, shaderinstance);
				VertexBuffer.unbind();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				float[] afloat = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
				if(afloat != null) {
					RenderSystem.setShader(GameRenderer::getPositionColorShader);
//					RenderSystem.disableBlend();
					RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
					modelViewMatrix.rotate(Axis.XP.rotationDegrees(90F));
					float f3 = Mth.sin(level.getSunAngle(partialTick)) < 0F ? 180F : 0F;
					modelViewMatrix.rotate(Axis.ZP.rotationDegrees(f3));
					modelViewMatrix.rotate(Axis.ZP.rotationDegrees(90F));
					float f4 = afloat[0], f5 = afloat[1], f6 = afloat[2];
					bufferbuilder.addVertex(modelViewMatrix, 0F, 100F, 0F).setColor(f4, f5, f6, afloat[3]);
					for(int j = 0; j <= 16; ++j) {
						float f7 = (float)(j * (Math.PI * 2F) / 16F), f8 = Mth.sin(f7);
						float f9 = Mth.cos(f7);
						bufferbuilder.addVertex(modelViewMatrix, f8 * 120F, f9 * 120F, -f9 * 40F * afloat[3]).setColor(afloat[0], afloat[1], afloat[2], 0F);
					} BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
					modelViewMatrix.rotate(Axis.ZP.rotationDegrees(-90F));
					modelViewMatrix.rotate(Axis.ZP.rotationDegrees(-f3));
					modelViewMatrix.rotate(Axis.XP.rotationDegrees(-90F));
				}
//				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				float f11 = 1F - level.getRainLevel(partialTick);
				RenderSystem.setShaderColor(1F, 1F, 1F, f11);
				modelViewMatrix.rotate(Axis.YP.rotationDegrees(-90F));
				modelViewMatrix.rotate(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360F));
				float f12 = 30F;
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, level.dimension() == LevelRegistry.MORTUM && camera.getPosition().y < 0 ? POG_SUN_LOCATION : SUN_LOCATION);
				BufferBuilder bufferbuilder1 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				bufferbuilder1.addVertex(modelViewMatrix, -f12, 100, -f12).setUv(0, 0);
				bufferbuilder1.addVertex(modelViewMatrix, f12, 100, -f12).setUv(1, 0);
				bufferbuilder1.addVertex(modelViewMatrix, f12, 100, f12).setUv(1, 1);
				bufferbuilder1.addVertex(modelViewMatrix, -f12, 100, f12).setUv(0, 1);
				BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
				float f10 = level.getStarBrightness(partialTick) * f11 * 1.01F;
				if(f10 > 0F) {
					RenderSystem.setShaderColor(f10, f10, f10, f10);
					FogRenderer.setupNoFog();
					starBuffer.bind();
					starBuffer.drawWithShader(modelViewMatrix, projectionMatrix, GameRenderer.getPositionShader());
					VertexBuffer.unbind();
					setupFog.run();
				} modelViewMatrix.rotate(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * -360F));
				modelViewMatrix.rotate(Axis.YP.rotationDegrees(90F));
				if(level.dimension() == LevelRegistry.EDEN) RenderSystem.setShaderColor(1F, 1F, .9F, 1F);
				else if(level.dimension() == LevelRegistry.WILDWOOD) RenderSystem.setShaderColor(.8F, .9F, 1F, 1F);
				else if(level.dimension() == LevelRegistry.APALACHIA) RenderSystem.setShaderColor(.9F, .8F, 1F, 1F);
				else if(level.dimension() == LevelRegistry.SKYTHERN) RenderSystem.setShaderColor(.9F, .9F, 1F, 1F);
				else if(level.dimension() == LevelRegistry.MORTUM) RenderSystem.setShaderColor(.9F, .8F, .8F, 1F);
				else RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				RenderSystem.disableBlend();
				RenderSystem.defaultBlendFunc();
//				RenderSystem.disableBlend();
//				RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
//				if(level.effects().hasGround()) RenderSystem.setShaderColor(f * .2F + .04F, f1 * .2F + .04F, f2 * .6F + .1F, 1F);
//				else RenderSystem.setShaderColor(f, f1, f2, 1F);
//				RenderSystem.enableBlend();
				RenderSystem.depthMask(true);
			}
		}
		return true;
	}
	public static boolean doesMobEffectBlockSky(Camera camera) {
		Entity entity = camera.getEntity();
		if(!(entity instanceof LivingEntity livingentity)) return false;
		return livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS) || livingentity.hasEffect(MobEffectRegistry.HEAVY_AIR);
	}
	public static MeshData buildSkyDisc(Tesselator tesselator, float g) {
		float f = Math.signum(g) * 512;
		RenderSystem.setShader(GameRenderer::getPositionShader);
		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
		builder.addVertex(0F, g, 0F);
		for(int i = -180; i <= 180; i += 45) builder.addVertex((float)(f * Math.cos(i * (Math.PI / 180D))), g, (float)(512 * Math.sin(i * (Math.PI / 180D))));
		return builder.build();
	}
	public static MeshData drawStars(Tesselator tesselator) {
		RandomSource randomsource = RandomSource.create(10842L);
		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		for(int i = 0; i < 1500; ++i) {
			double d0 = randomsource.nextFloat() * 2F - 1F, d1 = randomsource.nextFloat() * 2F - 1F, d2 = randomsource.nextFloat() * 2F - 1F,
				d3 = .15F + randomsource.nextFloat() * .1F, d4 = d0 * d0 + d1 * d1 + d2 * d2;
			if(d4 < 1D && d4 > .01D) {
				d4 = 1D / Math.sqrt(d4);
	            d0 *= d4;
	            d1 *= d4;
	            d2 *= d4;
	            double d5 = d0 * 100D, d6 = d1 * 100D, d7 = d2 * 100D, d8 = Math.atan2(d0, d2), d9 = Math.sin(d8), d10 = Math.cos(d8), d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1),
	            	d12 = Math.sin(d11), d13 = Math.cos(d11), d14 = randomsource.nextDouble() * Math.PI * 2D, d15 = Math.sin(d14), d16 = Math.cos(d14);
	            for(int j = 0; j < 4; ++j) {
	               double d18 = ((j & 2) - 1) * d3, d19 = ((j + 1 & 2) - 1) * d3, d21 = d18 * d16 - d19 * d15, d22 = d19 * d16 + d18 * d15,
	               	d23 = d21 * d12 + 0D * d13, d24 = 0D * d12 - d21 * d13, d25 = d24 * d9 - d22 * d10, d26 = d22 * d9 + d24 * d10;
	               builder.addVertex((float)(d5 + d25), (float)(d6 + d23), (float)(d7 + d26));
	            }
	         }
	      }
	      return builder.build();
	}
}