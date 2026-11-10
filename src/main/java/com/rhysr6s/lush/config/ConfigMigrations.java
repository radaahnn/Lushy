package com.rhysr6s.lush.config;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;

final class ConfigMigrations {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final List<Consumer<LushConfig>> STEPS = List.of(cfg -> {
		LushConfig defaults = new LushConfig();
		cfg.rocks.slabBlocks = defaults.rocks.slabBlocks;
		cfg.biomeRocks = defaults.biomeRocks;
	});
	static final int CURRENT_VERSION = STEPS.size();

	private ConfigMigrations() {
	}

	static boolean migrate(LushConfig config) {
		int original = config.configVersion;
		int from = Math.max(0, original);
		if (from >= CURRENT_VERSION) {
			return false;
		}
		for (int v = from; v < CURRENT_VERSION; v++) {
			STEPS.get(v).accept(config);
		}
		config.configVersion = CURRENT_VERSION;
		LOGGER.info("[Lush] Migrated config from schema version {} to {}.", original, CURRENT_VERSION);
		return true;
	}
}
