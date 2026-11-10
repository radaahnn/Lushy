package com.rhysr6s.lush.fabric;

import com.rhysr6s.lush.Lush;
import com.rhysr6s.lush.biome.LushBiomeModifications;
import com.rhysr6s.lush.worldgen.feature.MushroomScatterFeature;
import com.rhysr6s.lush.worldgen.feature.RockFeature;
import com.rhysr6s.lush.worldgen.feature.VegetationFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class LushFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Lush.init(FabricLoader.getInstance().getConfigDir());
		Registry.register(BuiltInRegistries.FEATURE_TYPE, Lush.id("lush_vegetation"), VegetationFeature.CODEC);
		Registry.register(BuiltInRegistries.FEATURE_TYPE, Lush.id("rock"), RockFeature.CODEC);
		Registry.register(BuiltInRegistries.FEATURE_TYPE, Lush.id("mushroom_scatter"), MushroomScatterFeature.CODEC);
		LushBiomeModifications.register();
	}
}
