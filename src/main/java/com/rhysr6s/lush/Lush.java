package com.rhysr6s.lush;

import com.mojang.logging.LogUtils;
import com.rhysr6s.lush.config.ConfigManager;
import java.nio.file.Path;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public final class Lush {
	public static final String MOD_ID = "lush";
	private static final Logger LOGGER = LogUtils.getLogger();

	private Lush() {
	}

	public static void init(Path configDir) {
		ConfigManager.init(configDir);
		LOGGER.info("[Lush] initialised");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static Identifier parse(String id) {
		return Identifier.tryParse(id);
	}

	public static boolean isDebugEnabled() {
		try {
			return ConfigManager.getConfig().isDebugLoggingEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	public static void debugLog(String message) {
		if (isDebugEnabled()) {
			LOGGER.info("[Lush] {}", message);
		}
	}
}
