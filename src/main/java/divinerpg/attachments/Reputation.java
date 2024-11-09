package divinerpg.attachments;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import divinerpg.attachments.base.ServerHandledAttachment;
import divinerpg.entities.base.FactionEntity;
import divinerpg.registries.AttachmentRegistry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.UnknownNullability;

import divinerpg.entities.base.FactionEntity.Faction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class Reputation extends ServerHandledAttachment<Integer> {
	public final Supplier<Faction> faction;
	public Reputation(String name, Supplier<Faction> faction) {
		super(name, () -> faction.get().startingReputation, Codec.INT, ByteBufCodecs.INT);
		this.faction = faction;
	}
	public void modify(Entity e, int reputation) {
		setSilent(e, get(e) + reputation);
	}
}