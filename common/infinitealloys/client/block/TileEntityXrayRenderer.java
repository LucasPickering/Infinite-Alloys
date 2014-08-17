package infinitealloys.client.block;

import infinitealloys.util.EnumMachine;

public class TileEntityXrayRenderer extends TileEntityMachineRenderer {

	public TileEntityXrayRenderer() {
		super(EnumMachine.XRAY.getName(), new ModelPasture());
	}
}
