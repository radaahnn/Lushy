package com.rhysr6s.lush.worldgen;

public final class NoiseField {
	private static final int BALD_CELL = 7;

	private NoiseField() {
	}

	private static float cellHash(long seed, int cellX, int cellZ) {
		long h = seed ^ -7046029254386353131L;
		h = (h + cellX * -4658895280553007687L) * -3263064605168079213L;
		h ^= h >>> 31;
		h = (h + cellZ * -7723592293110705685L) * -4658895280553007687L;
		h ^= h >>> 29;
		return (float) (h >>> 40) / 1.6777216E7F;
	}

	public static boolean isBald(long seed, int x, int z, float baldChance) {
		if (baldChance <= 0.0F) {
			return false;
		}
		int cellX = Math.floorDiv(x, BALD_CELL);
		int cellZ = Math.floorDiv(z, BALD_CELL);
		return cellHash(seed, cellX, cellZ) < baldChance;
	}
}
