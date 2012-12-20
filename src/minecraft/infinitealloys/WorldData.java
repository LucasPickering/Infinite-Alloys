package infinitealloys;

import java.io.Serializable;

public class WorldData implements Serializable {

	private int[] validAlloys = new int[References.validAlloyCount];
	public int alloysUnlocked;

	public WorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}
}
