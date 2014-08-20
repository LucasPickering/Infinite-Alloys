package infinitealloys.client.block;

import infinitealloys.util.EnumMachine;

public class TileEntityEnergyStorageRenderer extends TileEntityMachineRenderer {

	public TileEntityEnergyStorageRenderer() {
		super(EnumMachine.ENERGY_STORAGE.getName(), new ModelEnergyStorage());
	}
}
