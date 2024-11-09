package divinerpg.registries;

import com.mojang.serialization.Codec;

import divinerpg.DivineRPG;
import divinerpg.attachments.*;
import divinerpg.attachments.base.*;
import divinerpg.config.CommonConfig;
import divinerpg.entities.base.FactionEntity.Faction;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.*;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;

import java.util.function.Supplier;

public class AttachmentRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DivineRPG.MODID);
	private AttachmentRegistry() {}

	//Standard Attachment
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SOUL_TRAP_COUNT = ATTACHMENT_TYPES.register("soul_trap_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

	//Single sided
	/*
	The SingleSidedAttachments can be replaced with a single field in the entity's class with overriding readAdditionalSaveData(CompondTag) and addAdditionalSaveData(CompondTag).
	The benefit of the SingleSidedAttachments is that they provide a very simple way of controlling and overseeing sided data access.
	 */
	public static final SingleSidedAttachment<DimensionalInventory> DIMENSIONAL_INVENTORY = registerSingleSided("dimensional_inventory", DimensionalInventory::new, false);
	public static final SingleSidedAttachment<String> ANGRY_AT = registerSingleSided("angry_at", () -> "", Codec.STRING, false);
	public static final SingleSidedAttachment<Integer> ANGER_TIME = registerSingleSided("anger_time", () -> 0, Codec.INT, false);
	public static final SingleSidedAttachment<Float> RADIUS = registerSingleSided("radius", () -> 0F, Codec.FLOAT, false);

	//Server handled
	public static final ServerHandledAttachment<Float> ARCANA = registerServerHandled("arcana", () -> 0F, Codec.FLOAT, ByteBufCodecs.FLOAT);
	public static final ServerHandledAttachment<Float> MAX_ARCANA = registerServerHandled("max_arcana", () -> CommonConfig.ARCANA_MAX, Codec.FLOAT, ByteBufCodecs.FLOAT);

	public static final ServerHandledAttachment<Boolean> ANGRY = registerServerHandled("angry", () -> false, Codec.BOOL, ByteBufCodecs.BOOL);
	public static final Reputation GROGLIN_REPUTATION = new Reputation("groglin_reputation", () -> Faction.GROGLIN);
	public static final Reputation GRUZZORLUG_REPUTATION = new Reputation("gruzzorlug_reputation", () -> Faction.GRUZZORLUG);
	public static final Reputation ICEIKA_MERCHANT_REPUTATION = new Reputation("iceika_merchant_reputation", () -> Faction.ICEIKA_MERCHANT);
	public static final ServerHandledAttachment<Byte> VARIANT = registerServerHandled("variant", () -> (byte)0, Codec.BYTE, ByteBufCodecs.BYTE);
	public static final ServerHandledAttachment<Boolean> HANGING = registerServerHandled("hanging", () -> false, Codec.BOOL, ByteBufCodecs.BOOL);
	public static final ServerHandledAttachment<Boolean> SPECIAL = registerServerHandled("special", () -> false, Codec.BOOL, ByteBufCodecs.BOOL);

	/*
	Allowing clients to modify data is very hackable.
	Use only if you are not afraid of hack clients that can exploit this resource.
	Good examples where to allow data modification on clients are non-game-mechanic-altering data values, such as visual elements like color and text.
	 */
	//Client handled

	//Common handled
	public static final CommonHandledAttachment<Integer> COLOR = registerCommonHandled("color", () -> 0xFF0000, Codec.INT, ByteBufCodecs.INT);

	public static void registerPayloads(final PayloadRegistrar r) {
		ARCANA.registerPayload(r);
		MAX_ARCANA.registerPayload(r);
		ANGRY.registerPayload(r);
		GROGLIN_REPUTATION.registerPayload(r);
		GRUZZORLUG_REPUTATION.registerPayload(r);
		ICEIKA_MERCHANT_REPUTATION.registerPayload(r);
		COLOR.registerPayload(r);
		VARIANT.registerPayload(r);
		HANGING.registerPayload(r);
		SPECIAL.registerPayload(r);
	}

	public static <T> SingleSidedAttachment<T> registerSingleSided(String name, Supplier<T> defaultValue, Codec<T> codec, boolean clientSide) {
		return new SingleSidedAttachment<>(name, defaultValue, codec, clientSide);
	}
	public static <B extends Tag, T extends INBTSerializable<B>> SingleSidedAttachment<T> registerSingleSided(String name, Supplier<T> defaultValue, boolean clientSide) {
		return new SingleSidedAttachment.Serializable<>(name, defaultValue, clientSide);
	}
	public static <T> ServerHandledAttachment<T> registerServerHandled(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
		return new ServerHandledAttachment<>(name, defaultValue, codec, streamCodec);
	}
	public static <B extends Tag, T extends INBTSerializable<B>> ServerHandledAttachment<T> registerServerHandled(String name, Supplier<T> defaultValue, StreamCodec<ByteBuf, T> streamCodec) {
		return new ServerHandledAttachment.Serializable<>(name, defaultValue, streamCodec);
	}
	public static <T> ClientHandledAttachment<T> registerClientHandled(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
		return new ClientHandledAttachment<>(name, defaultValue, codec, streamCodec);
	}
	public static <B extends Tag, T extends INBTSerializable<B>> ClientHandledAttachment<T> registerClientHandled(String name, Supplier<T> defaultValue, StreamCodec<ByteBuf, T> streamCodec) {
		return new ClientHandledAttachment.Serializable<>(name, defaultValue, streamCodec);
	}
	public static <T> CommonHandledAttachment<T> registerCommonHandled(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
		return new CommonHandledAttachment<>(name, defaultValue, codec, streamCodec);
	}
	public static <B extends Tag, T extends INBTSerializable<B>> CommonHandledAttachment<T> registerCommonHandled(String name, Supplier<T> defaultValue, StreamCodec<ByteBuf, T> streamCodec) {
		return new CommonHandledAttachment.Serializable<>(name, defaultValue, streamCodec);
	}
}