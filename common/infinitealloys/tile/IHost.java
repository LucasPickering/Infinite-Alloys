package infinitealloys.tile;

import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;

/** Machines that can host networks between other machines, e.g. the computer */
public interface IHost {

	public boolean addClient(EntityPlayer player, Point client);

	public boolean isClientValid(Point p);
}
