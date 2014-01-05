package infinitealloys.network;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import com.google.common.io.ByteArrayDataInput;

public class PacketWorldData implements PacketIA {

	@Override
	public void execute(EntityPlayer player, ByteArrayDataInput data) {
		final int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];
		for(int i = 0; i < validAlloys.length; i++)
			validAlloys[i] = data.readInt();
		InfiniteAlloys.instance.worldData = new WorldData(validAlloys);
	}

	public static Packet250CustomPayload getPacket() {
		return PacketHandler.getPacket(PacketHandler.WORLD_DATA, Funcs.getValidAlloys());
	}
}
