package infinitealloys.client.render.block;

import infinitealloys.client.model.block.ModelComputer;
import infinitealloys.util.EnumMachine;

public class TileEntityComputerRenderer extends TileEntityMachineRenderer {

  public TileEntityComputerRenderer() {
    super(EnumMachine.COMPUTER.getName(), new ModelComputer());
  }
}
