package infinitealloys.core;

import infinitealloys.util.Consts;
import java.io.Serializable;

public class WorldData implements Serializable {

	private int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];

	/** A number from 0 to {@link infinitealloys.util.Consts.VALID_ALLOY_COUNT Consts.VALID_ALLOY_COUNT} that represents how many alloys have been unlocked */
	private int unlockedAlloyCount;

	public WorldData(int[] validAlloys) {
		this.validAlloys = validAlloys;
	}

	public int[] getValidAlloys() {
		return validAlloys;
	}

	public int getUnlockedAlloyCount() {
		return unlockedAlloyCount;
	}

	public void incrUnlockedAlloyCount() {
		unlockedAlloyCount++;
	}
}