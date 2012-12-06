package infinitealloys.handlers;

import infinitealloys.References;
import infinitealloys.client.GuiAnalyzer;
import infinitealloys.client.GuiComputer;
import infinitealloys.client.GuiMetalForge;
import infinitealloys.client.GuiPrinter;
import infinitealloys.client.GuiXray;
import infinitealloys.inventory.ContainerAnalyzer;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.inventory.ContainerMetalForge;
import infinitealloys.inventory.ContainerPrinter;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
				return new ContainerMachine(player.inventory, (TileEntityMachine)tileEntity);
			case 1:
				return new ContainerMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
			case 2:
				return new ContainerAnalyzer(player.inventory, (TileEntityAnalyzer)tileEntity);
			case 3:
				return new ContainerPrinter(player.inventory, (TileEntityPrinter)tileEntity);
			case 4:
				return new ContainerXray(player.inventory, (TileEntityXray)tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
				return new GuiComputer(player.inventory, (TileEntityComputer)tileEntity);
			case 1:
				return new GuiMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
			case 2:
				return new GuiAnalyzer(player.inventory, (TileEntityAnalyzer)tileEntity);
			case 3:
				return new GuiPrinter(player.inventory, (TileEntityPrinter)tileEntity);
			case 4:
				return new GuiXray(player.inventory, (TileEntityXray)tileEntity);
		}
		return null;
	}
}
