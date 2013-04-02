package infinitealloys.core;

import infinitealloys.util.Consts;
import java.io.Serializable;

public class WorldData implements Serializable {

	private int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];
	public int alloysUnlocked;

	public WorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}
}