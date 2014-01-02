package infinitealloys.network;

import infinitealloys.item.ItemInternetWand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;

public class PacketWand implements PacketIA {

	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		boolean adding = data.readBoolean();
		ItemStack heldItem = player.getHeldItem();
		if(heldItem.getItem() instanceof ItemInternetWand) {
			if(adding) {
				int x = data.readInt();
				short y = data.readShort();
				int z = data.readInt();
				((ItemInternetWand)heldItem.getItem()).addMachine(player.worldObj, heldItem, x, y, z);
			}
			else {
				byte id = data.readByte();
				((ItemInternetWand)heldItem.getItem()).removeMachine(heldItem, id);
			}
		}
	}

	public static Packet250CustomPayload getPacketAdd(int x, short y, int z) {
		return PacketHandler.getPacket(PacketHandler.WAND, true, x, y, z);
	}

	public static Packet250CustomPayload getPacketRemove(byte index) {
		return PacketHandler.getPacket(PacketHandler.WAND, false, index);
	}
}
