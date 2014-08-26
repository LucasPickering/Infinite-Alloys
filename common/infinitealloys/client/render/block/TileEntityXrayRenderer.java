package infinitealloys.client.render.block;

import infinitealloys.client.model.block.ModelXray;
import infinitealloys.util.EnumMachine;

public class TileEntityXrayRenderer extends TileEntityMachineRenderer {

	public TileEntityXrayRenderer() {
		super(EnumMachine.XRAY.getName(), new ModelXray());
	}
}
