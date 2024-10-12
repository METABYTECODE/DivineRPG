package divinerpg.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record UniversalPosition(ResourceKey<Level> dimension, Vec3 pos) {
	public UniversalPosition(Level level, Vec3 pos) {
		this(level.dimension(), pos);
	}
	public UniversalPosition(ResourceKey<Level> dimension, BlockPos pos) {
		this(dimension, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
	}
	public UniversalPosition(Level level, BlockPos pos) {
		this(level.dimension(), new Vec3(pos.getX(), pos.getY(), pos.getZ()));
	}
	public BlockPos blockPos() {
		return new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
	}
	public ChunkPos chunkPos() {
		return new ChunkPos((int)pos.x >> 4, (int)pos.z >> 4);
	}
	public ServerLevel level(MinecraftServer server) {
		return server.getLevel(dimension);
	}
	@Override
	public final boolean equals(Object o) {
		return o != null && o instanceof UniversalPosition p && p.dimension.location().equals(dimension.location()) && p.pos.equals(pos);
	}
	public static BlockPos toBlockPos(Vec3 v) {
		return new BlockPos((int)v.x, (int)v.y, (int)v.z);
	}
}