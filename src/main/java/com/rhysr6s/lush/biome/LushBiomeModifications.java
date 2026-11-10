package com.rhysr6s.lush.biome;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import com.rhysr6s.lush.Lush;

public final class LushBiomeModifications {
	public static final ResourceKey<PlacedFeature> VEGETATION_PLACED = placed("lush_vegetation");
	public static final ResourceKey<PlacedFeature> ROCK_PLACED = placed("rock");
	public static final ResourceKey<PlacedFeature> MUSHROOM_SCATTER_PLACED = placed("mushroom_scatter");

	private LushBiomeModifications() {
	}

	private static ResourceKey<PlacedFeature> placed(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Lush.id(name));
	}

	public static void register() {
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.VEGETAL_DECORATION, VEGETATION_PLACED);

		BiomeModifications.addFeature(
			BiomeSelectors.includeByKey(
				Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW,
				Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST,
				Biomes.DARK_FOREST, Biomes.CHERRY_GROVE,
				Biomes.TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.SNOWY_TAIGA,
				Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE,
				Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_SAVANNA,
				Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS,
				Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS,
				Biomes.DESERT, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU),
			GenerationStep.Decoration.LOCAL_MODIFICATIONS, ROCK_PLACED);

		BiomeModifications.addFeature(
			BiomeSelectors.includeByKey(Biomes.MUSHROOM_FIELDS),
			GenerationStep.Decoration.VEGETAL_DECORATION, MUSHROOM_SCATTER_PLACED);
	}
}
