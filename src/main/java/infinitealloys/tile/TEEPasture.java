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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import io.netty.buffer.ByteBuf;

public final class TEEPasture extends TileEntityElectric {

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
  public static final Class[] mobClasses =
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

  @SuppressWarnings("unchecked")
  @Override
  protected void onFinishProcess() {
                /* NOTE: For this specific machine, ticksToProcess = 0, meaning this function is called every tick. It is essentially an update() function with
                 * conditions applied in TileEntityMachine.update() */

    final ArrayList<EntityCreature> trapList = new ArrayList<EntityCreature>();
    final ArrayList<EntityCreature> repelList = new ArrayList<EntityCreature>();

    for (int i = 0; i < mobActions.length; i++) {
      if (mobActions[i] == 1) {
        trapList.addAll(((List<EntityCreature>) worldObj.getEntitiesWithinAABB(
            mobClasses[i], new AxisAlignedBB(
                pos.getX() - trapRange - 1, 0, pos.getZ() - trapRange - 1,
                pos.getX() + trapRange + 2, worldObj.getHeight(), pos.getZ() + trapRange + 2)))
                            .stream().collect(Collectors.toList()));
      } else if (mobActions[i] == 2) {
        repelList.addAll(((List<EntityCreature>) worldObj.getEntitiesWithinAABB(
            mobClasses[i], new AxisAlignedBB
                (pos.getX() - repelRange, 0, pos.getZ() - repelRange,
                 pos.getX() + repelRange + 1, worldObj.getHeight(), pos.getZ() + repelRange + 1)))
                             .stream().collect(Collectors.toList()));
      }
    }

    for (final EntityCreature creature : trapList) {
      // Is the creature too far away in the x direction
      if (Math.abs(pos.getX() - creature.posX) > trapRange + 1) {
        // Move it back to the edge of the radius
        creature.moveEntity(
            pos.getX() + Math.signum(creature.posX - pos.getX()) * trapRange - creature.posX, 0, 0);
      }

      // Is the creature too far away in the z direction
      if (Math.abs(pos.getZ() - creature.posZ) > trapRange + 1) {
        // Move it back to the edge of the radius
        creature.moveEntity(
            0, 0, pos.getZ() + Math.signum(creature.posZ - pos.getZ()) * trapRange - creature.posZ);
      }
    }

    for (final EntityCreature creature : repelList) {
      // Is the creature too close in the x direction
      if (Math.abs(pos.getX() - creature.posX) > repelRange) {
        // Move it back to the edge of the radius
        creature.moveEntity(
            creature.posX - pos.getX() - Math.signum(creature.posX - pos.getX()) * repelRange, 0, 0);
      }

      // Is the creature too close in the z direction
      if (Math.abs(pos.getZ() - creature.posZ) > repelRange) {
        // Move is back to the edge of the radius
        creature.moveEntity(
            0, 0, creature.posZ - pos.getZ() - Math.signum(creature.posZ - pos.getZ()) * repelRange);
      }
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
  public void readToClientData(ByteBuf bytes) {
    super.readToClientData(bytes);
    for (int i = 0; i < mobActions.length; i++) {
      mobActions[i] = bytes.readByte();
    }
  }

  @Override
  public void writeToClientData(ByteBuf bytes) {
    super.writeToClientData(bytes);
    for (byte mobAction : mobActions) {
      bytes.writeByte(mobAction);
    }
  }

  @Override
  public void readToServerData(ByteBuf bytes) {
    super.readToServerData(bytes);
    for (int i = 0; i < mobActions.length; i++) {
      mobActions[i] = bytes.readByte();
    }
  }

  @Override
  public void writeToServerData(ByteBuf bytes) {
    super.writeToServerData(bytes);
    for (byte mobAction : mobActions) {
      bytes.writeByte(mobAction);
    }
  }

  @Override
  protected void updateUpgrades() {
    float[] efficiencyUpgradeValues = {1F, 0.83F, 0.67F, 0.5F};
    rkPerTickMult = efficiencyUpgradeValues[getUpgradeTier(EnumUpgrade.EFFICIENCY)];

    int[] capacityUpgradeValues = {2, 4, 6, 8};
    maxSpots = capacityUpgradeValues[getUpgradeTier(EnumUpgrade.CAPACITY)];

    int[] trapRangeUpgradeValues = {5, 10, 15, 20};
    trapRange = trapRangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];
    int[] repelRangeUpgradeValues = {8, 16, 24, 32};
    repelRange = repelRangeUpgradeValues[getUpgradeTier(EnumUpgrade.RANGE)];
  }

  @Override
  protected void populateValidUpgrades() {
    addValidUpgradeType(EnumUpgrade.EFFICIENCY);
    addValidUpgradeType(EnumUpgrade.CAPACITY);
    addValidUpgradeType(EnumUpgrade.RANGE);
    addValidUpgradeType(EnumUpgrade.WIRELESS);
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
