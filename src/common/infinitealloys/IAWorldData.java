package infinitealloys;

import java.io.Serializable;

public class IAWorldData implements Serializable {

	public int[] validAlloys = new int[References.validAlloyCount];

	public IAWorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloy() {
		return validAlloys;
	}
}
