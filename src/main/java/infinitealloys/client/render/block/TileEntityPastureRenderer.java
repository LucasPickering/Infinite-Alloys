package infinitealloys.client.render.block;

import infinitealloys.client.model.block.ModelPasture;
import infinitealloys.util.EnumMachine;

public class TileEntityPastureRenderer extends TileEntityMachineRenderer {

  public TileEntityPastureRenderer() {
    super(EnumMachine.PASTURE.getName(), new ModelPasture());
  }
}
