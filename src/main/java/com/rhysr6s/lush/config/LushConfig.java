package com.rhysr6s.lush.config;

import com.mojang.logging.LogUtils;
import com.rhysr6s.lush.Lush;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class LushConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   public int configVersion = 0;
   public LushConfig.General general = new LushConfig.General();
   public LushConfig.Rocks rocks = new LushConfig.Rocks();
   public LushConfig.Mushrooms mushrooms = new LushConfig.Mushrooms();
   public Map<String, LushConfig.VegetationPalette> vegetation = defaultVegetation();
   public Map<String, LushConfig.RockSettings> biomeRocks = defaultRocks();
   private transient BlockState[] resolvedSlabs;
   private static final LushConfig.VegetationPalette DEFAULT_VEGETATION = new LushConfig.VegetationPalette(0.5F, 0.05F, 0.85F, 0.05F, 0.1F, 0.0F);
   private static final LushConfig.RockSettings DEFAULT_ROCK = new LushConfig.RockSettings(0.05F, 1, 1);

   public float vegetationDensity() {
      return this.general.vegetationDensity;
   }

   public float vegetationBoost() {
      return this.general.vegetationBoost;
   }

   public float rockDensity() {
      return this.general.rockDensity;
   }

   public float snowReplaceChance() {
      return this.general.snowReplaceChance;
   }

   public boolean rocksEnabled() {
      return this.general.enableRocks;
   }

   public boolean mushroomsEnabled() {
      return this.general.enableMushrooms;
   }

   public boolean grassTintEnabled() {
      return this.general.enableGrassTint;
   }

   public boolean isDebugLoggingEnabled() {
      return this.general.debugLogging;
   }

   public int mushroomCount() {
      return Math.max(0, this.mushrooms.countPerPatch);
   }

   public float mushroomRedChance() {
      return this.mushrooms.redMushroomChance;
   }

   public float doubleSlabChance() {
      return this.rocks.doubleSlabChance;
   }

   public float companionPebbleChance() {
      return this.rocks.companionPebbleChance;
   }

   public LushConfig.VegetationPalette vegetationFor(String group) {
      LushConfig.VegetationPalette p = this.vegetation.get(group);
      return p != null ? p : DEFAULT_VEGETATION;
   }

   public LushConfig.RockSettings rockFor(String group) {
      LushConfig.RockSettings r = this.biomeRocks.get(group);
      return r != null ? r : DEFAULT_ROCK;
   }

   public BlockState[] resolvedSlabs() {
      if (this.resolvedSlabs == null) {
         this.resolvedSlabs = this.resolveSlabs();
      }

      return this.resolvedSlabs;
   }

   public void invalidateSlabs() {
      this.resolvedSlabs = null;
   }

   private BlockState[] resolveSlabs() {
      List<BlockState> out = new ArrayList<>();

      for (String id : this.rocks.slabBlocks) {
         Identifier rl = Lush.parse(id);
         Block block = rl == null ? null : BuiltInRegistries.BLOCK.getOptional(rl).orElse(null);
         if (block instanceof SlabBlock) {
            out.add(block.defaultBlockState());
         } else {
            LOGGER.warn("[Lush] Ignoring invalid slab block in config: '{}' (not a slab).", id);
         }
      }

      if (out.isEmpty()) {
         LOGGER.warn("[Lush] No valid slab blocks configured; falling back to stone_slab.");
         out.add(Blocks.STONE_SLAB.defaultBlockState());
      }

      return out.toArray(new BlockState[0]);
   }

   private static Map<String, LushConfig.VegetationPalette> defaultVegetation() {
      Map<String, LushConfig.VegetationPalette> m = new LinkedHashMap<>();
      m.put("plains", new LushConfig.VegetationPalette(0.7F, 0.06F, 0.84F, 0.14F, 0.0F, 0.02F));
      m.put("meadow", new LushConfig.VegetationPalette(0.8F, 0.04F, 0.7F, 0.15F, 0.1F, 0.05F));
      m.put("forest", new LushConfig.VegetationPalette(0.75F, 0.04F, 0.8F, 0.04F, 0.14F, 0.02F));
      m.put("birch_forest", new LushConfig.VegetationPalette(0.72F, 0.04F, 0.86F, 0.05F, 0.07F, 0.02F));
      m.put("flower_forest", new LushConfig.VegetationPalette(0.85F, 0.03F, 0.66F, 0.12F, 0.14F, 0.08F));
      m.put("dark_forest", new LushConfig.VegetationPalette(0.7F, 0.04F, 0.55F, 0.05F, 0.28F, 0.12F));
      m.put("taiga", new LushConfig.VegetationPalette(0.7F, 0.05F, 0.45F, 0.05F, 0.4F, 0.1F));
      m.put("old_growth_pine", new LushConfig.VegetationPalette(0.68F, 0.05F, 0.35F, 0.05F, 0.4F, 0.2F));
      m.put("old_growth_spruce", new LushConfig.VegetationPalette(0.68F, 0.05F, 0.38F, 0.05F, 0.4F, 0.17F));
      m.put("jungle", new LushConfig.VegetationPalette(0.9F, 0.02F, 0.55F, 0.2F, 0.2F, 0.05F));
      m.put("sparse_jungle", new LushConfig.VegetationPalette(0.7F, 0.04F, 0.6F, 0.15F, 0.2F, 0.05F));
      m.put("windswept", new LushConfig.VegetationPalette(0.42F, 0.1F, 0.92F, 0.04F, 0.04F, 0.0F));
      m.put("savanna", new LushConfig.VegetationPalette(0.38F, 0.12F, 0.92F, 0.08F, 0.0F, 0.0F));
      m.put("cherry_grove", new LushConfig.VegetationPalette(0.8F, 0.03F, 0.66F, 0.1F, 0.16F, 0.08F));
      m.put("swamp", new LushConfig.VegetationPalette(0.75F, 0.06F, 0.6F, 0.1F, 0.25F, 0.05F));
      m.put("snowy", new LushConfig.VegetationPalette(0.3F, 0.07F, 0.6F, 0.05F, 0.3F, 0.05F));
      m.put("mushroom", new LushConfig.VegetationPalette(0.55F, 0.05F, 0.65F, 0.1F, 0.2F, 0.05F));
      return m;
   }

   private static Map<String, LushConfig.RockSettings> defaultRocks() {
      Map<String, LushConfig.RockSettings> m = new LinkedHashMap<>();
      m.put("plains", new LushConfig.RockSettings(0.08F, 1, 2));
      m.put("forest", new LushConfig.RockSettings(0.06F, 1, 1));
      m.put("taiga", new LushConfig.RockSettings(0.05F, 1, 1));
      m.put("mountains", new LushConfig.RockSettings(0.22F, 1, 3));
      m.put("windswept_forest", new LushConfig.RockSettings(0.16F, 1, 3));
      m.put("badlands", new LushConfig.RockSettings(0.12F, 1, 3));
      m.put("desert", new LushConfig.RockSettings(0.1F, 1, 2));
      m.put("savanna", new LushConfig.RockSettings(0.09F, 1, 2));
      m.put("jungle", new LushConfig.RockSettings(0.06F, 1, 2));
      return m;
   }

   public static class General {
      public float vegetationDensity = 1.0F;
      public float vegetationBoost = 1.5F;
      public float rockDensity = 1.0F;
      public float snowReplaceChance = 0.85F;
      public boolean enableRocks = true;
      public boolean enableMushrooms = true;
      public boolean enableGrassTint = true;
      public boolean debugLogging = false;
   }

   public static class Mushrooms {
      public int countPerPatch = 12;
      public float redMushroomChance = 0.4F;
   }

   public static class RockSettings {
      public float chance;
      public int min;
      public int max;

      public RockSettings() {
      }

      public RockSettings(float chance, int min, int max) {
         this.chance = chance;
         this.min = min;
         this.max = max;
      }
   }

   public static class Rocks {
      public List<String> slabBlocks = new ArrayList<>(List.of("minecraft:stone_slab", "minecraft:cobblestone_slab", "minecraft:andesite_slab"));
      public float doubleSlabChance = 0.15F;
      public float companionPebbleChance = 0.4F;
   }

   public static class VegetationPalette {
      public float density;
      public float bald;
      public float shortGrass;
      public float tallGrass;
      public float fern;
      public float largeFern;

      public VegetationPalette() {
      }

      public VegetationPalette(float density, float bald, float shortGrass, float tallGrass, float fern, float largeFern) {
         this.density = density;
         this.bald = bald;
         this.shortGrass = shortGrass;
         this.tallGrass = tallGrass;
         this.fern = fern;
         this.largeFern = largeFern;
      }

      public float totalWeight() {
         return this.shortGrass + this.tallGrass + this.fern + this.largeFern;
      }
   }
}
