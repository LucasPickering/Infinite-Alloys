package infinitealloys.tile;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import infinitealloys.item.ItemIA;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;

public class TEEPasture extends TileEntityElectric {

  /**
   * The the mode value for turning the machine off
   */
  public static final int MODE_OFF = 0;
  /**
   * The mode value for only trapping animals
   */
  public static final int MODE_ATTRACT = 1;
  /**
   * The mode value for only repelling monsters
   */
  public static final int MODE_REPEL = 2;
  /**
   * Classes for each animal/monster that is compatible with this machine
   */
  public static final Class[]
      mobClasses =
      {EntityChicken.class, EntityCow.class, EntityCow.class, EntitySheep.class,
       EntityCreeper.class, EntitySkeleton.class, EntitySpider.class, EntityZombie.class};

  /**
   * 0 is do nothing to the mob, 1 is attract the mob, 2 is repel the mob. The order can be seen in
   * {@link #mobClasses mobClasses}
   */
  public byte[] mobActions = new byte[mobClasses.length];
  private int maxSpots;
  private int trapRange;
  private int repelRange;

  public TEEPasture() {
    super(1);
    baseRKPerTick = -4;
    ticksToProcess = 0;
  }

  @Override
  public EnumMachine getEnumMachine() {
    return EnumMachine.PASTURE;
  }

  @Override
  public boolean shouldProcess() {
    return true;
  }

  @Override
  protected void onFinishProcess() {
                /* NOTE: For this specific machine, ticksToProcess = 0, meaning this function is called every tick. It is essentially an updateEntity() function with
                 * conditions applied in TileEntityMachine.updateEntity() */

    ArrayList<EntityCreature> trapList = new ArrayList<EntityCreature>();
    ArrayList<EntityCreature> repelList = new ArrayList<EntityCreature>();

    for (int i = 0; i < mobActions.length; i++) {
      if (mobActions[i] == 1) {
        for (EntityCreature creature : (ArrayList<EntityCreature>) worldObj
            .getEntitiesWithinAABB(mobClasses[i],
                                   AxisAlignedBB.getBoundingBox(xCoord - trapRange - 1, 0,
                                                                zCoord - trapRange - 1,
                                                                xCoord + trapRange + 2,
                                                                worldObj.getHeight(),
                                                                zCoord + trapRange + 2))) {
          trapList.add(creature);
        }
      } else if (mobActions[i] == 2) {
        for (EntityCreature creature : (ArrayList<EntityCreature>) worldObj
            .getEntitiesWithinAABB(mobClasses[i],
                                   AxisAlignedBB
                                       .getBoundingBox(xCoord - repelRange, 0, zCoord - repelRange,
                                                       xCoord + repelRange + 1,
                                                       worldObj.getHeight(),
                                                       zCoord + repelRange + 1))) {
          repelList.add(creature);
        }
      }
    }

    for (final EntityCreature creature : trapList) {
      if (Math.abs(xCoord - creature.posX)
          > trapRange + 1) // Is the creature too far away in the x direction
      {
        creature
            .moveEntity(xCoord + Math.signum(creature.posX - xCoord) * trapRange - creature.posX, 0,
                        0); // Move it back to the edge of the radius
      }
      // in the x direction
      if (Math.abs(zCoord - creature.posZ)
          > trapRange + 1) // Is the creature too far away in the z direction
      {
        creature.moveEntity(0, 0, zCoord + Math.signum(creature.posZ - zCoord) * trapRange
                                  - creature.posZ); // Move is back to the edge of the radius
      }
      // in the z direction
    }

    for (final EntityCreature creature : repelList) {
      if (Math.abs(xCoord - creature.posX)
          > repelRange) // Is the creature too close in the x direction
      {
        creature
            .moveEntity(creature.posX - xCoord - Math.signum(creature.posX - xCoord) * repelRange,
                        0, 0); // Move it back to the edge of the radius
      }
      // in the x direction
      if (Math.abs(zCoord - creature.posZ)
          > repelRange) // Is the creature too close in the z direction
      {
        creature.moveEntity(0, 0, creature.posZ - zCoord - Math.signum(creature.posZ - zCoord)
                                                           * repelRange); // Move is back to the edge of the radius
      }
      // in the z direction
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    for (int i = 0; i < mobActions.length; i++) {
      mobActions[i] = tagCompound.getByte("Mob" + i);
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    for (int i = 0; i < mobActions.length; i++) {
      tagCompound.setByte("Mob" + i, mobActions[i]);
    }
  }

  @Override
  public Object[] getSyncDataToClient() {
    return ArrayUtils.addAll(super.getSyncDataToClient(), mobActions);
  }

  @Override
  public Object[] getSyncDataToServer() {
    return new Object[]{mobActions};
  }

  public void handleTEPPacketData(byte[] mobActions) {
    this.mobActions = mobActions;
  }

  @Override
  protected void updateUpgrades() {
    float[] efficiencyUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(Consts.EFFICIENCY)];

    int[] capacityUpgradeValues = {2, 4, 6, 8};
    stackLimit = capacityUpgradeValues[getUpgradeTier(Consts.CAPACITY)];

    int[] trapRangeUpgradeValues = {5, 10, 15, 20};
    trapRange = trapRangeUpgradeValues[getUpgradeTier(Consts.RANGE)];
    int[] repelRangeUpgradeValues = {8, 16, 24, 32};
    repelRange = repelRangeUpgradeValues[getUpgradeTier(Consts.RANGE)];
  }

  @Override
  protected void populateValidUpgrades() {
    validUpgradeTypes.add(ItemIA.upgrades[Consts.EFFICIENCY]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.CAPACITY]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.RANGE]);
    validUpgradeTypes.add(ItemIA.upgrades[Consts.WIRELESS]);
  }

  /**
   * Does the pasture have enough space to enable another animal or monster
   *
   * @return true if there is enough space to enable another animal or monster
   */
  public boolean hasFreeSpots() {
    int usedSpots = 0;
    for (final byte mob : mobActions) {
      if (mob > 0) {
        usedSpots++;
      }
    }
    return usedSpots < maxSpots;
  }
}
