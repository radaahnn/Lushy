package com.rhysr6s.lush.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.rhysr6s.lush.config.ConfigManager;
import com.rhysr6s.lush.config.LushConfig;
import com.rhysr6s.lush.worldgen.LushBlocks;
import com.rhysr6s.lush.worldgen.NoiseField;
import com.rhysr6s.lush.worldgen.Palettes;
import com.rhysr6s.lush.worldgen.Scatter;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record VegetationFeature() implements Feature {
	public static final MapCodec<VegetationFeature> CODEC = MapCodec.unit(VegetationFeature::new);
	private static final int PLACEMENTS_PER_CHUNK = 40;
	private static final int FLAG = 2;
	private static final float SNOW_TEMPERATURE = 0.15F;

	private enum Plant {
		SHORT_GRASS,
		FERN,
		TALL_GRASS,
		LARGE_FERN
	}

	public MapCodec<VegetationFeature> codec() {
		return CODEC;
	}

	public Stream<Holder<Feature>> getSubFeatures() {
		return Stream.empty();
	}

	public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
		return Scatter.run(level, random, origin, PLACEMENTS_PER_CHUNK, pos -> placeAt(level, random, pos));
	}

	private boolean placeAt(WorldGenLevel level, RandomSource random, BlockPos origin) {
		long seed = level.getSeed();
		Holder<Biome> biome = level.getBiome(origin);
		LushConfig config = ConfigManager.getConfig();
		boolean snowy = biome.value().getBaseTemperature() < SNOW_TEMPERATURE;
		String group = Palettes.vegetationGroup(biome);
		return group == null
			? boost(level, random, origin, config, snowy)
			: authored(level, random, origin, biome, config.vegetationFor(group), config, seed, snowy);
	}

	private boolean authored(WorldGenLevel level, RandomSource random, BlockPos origin, Holder<Biome> biome,
			LushConfig.VegetationPalette pal, LushConfig config, long seed, boolean snowy) {
		boolean mushroom = biome.is(Biomes.MUSHROOM_FIELDS);
		float density = pal.density * config.vegetationDensity();
		float snowReplace = config.snowReplaceChance();
		boolean placed = false;
		BlockPos anchor = findGround(level, origin, mushroom);
		if (anchor != null && !NoiseField.isBald(seed, anchor.getX(), anchor.getZ(), pal.bald)) {
			Plant tall = pickTall(random, pal);
			if (tall != null) {
				placed |= cluster(level, random, anchor, tall, mushroom, snowy, snowReplace);
			}
		}

		int attempts = Math.round((8 + random.nextInt(8)) * density);

		for (int i = 0; i < attempts; i++) {
			BlockPos pos = findGround(level, origin.offset(random.nextInt(7) - 3, 0, random.nextInt(7) - 3), mushroom);
			if (pos != null
				&& !NoiseField.isBald(seed, pos.getX(), pos.getZ(), pal.bald)
				&& !suppressedBySnow(random, snowy, snowReplace)
				&& placePlant(level, pos, pickPlant(random, pal))) {
				placed = true;
			}
		}

		return placed;
	}

	private boolean cluster(WorldGenLevel level, RandomSource random, BlockPos center, Plant plant, boolean mushroom, boolean snowy, float snowReplace) {
		int radius = plant == Plant.LARGE_FERN ? 2 : 3;
		float fill = plant == Plant.LARGE_FERN ? 0.7F : 0.85F;
		boolean placed = !suppressedBySnow(random, snowy, snowReplace) && placePlant(level, center, plant);

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if ((dx != 0 || dz != 0)
					&& Math.abs(dx) + Math.abs(dz) <= radius
					&& !(random.nextFloat() >= fill)
					&& !suppressedBySnow(random, snowy, snowReplace)) {
					BlockPos pos = findGround(level, center.offset(dx, 0, dz), mushroom);
					if (pos != null && placePlant(level, pos, plant)) {
						placed = true;
					}
				}
			}
		}

		return placed;
	}

	private boolean boost(WorldGenLevel level, RandomSource random, BlockPos origin, LushConfig config, boolean snowy) {
		float boost = config.vegetationBoost();
		if (boost <= 0.0F) {
			return false;
		}

		float snowReplace = config.snowReplaceChance();
		boolean placed = false;
		int attempts = Math.round(6.0F * boost);

		for (int i = 0; i < attempts; i++) {
			BlockPos pos = findGround(level, origin.offset(random.nextInt(7) - 3, 0, random.nextInt(7) - 3), false);
			if (pos != null && !suppressedBySnow(random, snowy, snowReplace)) {
				Plant plant = random.nextFloat() < 0.1F ? Plant.FERN : Plant.SHORT_GRASS;
				if (placePlant(level, pos, plant)) {
					placed = true;
				}
			}
		}

		return placed;
	}

	private static boolean suppressedBySnow(RandomSource random, boolean snowy, float chance) {
		return snowy && random.nextFloat() < chance;
	}

	private boolean placePlant(WorldGenLevel level, BlockPos pos, Plant plant) {
		return switch (plant) {
			case SHORT_GRASS -> placeShort(level, pos, LushBlocks.SHORT_GRASS.defaultBlockState());
			case FERN -> placeShort(level, pos, Blocks.FERN.defaultBlockState());
			case TALL_GRASS -> placeTall(level, pos, Blocks.TALL_GRASS);
			case LARGE_FERN -> placeTall(level, pos, Blocks.LARGE_FERN);
		};
	}

	private boolean placeShort(WorldGenLevel level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state, FLAG);
		return true;
	}

	private boolean placeTall(WorldGenLevel level, BlockPos pos, Block doublePlant) {
		if (!level.getBlockState(pos.above()).isAir()) {
			return false;
		}

		level.setBlock(pos, doublePlant.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), FLAG);
		level.setBlock(pos.above(), doublePlant.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), FLAG);
		return true;
	}

	private static BlockPos findGround(WorldGenLevel level, BlockPos pos, boolean mushroom) {
		for (int dy = 2; dy >= -3; dy--) {
			BlockPos p = pos.offset(0, dy, 0);
			if (level.getBlockState(p).isAir() && canGrowOn(level.getBlockState(p.below()), mushroom)) {
				return p;
			}
		}

		return null;
	}

	private static boolean canGrowOn(BlockState ground, boolean mushroom) {
		return mushroom
			? ground.is(Blocks.MYCELIUM)
			: ground.is(Blocks.GRASS_BLOCK)
				|| ground.is(Blocks.PODZOL)
				|| ground.is(Blocks.DIRT)
				|| ground.is(Blocks.COARSE_DIRT)
				|| ground.is(Blocks.MYCELIUM);
	}

	private static Plant pickPlant(RandomSource random, LushConfig.VegetationPalette pal) {
		float total = pal.totalWeight();
		if (total <= 0.0F) {
			return Plant.SHORT_GRASS;
		}
		float r = random.nextFloat() * total;
		if ((r -= pal.shortGrass) < 0.0F) {
			return Plant.SHORT_GRASS;
		} else if ((r -= pal.tallGrass) < 0.0F) {
			return Plant.TALL_GRASS;
		} else {
			return r - pal.fern < 0.0F ? Plant.FERN : Plant.LARGE_FERN;
		}
	}

	private static Plant pickTall(RandomSource random, LushConfig.VegetationPalette pal) {
		float roll = random.nextFloat();
		if (roll < pal.largeFern) {
			return Plant.LARGE_FERN;
		}
		return roll < pal.largeFern + pal.tallGrass ? Plant.TALL_GRASS : null;
	}
}
