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
		for(int i = 0; i < IAValues.oreCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ore, 0, i), IAValues.metalNames[i + 1] + " Ore");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "Computer");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "Metal Forge");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "Crafter");
	}

	public void initItems() {
		InfiniteAlloys.ingot = new ItemIngot(InfiniteAlloys.ingotID, 0).setItemName("IAIngot");
		InfiniteAlloys.alloyIngot = new ItemAlloyIngot(InfiniteAlloys.alloyIngotID, 0).setItemName("IAAlloyIngot");
		InfiniteAlloys.upgrade = new ItemUpgrade(InfiniteAlloys.upgradeID, 1).setItemName("IAUpgrade");
		for(int i = 0; i < IAValues.oreCount; i++)
			LanguageRegistry.addName(new ItemStack(InfiniteAlloys.ingot, 0, i), IAValues.metalNames[i + 1] + " Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.alloyIngot), "Alloy Ingot");
		LanguageRegistry.addName(new ItemStack(InfiniteAlloys.upgrade), "Upgrade");
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
		World world = InfiniteAlloys.proxy.getClientWorld();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityMachine) {
			byte networkID = data.readByte();
			byte orientation = data.readByte();
			((TileEntityMachine)te).handlePacketData(orientation, networkID);
			if(te instanceof TileEntityMetalForge) {
				int currentFuelBurnTime = data.readShort();
				int heatLeft = data.readShort();
				int smeltProgress = data.readShort();
				byte[] recipeAmts = new byte[IAValues.metalCount];
				for(int i = 0; i < recipeAmts.length; i++)
					recipeAmts[i] = data.readByte();
				((TileEntityMetalForge)te).handlePacketData(currentFuelBurnTime, heatLeft, smeltProgress, recipeAmts);
			}
		}
	}

	public static Packet getPacket(TileEntityMachine tem) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(tem.xCoord);
			dos.writeInt(tem.yCoord);
			dos.writeInt(tem.zCoord);
			dos.writeByte(tem.networkID);
			dos.writeByte(tem.orientation);
			if(tem instanceof TileEntityMetalForge) {
				TileEntityMetalForge temf = (TileEntityMetalForge)tem;
				dos.writeShort(temf.currentFuelBurnTime);
				dos.writeShort(temf.heatLeft);
				dos.writeShort(temf.smeltProgress);
				for(byte amt : temf.recipeAmts)
					dos.writeByte(amt);
			}
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
