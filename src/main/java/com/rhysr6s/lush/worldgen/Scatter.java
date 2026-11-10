package com.rhysr6s.lush.worldgen;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;

/** Replaces the count / in_square / heightmap(WORLD_SURFACE_WG) placement modifiers the original used. */
public final class Scatter {
	private Scatter() {
	}

	public static boolean run(WorldGenLevel level, RandomSource random, BlockPos chunkOrigin, int count, Predicate<BlockPos> placer) {
		boolean any = false;
		for (int i = 0; i < count; i++) {
			int x = chunkOrigin.getX() + random.nextInt(16);
			int z = chunkOrigin.getZ() + random.nextInt(16);
			int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
			if (placer.test(new BlockPos(x, y, z))) {
				any = true;
			}
		}
		return any;
	}
}
