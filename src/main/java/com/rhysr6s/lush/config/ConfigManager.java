package com.rhysr6s.lush.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;

public final class ConfigManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String FILE = "lush.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static Path configDir;
	private static LushConfig config;

	private ConfigManager() {
	}

	public static void init(Path dir) {
		configDir = dir;
		loadConfig();
	}

	public static synchronized LushConfig getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	public static synchronized void loadConfig() {
		Path path = path();
		if (path != null && Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				LushConfig parsed = GSON.fromJson(reader, LushConfig.class);
				if (parsed != null) {
					config = parsed;
					if (ConfigMigrations.migrate(config)) {
						config.invalidateSlabs();
						backup(path);
						saveConfig();
					}
					return;
				}
				LOGGER.warn("[Lush] {} was empty; regenerating defaults.", FILE);
			} catch (Exception e) {
				LOGGER.error("[Lush] Failed to read {} ({}); keeping it as {}.bak and using defaults.", FILE, e.getMessage(), FILE);
				backup(path);
			}
		}
		config = freshDefaults();
		saveConfig();
	}

	public static synchronized void saveConfig() {
		if (config == null) {
			config = freshDefaults();
		}
		Path path = path();
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException e) {
			LOGGER.error("[Lush] Failed to save {}: {}", FILE, e.getMessage());
		}
	}

	private static Path path() {
		return configDir == null ? null : configDir.resolve(FILE);
	}

	private static LushConfig freshDefaults() {
		LushConfig c = new LushConfig();
		c.configVersion = ConfigMigrations.CURRENT_VERSION;
		return c;
	}

	private static void backup(Path path) {
		try {
			Files.copy(path, path.resolveSibling(FILE + ".bak"), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ignored) {
		}
	}
}
