package infinitealloys.tile;

import infinitealloys.util.Consts;
import java.util.ArrayList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityPasture extends TileEntityMachine {

	/** The the mode value for turning the machine off */
	public static final int MODE_OFF = 0;
	/** The mode value for only trapping animals */
	public static final int MODE_ATTRACT = 1;
	/** The mode value for only repelling monsters */
	public static final int MODE_REPEL = 2;
	/** 0 is do nothing to the animal, 1 is attract the animal, 2 is repel the animal. Animals are in the order of chicken, cow, pig, sheep. */
	public byte[] animals = new byte[Consts.PASTURE_ANIMALS];
	/** 0 is do nothing to the monster, 1 is attract the monster, 2 is repel the monster. Monsters are in the order of creeper, skeleton, spider, zombie */
	public byte[] monsters = new byte[Consts.PASTURE_MONSTERS];
	/** The list of entities that are currently trapped */
	private ArrayList<EntityCreature> trapList = new ArrayList<EntityCreature>();
	private int maxSpots;
	private int range;

	public TileEntityPasture(int facing) {
		this();
		front = facing;
	}

	public TileEntityPasture() {
		super(0);
		inventoryStacks = new ItemStack[1];
		ticksToProcess = 0;
	}

	@Override
	public String getInvName() {
		return "Pasture";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return super.isItemValidForSlot(slot, itemstack) || TEHelper.stackValidForSlot(TEHelper.PASTURE, slot, itemstack);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(xCoord - range, yCoord - range, zCoord - range, xCoord + range + 1, yCoord + range + 1, zCoord + range + 1);

		if(animals[0] == 1)
			for(EntityChicken chicken : (ArrayList<EntityChicken>)worldObj.getEntitiesWithinAABB(EntityChicken.class, aabb))
				if(!trapList.contains(chicken))
					trapList.add(chicken);
		if(animals[1] == 1)
			for(EntityCow cow : (ArrayList<EntityCow>)worldObj.getEntitiesWithinAABB(EntityCow.class, aabb))
				if(!trapList.contains(cow))
					trapList.add(cow);
		if(animals[2] == 1)
			for(EntityPig pig : (ArrayList<EntityPig>)worldObj.getEntitiesWithinAABB(EntityPig.class, aabb))
				if(!trapList.contains(pig))
					trapList.add(pig);
		if(animals[3] == 1)
			for(EntitySheep sheep : (ArrayList<EntitySheep>)worldObj.getEntitiesWithinAABB(EntitySheep.class, aabb))
				if(!trapList.contains(sheep))
					trapList.add(sheep);

		if(monsters[0] == 1)
			for(EntityCreeper creeper : (ArrayList<EntityCreeper>)worldObj.getEntitiesWithinAABB(EntityCreeper.class, aabb))
				if(!trapList.contains(creeper))
					trapList.add(creeper);
		if(monsters[1] == 1)
			for(EntitySkeleton skeleton : (ArrayList<EntitySkeleton>)worldObj.getEntitiesWithinAABB(EntitySkeleton.class, aabb))
				if(!trapList.contains(skeleton))
					trapList.add(skeleton);
		if(monsters[2] == 1)
			for(EntitySpider spider : (ArrayList<EntitySpider>)worldObj.getEntitiesWithinAABB(EntitySpider.class, aabb))
				if(!trapList.contains(spider))
					trapList.add(spider);
		if(monsters[3] == 1)
			for(EntityZombie zombie : (ArrayList<EntityZombie>)worldObj.getEntitiesWithinAABB(EntityZombie.class, aabb))
				if(!trapList.contains(zombie))
					trapList.add(zombie);

		for(EntityCreature creature : trapList) {
			if(creature.posX * creature.posX + creature.posZ * creature.posZ >= range * range) { // Is the creature outside the edge of the pasture's range
				float yawRads = creature.rotationYaw * (float)Math.PI / 180F;
				if(Math.signum(xCoord - creature.posX) != Math.signum(Math.cos(yawRads))) // Is the creature moving away from the pasture in the x
					creature.rotationYaw = Math.signum(creature.rotationYaw) * (float)Math.PI - creature.rotationYaw; // Turn the creature around in the x
				if(Math.signum(zCoord - creature.posZ) != Math.signum(Math.sin(yawRads))) // Is the creature moving away from the pasture in the z
					creature.rotationYaw = -creature.rotationYaw; // Turn the creature around in the z direction
			}
		}
	}

	@Override
	public boolean shouldProcess() {
		return false;
	}

	@Override
	public void finishProcessing() {}

	public void handlePacketData(byte[] recipeAmts) {}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++)
			animals[i] = tagCompound.getByte("Animal" + i);
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++)
			monsters[i] = tagCompound.getByte("Monster" + i);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		for(int i = 0; i < Consts.PASTURE_ANIMALS; i++)
			tagCompound.setByte("Animal" + i, animals[i]);
		for(int i = 0; i < Consts.PASTURE_MONSTERS; i++)
			tagCompound.setByte("Monster" + i, monsters[i]);
	}

	@Override
	public int getJoulesUsed() {
		if(shouldProcess())
			return joulesUsedPerTick;
		return 0;
	}

	@Override
	protected void updateUpgrades() {
		if(hasUpgrade(TEHelper.EFFICIENCY2))
			joulesUsedPerTick = 1;
		else if(hasUpgrade(TEHelper.EFFICIENCY1))
			joulesUsedPerTick = 2;
		else
			joulesUsedPerTick = 4;

		if(hasUpgrade(TEHelper.CAPACITY2))
			maxSpots = 8;
		else if(hasUpgrade(TEHelper.CAPACITY1))
			maxSpots = 4;
		else
			maxSpots = 2;

		if(hasUpgrade(TEHelper.RANGE2))
			range = 15;
		else if(hasUpgrade(TEHelper.RANGE1))
			range = 10;
		else
			range = 5;

		canNetwork = hasUpgrade(TEHelper.WIRELESS);

		if(hasUpgrade(TEHelper.ELECCAPACITY2))
			setMaxEnergyStored(1000000);
		else if(hasUpgrade(TEHelper.ELECCAPACITY1))
			setMaxEnergyStored(750000);
		else
			setMaxEnergyStored(500000);
	}

	@Override
	protected void populateValidUpgrades() {
		validUpgrades.add(TEHelper.EFFICIENCY1);
		validUpgrades.add(TEHelper.EFFICIENCY2);
		validUpgrades.add(TEHelper.CAPACITY1);
		validUpgrades.add(TEHelper.CAPACITY2);
		validUpgrades.add(TEHelper.RANGE1);
		validUpgrades.add(TEHelper.RANGE2);
		validUpgrades.add(TEHelper.WIRELESS);
		validUpgrades.add(TEHelper.ELECCAPACITY1);
		validUpgrades.add(TEHelper.ELECCAPACITY2);
	}

	/** Does the pasture have enough space to enable another animal or monster
	 * 
	 * @return true if there is enough space to enable another animal or monster */
	public boolean hasFreeSpots() {
		int usedSpots = 0;
		for(byte animal : animals)
			if(animal > 0)
				usedSpots++;
		for(byte monster : monsters)
			if(monster > 0)
				usedSpots++;
		return usedSpots < maxSpots;
	}
}
