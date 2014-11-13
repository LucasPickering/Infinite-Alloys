package infinitealloys.client.render.block;

import infinitealloys.client.model.block.ModelEnergyStorage;
import infinitealloys.util.EnumMachine;

public class TileEntityEnergyStorageRenderer extends TileEntityMachineRenderer {

	public TileEntityEnergyStorageRenderer() {
		super(EnumMachine.ENERGY_STORAGE.name, new ModelEnergyStorage());
	}
}
