package infinitealloys.core;

import java.io.Serializable;

public class WorldData implements Serializable {

	private int[] validAlloys = new int[References.VALID_ALLOY_COUNT];
	public int alloysUnlocked;

	public WorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}
}