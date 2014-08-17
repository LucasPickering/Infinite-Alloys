package infinitealloys.client.block;

import infinitealloys.util.EnumMachine;

public class TileEntityMetalForgeRenderer extends TileEntityMachineRenderer {

	public TileEntityMetalForgeRenderer() {
		super(EnumMachine.METAL_FORGE.getName(), new ModelMetalForge());
	}
}
