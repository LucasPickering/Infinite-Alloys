package infinitealloys;

import infinitealloys.client.GuiComputer;
import infinitealloys.client.GuiMetalForge;
import infinitealloys.client.RendererMetalForge;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy implements IGuiHandler, IPacketHandler {

	public void initBlocks() {
		InfiniteAlloys.ore = new BlockOre(InfiniteAlloys.oreID, 0).setBlockName("IAOre");
		InfiniteAlloys.machine = new BlockMachine(InfiniteAlloys.machineID, 6).setBlockName("IAMachine");
		GameRegistry.registerBlock(InfiniteAlloys.ore, ItemBlockIA.class);
		GameRegistry.registerBlock(InfiniteAlloys.machine, ItemBlockIA.class);
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 0), "Copper Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 1), "Tin Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 2), "Zinc Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 3), "Aluminum Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 4), "Magnesium Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 5), "Titanium Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 6), "Awesome Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 1, 7), "Amazing Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "Computer");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "Metal Forge");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "Crafter");
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 0);
		InfiniteAlloys.alloyIngot = new ItemIngot(InfiniteAlloys.alloyIngotID, 10);
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 7);
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 0), "Copper Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 1), "Tin Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 2), "Aluminum Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 3), "Magnesium Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 4), "Zinc Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 5), "Titanium Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 6), "Tantalum Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 1, 7), "Amazing Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.alloyIngot, 1, 0), "Alloy Ingot");
		LanguageRegistry.addName(InfiniteAlloys.upgrade, "Upgrade");
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TileEntityComputer.class, "Computer");
		GameRegistry.registerTileEntity(TileEntityMetalForge.class, "MetalForge");
	}

	public void initRendering() {
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 1:
				return new ContainerMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
				return new GuiComputer((TileEntityComputer)tileEntity);
			case 1:
				return new GuiMetalForge(player.inventory, (TileEntityMetalForge)tileEntity);
		}
		return null;
	}
	
	public World getClientWorld() {
		return null;
	}
	
	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		byte orientation = data.readByte();
		World world = InfiniteAlloys.proxy.getClientWorld();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			TileEntityMachine tem = (TileEntityMachine)te;
			tem.handlePacketData(orientation);
		}
	}

	public static Packet getPacket(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int x = tem.xCoord;
		int y = tem.yCoord;
		int z = tem.zCoord;
		byte orientation = (byte)tem.orientation;
		try {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			dos.writeByte(orientation);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "InfiniteAlloys";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
		return packet;
	}
}
