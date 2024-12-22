package divinerpg.registries;

import com.mojang.serialization.MapCodec;
import divinerpg.DivineRPG;
import divinerpg.client.particle.*;
import divinerpg.client.particle.options.ParticleColouredOption;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.*;

import static divinerpg.DivineRPG.MODID;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EDEN_PORTAL = PARTICLES.register("eden_portal", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WILDWOOD_PORTAL = PARTICLES.register("wildwood_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> APALACHIA_PORTAL = PARTICLES.register("apalachia_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SKYTHERN_PORTAL = PARTICLES.register("skythern_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MORTUM_PORTAL = PARTICLES.register("mortum_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HALITE_PORTAL = PARTICLES.register("halite_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TWILIGHT_PORTAL = PARTICLES.register("twilight_portal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLACK_FLAME = PARTICLES.register("black_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLUE_FLAME = PARTICLES.register("blue_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREEN_FLAME = PARTICLES.register("green_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PURPLE_FLAME = PARTICLES.register("purple_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FROST = PARTICLES.register("frost", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARKLER = PARTICLES.register("sparkler", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ENDER_TRIPLET = PARTICLES.register("ender_triplets", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TAR = PARTICLES.register("tar", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPLASH = PARTICLES.register("splash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, ParticleType<ParticleColouredOption>> COLORED = PARTICLES.register("colored", () -> new ParticleType<ParticleColouredOption>(false) {@Override public MapCodec<ParticleColouredOption> codec() {return null;} @Override public StreamCodec<? super RegistryFriendlyByteBuf, ParticleColouredOption> streamCodec() {return null;}});

}