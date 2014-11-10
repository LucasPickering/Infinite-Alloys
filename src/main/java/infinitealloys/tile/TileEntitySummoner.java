package infinitealloys.tile;

import infinitealloys.util.EnumBoss;
import java.util.ArrayList;
import scala.actors.threadpool.Arrays;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySummoner extends TileEntity {

	private int storedXP;

	/** Take a level of XP away from the given player and add it to this machine. This DOES NOT assume that the player has enough XP already.
	 * Exactly one level is always taken from the player, but the amount of experience added varies by how many levels the player has because the higher levels require more experience. */
	public void addLevel(EntityPlayer player) {
		player.addExperienceLevel(-1);
		storedXP += player.xpBarCap();
	}

	/** Spawn an entity for the given boss type. */
	public void spawnBoss(EnumBoss boss) {
		if(!worldObj.isRemote && boss != null) {
			EntityLiving entityliving;
			try {
				entityliving = (EntityLiving)boss.entityClass.getConstructor().newInstance();
				entityliving.setLocationAndAngles(xCoord, yCoord + 1, zCoord, 0F, 0F);
				entityliving.rotationYawHead = entityliving.rotationYaw;
				entityliving.renderYawOffset = entityliving.rotationYaw;
				worldObj.spawnEntityInWorld(entityliving);
				entityliving.playLivingSound();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** Get an array of the boss types that can be summoned by this Summoner. This list is based solely on {@link storedXP}. */
	public EnumBoss[] getAvailableBosses() {
		EnumBoss[] bosses = EnumBoss.values();
		for(int i = 0; i < bosses.length; i++)
			if(storedXP < bosses[i].unlockXP)
				return (EnumBoss[])Arrays.copyOfRange(bosses, 0, i);
		return bosses;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		storedXP = tagCompound.getInteger("storedXP");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("storedXP", storedXP);
	}
}
