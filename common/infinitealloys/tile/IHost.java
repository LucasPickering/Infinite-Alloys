package infinitealloys.tile;

import java.util.ArrayList;
import java.util.List;
import infinitealloys.util.MachineHelper;
import infinitealloys.util.Point;
import net.minecraft.entity.player.EntityPlayer;

/** Machines that can host networks between other machines, e.g. the computer */
public interface IHost {

	public boolean addMachine(EntityPlayer player, int machineX, int machineY, int machineZ);
}
