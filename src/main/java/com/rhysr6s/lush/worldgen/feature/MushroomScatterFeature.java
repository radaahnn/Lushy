package com.rhysr6s.lush.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.rhysr6s.lush.config.ConfigManager;
import com.rhysr6s.lush.config.LushConfig;
import com.rhysr6s.lush.worldgen.Scatter;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record MushroomScatterFeature() implements Feature {
	public static final MapCodec<MushroomScatterFeature> CODEC = MapCodec.unit(MushroomScatterFeature::new);
	private static final int PLACEMENTS_PER_CHUNK = 5;

	public MapCodec<MushroomScatterFeature> codec() {
		return CODEC;
	}

	public Stream<Holder<Feature>> getSubFeatures() {
		return Stream.empty();
	}

	public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
		return Scatter.run(level, random, origin, PLACEMENTS_PER_CHUNK, pos -> placeAt(level, random, pos));
	}

	private static boolean placeAt(WorldGenLevel level, RandomSource random, BlockPos origin) {
		if (!level.getBiome(origin).is(Biomes.MUSHROOM_FIELDS)) {
			return false;
		}
		LushConfig config = ConfigManager.getConfig();
		if (!config.mushroomsEnabled()) {
			return false;
		}

		int count = config.mushroomCount();
		float redChance = config.mushroomRedChance();
		boolean placed = false;

		for (int i = 0; i < count; i++) {
			BlockPos pos = origin.offset(random.nextInt(8) - random.nextInt(8), 0, random.nextInt(8) - random.nextInt(8));

			while (pos.getY() > level.getMinY()
				&& level.isEmptyBlock(pos)
				&& !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
				pos = pos.below();
			}

			BlockState mushroom = random.nextFloat() < redChance ? Blocks.RED_MUSHROOM.defaultBlockState() : Blocks.BROWN_MUSHROOM.defaultBlockState();
			if (level.isEmptyBlock(pos) && mushroom.canSurvive(level, pos)) {
				level.setBlock(pos, mushroom, 2);
				placed = true;
			}
		}

		return placed;
	}
}
