package infinitealloys.network;

import infinitealloys.item.ItemInternetWand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketAddToWand implements PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		World world = player.worldObj;
		int x = data.readInt();
		int y = data.readShort();
		int z = data.readInt();
		ItemStack heldItem = player.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand)
			((ItemInternetWand)heldItem.getItem()).addMachineToWand(player.worldObj, heldItem, x, y, z);
	}

	public static Packet250CustomPayload getPacket(int x, short y, int z) {
		return PacketHandler.getPacket(PacketHandler.ADD_TO_WAND, x, y, z);
	}
}
