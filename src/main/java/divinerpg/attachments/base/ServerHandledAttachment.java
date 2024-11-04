package divinerpg.attachments.base;

import com.mojang.serialization.Codec;
import divinerpg.registries.AttachmentRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.io.IOException;
import java.util.function.Supplier;

public class ServerHandledAttachment<T> extends SynchedAttachement<T> {
    public ServerHandledAttachment(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
        super(name, defaultValue, codec, streamCodec);
    }
    protected ServerHandledAttachment(String name, DeferredHolder<AttachmentType<?>, AttachmentType<T>> attachment, StreamCodec<ByteBuf, T> streamCodec) {
        super(name, attachment, streamCodec);
    }
    @Override
    public void registerPayload(PayloadRegistrar r) {
        r.playToClient(type, streamCodec, (payload, context) -> context.enqueueWork(() ->
            context.player().level().getEntity(payload.entityID).setData(attachment, payload.data)
//            DivineRPG.LOGGER.info("Recieved entity data packet for entity[" + payload.entityID + "] and data: " + payload.data);
        ));
        r.playToServer(requestType, requestCodec, (payload, context) -> context.enqueueWork(() ->
            context.reply(new AttachmentPayload(context.player().level().getEntity(payload.entityID).getData(attachment), payload.entityID))
        ));
    }
    @Override
    public void requestAttachment(Entity e, ServerPlayer player) {
        if(!e.level().isClientSide()) throw new UnsupportedOperationException("Only client can send Data request!");
        PacketDistributor.sendToServer(new RequestAttachmentPayload(e.getId()));
    }
    @Override
    public boolean validate(Entity e, T data) {
        if(e.level().isClientSide()) throw new UnsupportedOperationException("Server handled Data only!");
        return super.validate(e, data);
    }
    @Override
    public void update(Entity e, T data) {
//        DivineRPG.LOGGER.info("Sending entity data packet for entity[" + e.getId() + "] and data: " + data);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(e, new AttachmentPayload(data, e.getId()));
    }
    @Override
    public void setSilent(Entity e, T data) {
        try(Level level = e.level()) {if(level.isClientSide()) throw new UnsupportedOperationException("Server handled Data only!");} catch(IOException ignored) {}
        super.setSilent(e, data);
    }
    public static class Serializable<B extends Tag, S extends INBTSerializable<B>>  extends ServerHandledAttachment<S> {
        public Serializable(String name, Supplier<S> defaultValue, StreamCodec<ByteBuf, S> streamCodec) {
            super(name, AttachmentRegistry.ATTACHMENT_TYPES.register(name, () -> AttachmentType.serializable(defaultValue).build()), streamCodec);
        }
    }
}