package infinitealloys.client.render.block;

import infinitealloys.client.model.block.ModelMetalForge;
import infinitealloys.util.EnumMachine;

public class TileEntityMetalForgeRenderer extends TileEntityMachineRenderer {

	public TileEntityMetalForgeRenderer() {
		super(EnumMachine.METAL_FORGE.getName(), new ModelMetalForge());
	}
}
