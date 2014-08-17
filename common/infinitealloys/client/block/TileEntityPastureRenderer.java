package infinitealloys.client.block;

import infinitealloys.util.EnumMachine;

public class TileEntityPastureRenderer extends TileEntityMachineRenderer {

	public TileEntityPastureRenderer() {
		super(EnumMachine.PASTURE.getName(), new ModelPasture());
	}
}
