package divinerpg.attachments.base;

import com.mojang.serialization.Codec;
import divinerpg.DivineRPG;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public abstract class SynchedAttachement<T> extends ModAttachment<T> {
    public final CustomPacketPayload.Type<AttachmentPayload> type;
    public final CustomPacketPayload.Type<RequestAttachmentPayload> requestType;
    public final StreamCodec<ByteBuf, AttachmentPayload> streamCodec;
    public final StreamCodec<ByteBuf, RequestAttachmentPayload> requestCodec = StreamCodec.composite(ByteBufCodecs.INT, RequestAttachmentPayload::entityID, RequestAttachmentPayload::new);
    public SynchedAttachement(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
        super(name, defaultValue, codec);
        type = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, name));
        requestType = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "request_" + name));
        this.streamCodec = StreamCodec.composite(streamCodec, AttachmentPayload::data, ByteBufCodecs.INT, AttachmentPayload::entityID, AttachmentPayload::new);
    }
    protected SynchedAttachement(String name, DeferredHolder<AttachmentType<?>, AttachmentType<T>> attachment, StreamCodec<ByteBuf, T> streamCodec) {
        super(attachment);
        type = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, name));
        requestType = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DivineRPG.MODID, "request_" + name));
        this.streamCodec = StreamCodec.composite(streamCodec, AttachmentPayload::data, ByteBufCodecs.INT, AttachmentPayload::entityID, AttachmentPayload::new);
    }
    public abstract void registerPayload(PayloadRegistrar r);
    public abstract void requestAttachment(Entity e, ServerPlayer player);
    public class RequestAttachmentPayload implements CustomPacketPayload {
        public final int entityID;
        protected RequestAttachmentPayload(int entityID) {this.entityID = entityID;}
        public int entityID() {return entityID;}
        @Override public Type<? extends CustomPacketPayload> type() {return requestType;}
    }
    public class AttachmentPayload implements CustomPacketPayload {
        public final T data;
        public final int entityID;
        protected AttachmentPayload(T data, int entityID) {this.data = data;this.entityID = entityID;}
        public T data() {return data;}
        public int entityID() {return entityID;}
        @Override public Type<? extends CustomPacketPayload> type() {return type;}
    }
}
