package infinitealloys;

import java.io.Serializable;

public class IAWorldData implements Serializable {

	private int[] validAlloys = new int[References.validAlloyCount];
	public int alloysUnlocked;

	public IAWorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}
}
