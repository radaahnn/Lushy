package com.rhysr6s.lush.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.rhysr6s.lush.config.ConfigManager;
import com.rhysr6s.lush.config.LushConfig;
import com.rhysr6s.lush.worldgen.Palettes;
import com.rhysr6s.lush.worldgen.Scatter;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record RockFeature() implements Feature {
	public static final MapCodec<RockFeature> CODEC = MapCodec.unit(RockFeature::new);
	private static final int PLACEMENTS_PER_CHUNK = 2;

	public MapCodec<RockFeature> codec() {
		return CODEC;
	}

	public Stream<Holder<Feature>> getSubFeatures() {
		return Stream.empty();
	}

	public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
		return Scatter.run(level, random, origin, PLACEMENTS_PER_CHUNK, pos -> placeAt(level, random, pos));
	}

	private boolean placeAt(WorldGenLevel level, RandomSource random, BlockPos origin) {
		LushConfig config = ConfigManager.getConfig();
		if (!config.rocksEnabled()) {
			return false;
		}

		Holder<Biome> biome = level.getBiome(origin);
		String group = Palettes.rockGroup(biome);
		if (group == null) {
			return false;
		}

		LushConfig.RockSettings rock = config.rockFor(group);
		float chance = Math.min(rock.chance * config.rockDensity(), 1.0F);
		if (random.nextFloat() >= chance) {
			return false;
		}

		BlockPos base = findSurface(level, origin);
		if (base == null) {
			return false;
		}

		BlockState[] slabs = config.resolvedSlabs();
		float doubleChance = config.doubleSlabChance();
		float companionChance = config.companionPebbleChance();
		int count = rock.min + random.nextInt(Math.max(1, rock.max - rock.min) + 1);
		boolean placed = false;

		for (int i = 0; i < count; i++) {
			BlockPos pos = findSurface(level, base.offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4));
			if (pos != null && canPlaceRock(level, pos)) {
				placed |= placeRock(level, pos, random, slabs, doubleChance, companionChance);
			}
		}

		return placed;
	}

	private static BlockPos findSurface(WorldGenLevel level, BlockPos pos) {
		int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());

		for (int y = top; y > level.getMinY(); y--) {
			BlockPos at = new BlockPos(pos.getX(), y, pos.getZ());
			if (level.getBlockState(at).isAir() && isSuitableGround(level, at.below())) {
				return at;
			}
		}

		return null;
	}

	private static boolean isSuitableGround(WorldGenLevel level, BlockPos groundPos) {
		BlockState s = level.getBlockState(groundPos);
		return !s.isAir()
			&& (s.is(BlockTags.BASE_STONE_OVERWORLD)
				|| s.is(BlockTags.TERRACOTTA)
				|| s.is(Blocks.GRAVEL)
				|| s.is(Blocks.SAND)
				|| s.is(Blocks.RED_SAND)
				|| s.is(Blocks.GRASS_BLOCK))
			&& s.isFaceSturdy(level, groundPos, Direction.UP);
	}

	private static boolean canPlaceRock(WorldGenLevel level, BlockPos pos) {
		return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
	}

	private static boolean placeRock(WorldGenLevel level, BlockPos pos, RandomSource random, BlockState[] slabs, float doubleChance, float companionChance) {
		BlockState rock = slabs[random.nextInt(slabs.length)]
			.setValue(BlockStateProperties.SLAB_TYPE, random.nextFloat() < doubleChance ? SlabType.DOUBLE : SlabType.BOTTOM);
		level.setBlock(pos, rock, 2);
		if (rock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE && random.nextFloat() < companionChance) {
			BlockPos side = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
			if (level.getBlockState(side).isAir() && level.getBlockState(side.below()).isFaceSturdy(level, side.below(), Direction.UP)) {
				level.setBlock(side, slabs[random.nextInt(slabs.length)].setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), 2);
			}
		}

		return true;
	}
}
