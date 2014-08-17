package infinitealloys.client.block;

import infinitealloys.util.EnumMachine;

public class TileEntityComputerRenderer extends TileEntityMachineRenderer {

	public TileEntityComputerRenderer() {
		super(EnumMachine.COMPUTER.getName(), new ModelComputer());
	}
}
