package infinitealloys.tile;

import org.apache.commons.lang3.ArrayUtils;
import infinitealloys.util.EnumBoss;
import infinitealloys.util.Funcs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEIASummoner extends TileEntityIA {

	private int storedXP;

	@Override
	public void updateEntity() {
		System.out.println(Funcs.getSideAsString() + ": " + storedXP);
	}

	/** Take a level of XP away from the given player and add it to this machine. This DOES NOT assume that the player has enough XP already.
	 * Exactly one level is always taken from the player, but the amount of experience added varies by how many levels the player has because the higher levels require more experience. */
	public void addLevel(EntityPlayer player) {
		if(player.capabilities.isCreativeMode)
			storedXP += 100;
		else {
			player.addExperienceLevel(-1);
			storedXP += player.xpBarCap();
		}
	}

	/** Spawn an entity for the given boss type. */
	public void spawnBoss(EnumBoss boss) {
		if(!worldObj.isRemote && boss != null) {
			try {
				EntityLiving entityliving = (EntityLiving)boss.entityClass.getConstructor().newInstance();
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
	public EnumBoss[] getUnlockedBosses() {
		EnumBoss[] bosses = EnumBoss.values();
		for(int i = 0; i < bosses.length; i++)
			if(storedXP < bosses[i].unlockXP)
				return (EnumBoss[])Arrays.copyOfRange(bosses, 0, i);
		return bosses;
	}

	/** Get the amount of XP accumulated since the last level was reached.
	 * 
	 * @return e.g. if the last boss needed 60 XP total and storedXP is currently 75, return 25 */
	public int getXPTowardsNextLevel() {
		EnumBoss[] bosses = getUnlockedBosses();
		if(bosses.length > 0)
			return storedXP - EnumBoss.values()[bosses.length - 1].unlockXP;
		return storedXP;
	}

	/** Get the amount of XP needed to unlock the next boss, from how much XP is stored now.
	 * 
	 * @return e.g. if the next boss needs 80 XP total, and storedXP is currently 75, return 5 */
	public int getXPNeededForNextLevel() {
		EnumBoss[] bosses = getUnlockedBosses();
		if(bosses.length < EnumBoss.values().length)
			return EnumBoss.values()[bosses.length].unlockXP - storedXP;
		return 0;
	}

	/** Get the difference between the total XP required for the last level and the total XP required for this level.
	 * 
	 * @return e.g. if the last boss needed 60 XP total and the next on needs 80 XP total, return 20 */
	public int getXPIntervalForNextLevel() {
		return getXPTowardsNextLevel() + getXPNeededForNextLevel();
	}

	@Override
	public Object[] getSyncDataToClient() {
		return ArrayUtils.addAll(super.getSyncDataToClient(), new Object[] { storedXP });
	}

	@Override
	public Object[] getSyncDataToServer() {
		return new Object[] { storedXP };
	}

	/** Sync packet data from client or server */
	public void handlePacketData(int storedXP) {
		this.storedXP = storedXP;
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
