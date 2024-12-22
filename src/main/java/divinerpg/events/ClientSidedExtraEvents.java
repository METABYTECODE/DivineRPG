package divinerpg.events;

import divinerpg.DivineRPG;
import divinerpg.client.particle.*;
import divinerpg.effect.dimension.IceikaSky;
import divinerpg.effect.dimension.TwilightSky;
import divinerpg.effect.dimension.VetheaSky;
import divinerpg.registries.ParticleRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = DivineRPG.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSidedExtraEvents {
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "iceika_sky"), new IceikaSky());
        event.register(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "twilight_sky"), new TwilightSky());
        event.register(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "vethea_sky"), new VetheaSky());
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.EDEN_PORTAL.get(), ParticlePortal.EdenProvider::new);
        event.registerSpriteSet(ParticleRegistry.WILDWOOD_PORTAL.get(), ParticlePortal.WildwoodProvider::new);
        event.registerSpriteSet(ParticleRegistry.APALACHIA_PORTAL.get(), ParticlePortal.ApalachiaProvider::new);
        event.registerSpriteSet(ParticleRegistry.SKYTHERN_PORTAL.get(),ParticlePortal.SkythernProvider::new);
        event.registerSpriteSet(ParticleRegistry.MORTUM_PORTAL.get(), ParticlePortal.MortumProvider::new);
        event.registerSpriteSet(ParticleRegistry.HALITE_PORTAL.get(), ParticlePortal.HaliteProvider::new);
        event.registerSpriteSet(ParticleRegistry.TWILIGHT_PORTAL.get(), ParticlePortal.TwilightProvider::new);
        event.registerSpriteSet(ParticleRegistry.BLACK_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.BLUE_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.GREEN_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.PURPLE_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(ParticleRegistry.FROST.get(), ParticleFrost.Provider::new);
        event.registerSpriteSet(ParticleRegistry.SPARKLER.get(), ParticleSparkler.Provider::new);
        event.registerSpriteSet(ParticleRegistry.ENDER_TRIPLET.get(), ParticleEnderTriplet.Provider::new);
        event.registerSpriteSet(ParticleRegistry.TAR.get(), ParticleTar.Provider::new);
        event.registerSpriteSet(ParticleRegistry.SPLASH.get(), ParticleSplash.Provider::new);
        event.registerSpriteSet(ParticleRegistry.COLORED.get(), ParticleColored.Provider::new);
    }
}
