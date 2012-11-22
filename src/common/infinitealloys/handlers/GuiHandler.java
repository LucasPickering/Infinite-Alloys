package infinitealloys.handlers;

import infinitealloys.ContainerAnalyzer;
import infinitealloys.ContainerMachine;
import infinitealloys.ContainerMetalForge;
import infinitealloys.ContainerPrinter;
import infinitealloys.ContainerXray;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMachine;
import infinitealloys.TileEntityMetalForge;
import infinitealloys.TileEntityPrinter;
import infinitealloys.TileEntityXray;
import infinitealloys.client.GuiAlloyBook;
import infinitealloys.client.GuiAnalyzer;
import infinitealloys.client.GuiComputer;
import infinitealloys.client.GuiMetalForge;
import infinitealloys.client.GuiPrinter;
import infinitealloys.client.GuiXray;
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
			case References.machineCount:
				return new GuiAlloyBook(player.getHeldItem());
		}
		return null;
	}
}
