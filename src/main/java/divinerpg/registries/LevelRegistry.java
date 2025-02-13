package divinerpg.registries;

import divinerpg.DivineRPG;
import divinerpg.effect.dimension.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class LevelRegistry {
    public static final ResourceKey<Level>
        EDEN = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "eden")),
        WILDWOOD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "wildwood")),
        APALACHIA = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "apalachia")),
        SKYTHERN = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "skythern")),
        MORTUM = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "mortum")),
        ICEIKA = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "iceika")),
        ARCANA = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "arcana")),
        VETHEA = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "vethea"));

}