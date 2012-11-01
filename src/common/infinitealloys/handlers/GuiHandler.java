package infinitealloys.handlers;

import cpw.mods.fml.common.network.IGuiHandler;
import infinitealloys.ContainerAnalyzer;
import infinitealloys.ContainerMachine;
import infinitealloys.ContainerMetalForge;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityMachine;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMetalForge;
import infinitealloys.client.GuiAnalyzer;
import infinitealloys.client.GuiComputer;
import infinitealloys.client.GuiMetalForge;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

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
		}
		return null;
	}
}
