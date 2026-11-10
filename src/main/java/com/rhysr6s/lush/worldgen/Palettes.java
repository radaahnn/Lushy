package com.rhysr6s.lush.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class Palettes {
	private Palettes() {
	}

	public static String vegetationGroup(Holder<Biome> b) {
		if (b.is(Biomes.PLAINS) || b.is(Biomes.SUNFLOWER_PLAINS)) {
			return "plains";
		} else if (b.is(Biomes.MEADOW)) {
			return "meadow";
		} else if (b.is(Biomes.FOREST)) {
			return "forest";
		} else if (b.is(Biomes.BIRCH_FOREST) || b.is(Biomes.OLD_GROWTH_BIRCH_FOREST)) {
			return "birch_forest";
		} else if (b.is(Biomes.FLOWER_FOREST)) {
			return "flower_forest";
		} else if (b.is(Biomes.DARK_FOREST)) {
			return "dark_forest";
		} else if (b.is(Biomes.TAIGA)) {
			return "taiga";
		} else if (b.is(Biomes.OLD_GROWTH_PINE_TAIGA)) {
			return "old_growth_pine";
		} else if (b.is(Biomes.OLD_GROWTH_SPRUCE_TAIGA)) {
			return "old_growth_spruce";
		} else if (b.is(Biomes.JUNGLE) || b.is(Biomes.BAMBOO_JUNGLE)) {
			return "jungle";
		} else if (b.is(Biomes.SPARSE_JUNGLE)) {
			return "sparse_jungle";
		} else if (b.is(Biomes.WINDSWEPT_HILLS) || b.is(Biomes.WINDSWEPT_FOREST)
			|| b.is(Biomes.WINDSWEPT_GRAVELLY_HILLS) || b.is(Biomes.WINDSWEPT_SAVANNA)) {
			return "windswept";
		} else if (b.is(Biomes.SAVANNA) || b.is(Biomes.SAVANNA_PLATEAU)) {
			return "savanna";
		} else if (b.is(Biomes.CHERRY_GROVE)) {
			return "cherry_grove";
		} else if (b.is(Biomes.SWAMP) || b.is(Biomes.MANGROVE_SWAMP)) {
			return "swamp";
		} else if (b.is(Biomes.SNOWY_PLAINS) || b.is(Biomes.SNOWY_TAIGA) || b.is(Biomes.GROVE)) {
			return "snowy";
		} else {
			return b.is(Biomes.MUSHROOM_FIELDS) ? "mushroom" : null;
		}
	}

	public static String rockGroup(Holder<Biome> b) {
		if (b.is(Biomes.WINDSWEPT_HILLS) || b.is(Biomes.WINDSWEPT_GRAVELLY_HILLS)
			|| b.is(Biomes.JAGGED_PEAKS) || b.is(Biomes.STONY_PEAKS)) {
			return "mountains";
		} else if (b.is(Biomes.WINDSWEPT_FOREST)) {
			return "windswept_forest";
		} else if (b.is(Biomes.BADLANDS) || b.is(Biomes.ERODED_BADLANDS) || b.is(Biomes.WOODED_BADLANDS)) {
			return "badlands";
		} else if (b.is(Biomes.DESERT)) {
			return "desert";
		} else if (b.is(Biomes.SAVANNA) || b.is(Biomes.SAVANNA_PLATEAU) || b.is(Biomes.WINDSWEPT_SAVANNA)) {
			return "savanna";
		} else if (b.is(Biomes.PLAINS) || b.is(Biomes.MEADOW) || b.is(Biomes.SUNFLOWER_PLAINS)) {
			return "plains";
		} else if (b.is(Biomes.FOREST) || b.is(Biomes.FLOWER_FOREST) || b.is(Biomes.BIRCH_FOREST)
			|| b.is(Biomes.OLD_GROWTH_BIRCH_FOREST) || b.is(Biomes.DARK_FOREST) || b.is(Biomes.CHERRY_GROVE)) {
			return "forest";
		} else if (b.is(Biomes.TAIGA) || b.is(Biomes.OLD_GROWTH_PINE_TAIGA)
			|| b.is(Biomes.OLD_GROWTH_SPRUCE_TAIGA) || b.is(Biomes.SNOWY_TAIGA)) {
			return "taiga";
		} else {
			return b.is(Biomes.JUNGLE) || b.is(Biomes.SPARSE_JUNGLE) || b.is(Biomes.BAMBOO_JUNGLE) ? "jungle" : null;
		}
	}
}
