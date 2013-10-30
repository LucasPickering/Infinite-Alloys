package infinitealloys.tile;

import infinitealloys.block.BlockMachine;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityMachine extends TileEntityUpgradable {

	/** Amount of RK this machine produces/consumes per tick while working. Positive is producing RK and negative is consuming RK */
	protected int rkPerTick;

	/** Amount of ticks it takes for this machine to finish one of its processes */
	public int ticksToProcess = 200;

	/** Amount of ticks this machine has been running its process for, when this reaches ticksToFinish it is done */
	public int processProgress;

	public TileEntityMachine(int upgradeSlotIndex) {
		this();
		this.upgradeSlotIndex = upgradeSlotIndex;
	}

	public TileEntityMachine() {
		populateValidUpgrades();
		updateUpgrades();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		// If the conditions are correct so that the process can continue, increment the progress by one. If it has reached or exceeded the limit for
		// completion, then finish the process and reset the counter.
		if(shouldProcess() && ++processProgress >= ticksToProcess) {
			processProgress = 0;
			finishProcessing();
			onInventoryChanged();
		}
	}

	public void handlePacketDataFromServer(int processProgress) {
		this.processProgress = processProgress;
	}

	/** Should the process tick be increased? Called every tick to determine if power should be used and if progress should continue. */
	protected abstract boolean shouldProcess();

	/** Called when processProgress reaches ticksToProgress */
	protected abstract void finishProcessing();

	/** Actual amount of RK change per tick, after certain calculations and conditions. Positive is produced RK and negative is consumed RK. */
	public int getRKChange() {
		if(shouldProcess())
			return rkPerTick;
		return 0;
	}

	/** Is there a power storage unit to support this machine? There must be a PSU in range with space if this generates power and one with sufficient energy if
	 * this consumes power.
	 * 
	 * @return True if the PSU can support this machine and False if it cannot */
	public boolean hasGoodCircuit() {
		return false; // TODO: Integrate this with the power storage to look at available power
	}

	@SideOnly(Side.CLIENT)
	public int getProcessProgressScaled(int scale) {
		return processProgress * scale / ticksToProcess;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		processProgress = tagCompound.getInteger("ProcessProgress");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("ProcessProgress", processProgress);
	}
}
