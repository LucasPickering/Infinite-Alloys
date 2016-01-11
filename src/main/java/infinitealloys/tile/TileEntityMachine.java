package infinitealloys.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import infinitealloys.block.IABlocks;
import infinitealloys.item.IAItems;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.network.MessageTEToClient;
import infinitealloys.network.MessageTEToServer;
import infinitealloys.network.NetworkHandler;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;
import io.netty.buffer.ByteBuf;

/**
 * A base class for Tile Entities that can receive upgrades. TileEntityElectric block are a sub-type
 * of this. Often referred to as TEMs or machines.
 *
 * @see TileEntityElectric
 */
public abstract class TileEntityMachine extends TileEntity implements IUpdatePlayerListBox,
                                                                      IInventory {

  /**
   * The stacks that make up the inventory of this TE
   */
  public ItemStack[] inventoryStacks;

  /**
   * A list of the upgrades that can be used on this machine
   */
  private final List<ItemUpgrade> validUpgradeItems = new LinkedList<>();

  /**
   * Each element in the array corresponds to an upgrade type, and represents how many tiers in the
   * type have been unlocked
   */
  private int[] upgrades = new int[Consts.UPGRADE_TYPE_COUNT];

  /**
   * The index of the slot that upgrades are placed in
   */
  public int upgradeSlotIndex = 0;

  /**
   * The size limit for one stack in this machine
   */
  protected int stackLimit = 64;

  /**
   * The coordinates of the computer that is controlling this machine
   */
  public BlockPos computerHost;

  /**
   * False until {@link #update} has been called for the first time.
   */
  private boolean initialized = false;

  /**
   * @param inventoryLength The amount of total slots in the inventory
   */
  public TileEntityMachine(int inventoryLength) {
    this();
    inventoryStacks = new ItemStack[inventoryLength];
    upgradeSlotIndex = inventoryLength - 1;
  }

  public TileEntityMachine() {
    populateValidUpgrades();
  }

  /**
   * Get the integer from {@link infinitealloys.util.MachineHelper MachineHelper} that corresponds to
   * this machine
   */
  public abstract EnumMachine getEnumMachine();

  /**
   * Called when the block is first placed to restore persistent data from before it was destroyed,
   * such as the stored RK in the ESU
   */
  public void loadNBTData(NBTTagCompound tagCompound) {
  }

  @Override
  public void update() {
    if (!initialized) {
      initialized = true;
      onFirstTick();
    }

    // Check for upgrades in the upgrade inventory slot.
    // If one is there, remove it from the slot and add it to the machine.
    if (inventoryStacks[upgradeSlotIndex] != null
        && isUpgradeValid(inventoryStacks[upgradeSlotIndex])) {
      // Increment the element in the upgrades array that corresponds to
      upgrades[((ItemUpgrade) inventoryStacks[upgradeSlotIndex].getItem()).upgradeType.ordinal()]++;
      inventoryStacks[upgradeSlotIndex] = null;
      updateUpgrades();
    }
  }

  /**
   * Called on the first tick of {@link #update}, then never again.
   */
  protected void onFirstTick() {
    updateUpgrades();
  }

  public void connectToComputerNetwork(BlockPos host) {
    if (computerHost != null) {
      ((TEMComputer) worldObj.getTileEntity(computerHost)).removeClient(pos, false);
    }
    computerHost = host;
  }

  public void disconnectFromComputerNetwork() {
    computerHost = null;
    worldObj.markBlockForUpdate(pos);
  }

  /**
   * An NBTTagCompound to be attached to the ItemStack that is dropped when the machine is destroyed.
   * This can have data such as energy stored in the ESU.
   */
  protected NBTTagCompound getDropTagCompound() {
    return null;
  }

  /**
   * Called when the TE's block is destroyed. Ends network connections and drops items and upgrades
   */
  public void onBlockDestroyed() {
    // Save this machine's data to an NBT Tag
    ItemStack block = new ItemStack(IABlocks.machine, 1, getEnumMachine().ordinal());
    NBTTagCompound tagCompound = getDropTagCompound();
    if (tagCompound != null) {
      block.setTagCompound(tagCompound);
    }
    spawnItem(block); // Drop block for this machine with the saved data on it

    InventoryHelper.dropInventoryItems(worldObj, pos, this); // Drop inventory items

    // Drop upgrades
    for (int i = 0; i < upgrades.length; i++) {
      for (int j = 0; j < upgrades[i]; j++) {
        spawnItem(new ItemStack(IAItems.upgrades[i], 1, j));
      }
    }
    Arrays.fill(upgrades, 0);

    if (computerHost != null) {
      ((IHost) worldObj.getTileEntity(computerHost)).removeClient(pos, true);
    }
  }

  /**
   * Spawn an EntityItem for an ItemStack
   */
  private void spawnItem(ItemStack itemstack) {
    Random random = new Random();
    float f = random.nextFloat() * 0.8F + 0.1F;
    float f1 = random.nextFloat() * 0.8F + 0.1F;
    float f2 = random.nextFloat() * 0.8F + 0.1F;
    EntityItem item = new EntityItem(worldObj, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2,
                                     itemstack);
    item.motionX = random.nextGaussian() * 0.05F;
    item.motionY = random.nextGaussian() * 0.25F;
    item.motionZ = random.nextGaussian() * 0.05F;
    worldObj.spawnEntityInWorld(item);
  }

  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    upgrades = tagCompound.getIntArray("upgrades");
    NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);
    for (int i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttag = nbttaglist.getCompoundTagAt(i);
      byte slot = nbttag.getByte("Slot");
      if (slot >= 0 && slot < inventoryStacks.length) {
        inventoryStacks[slot] = ItemStack.loadItemStackFromNBT(nbttag);
      }
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tagCompound) {
    super.writeToNBT(tagCompound);
    tagCompound.setIntArray("upgrades", upgrades);
    NBTTagList nbttaglist = new NBTTagList();
    for (int i = 0; i < inventoryStacks.length; i++) {
      if (inventoryStacks[i] != null) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Slot", (byte) i);
        inventoryStacks[i].writeToNBT(nbt);
        nbttaglist.appendTag(nbt);
      }
    }
    tagCompound.setTag("Items", nbttaglist);
  }

  /**
   * Send a packet to the server to sync this machine's data. Should only be called client-side.
   *
   * @throws IllegalStateException if this is called server-side
   */
  public void syncToServer() {
    if (!worldObj.isRemote) {
      throw new IllegalStateException("can only sync to server while client-side");
    }
    Funcs.sendPacketToServer(new MessageTEToServer(this));
  }

  @Override
  public Packet getDescriptionPacket() {
    return NetworkHandler.simpleNetworkWrapper.getPacketFrom(new MessageTEToClient(this));
  }

  /**
   * Read data from a server->client packet and sync certain values
   *
   * @param bytes the data from the server->client packet
   */
  public void readToClientData(ByteBuf bytes) {
    for (int i = 0; i < upgrades.length; i++) {
      upgrades[i] = bytes.readInt();
    }
  }

  /**
   * Write the data that this machine will send to clients to the given {@link
   * io.netty.buffer.ByteBuf}.
   *
   * @param bytes the {@link io.netty.buffer.ByteBuf} that will be written to
   */
  public void writeToClientData(ByteBuf bytes) {
    Funcs.writeBlockPosToByteBuf(bytes, pos);
    for (int upgrade : upgrades) {
      bytes.writeInt(upgrade);
    }
  }

  /**
   * Read data from a client->server packet and sync certain values
   *
   * @param bytes the data from the client->server packet
   */
  public void readToServerData(ByteBuf bytes) {
  }

  /**
   * Write the data that this machine will send to the server to the given {@link
   * io.netty.buffer.ByteBuf}.
   *
   * @param bytes the {@link io.netty.buffer.ByteBuf} that will be written to
   */
  public void writeToServerData(ByteBuf bytes) {
    Funcs.writeBlockPosToByteBuf(bytes, pos);
  }

  /**
   * Called from {@link infinitealloys.block.BlockMachine#onNeighborChange} when an adjacent
   * TileEntity changes
   *
   * @param pos the position of the block that changed
   */
  public void onNeighborChange(BlockPos pos) {
  }

  /**
   * Called when the inventory of this machine changes.
   *
   * @param slotIndex the index of the slot that changed
   */
  public void onInventoryChanged(int slotIndex) {
  }

  // Upgrade methods

  protected abstract void updateUpgrades();

  protected abstract void populateValidUpgrades();

  /**
   * Determines if the given ItemStack is a valid upgrade for the machine. Criteria: Does this machine
   * take this type of upgrade? Does this machine already have this upgrade? Does this upgrade have a
   * prerequisite upgrade and if so, does this machine already have that upgrade?
   *
   * @param stack for upgrade item with a binary upgrade damage value (see {@link
   *              infinitealloys.util.MachineHelper TEHelper} for upgrade numbers)
   * @return true if valid
   */
  public final boolean isUpgradeValid(ItemStack stack) {
    if (stack.getItem() instanceof ItemUpgrade && validUpgradeItems.contains(stack.getItem())) {
      EnumUpgrade upgradeType = ((ItemUpgrade) stack.getItem()).upgradeType;
      int upgradeTier = stack.getItemDamage() + 1;
      return upgrades[upgradeType.ordinal()] + 1 == upgradeTier
             && !hasUpgrade(upgradeType, upgradeTier);
    }
    return false;
  }

  /**
   * Does the machine have the specified type and tier of upgrade
   *
   * @param upgradeType Int representing type of upgrade, see {@link infinitealloys.util.Consts}
   * @param tier        Tier of the upgrade, e.g. 2 for Speed II or 3 for Capacity III
   * @return true if the machine has the upgrade
   */
  public boolean hasUpgrade(EnumUpgrade upgradeType, int tier) {
    return upgrades[upgradeType.ordinal()] >= tier;
  }

  /**
   * Get the tier of the specified upgrade type for this machine, e.g. if this machine has received
   * Speed I and II and the upgrade type is speed, this will return 2
   */
  public int getUpgradeTier(EnumUpgrade upgradeType) {
    return upgrades[upgradeType.ordinal()];
  }

  /**
   * Add an upgrade type to this machine's list of valid upgrade types.
   *
   * @param upgradeType the upgrade type
   */
  protected void addValidUpgradeType(EnumUpgrade upgradeType) {
    validUpgradeItems.add(IAItems.upgrades[upgradeType.ordinal()]);
  }

  // IWorldNameable methods

  @Override
  public String getName() {
    return getEnumMachine().name;
  }

  @Override
  public boolean hasCustomName() {
    return true;
  }

  @Override
  public IChatComponent getDisplayName() {
    return new ChatComponentText(getName());
  }

  //IInventory methods

  @Override
  public int getSizeInventory() {
    return inventoryStacks.length;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return inventoryStacks[slot];
  }

  @Override
  public ItemStack decrStackSize(int slot, int amt) {
    if (inventoryStacks[slot] != null) {
      ItemStack stack;
      if (inventoryStacks[slot].stackSize <= amt) {
        stack = inventoryStacks[slot];
        inventoryStacks[slot] = null;
        onInventoryChanged(slot);
        return stack;
      }
      stack = inventoryStacks[slot].splitStack(amt);
      if (inventoryStacks[slot].stackSize == 0) {
        inventoryStacks[slot] = null;
      }
      onInventoryChanged(slot);
      return stack;
    }
    return null;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int slot) {
    if (inventoryStacks[slot] != null) {
      final ItemStack stack = inventoryStacks[slot];
      inventoryStacks[slot] = null;
      return stack;
    }
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    inventoryStacks[slot] = stack;
    if (stack != null && stack.stackSize > getInventoryStackLimit()) {
      stack.stackSize = getInventoryStackLimit();
    }
    onInventoryChanged(slot);
  }

  @Override
  public int getInventoryStackLimit() {
    return stackLimit;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public void openInventory(EntityPlayer player) {
  }

  @Override
  public void closeInventory(EntityPlayer player) {
  }

  @Override
  public final boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    return slot == upgradeSlotIndex && isUpgradeValid(itemstack)
           || slot < upgradeSlotIndex && getEnumMachine().stackValidForSlot(slot, itemstack);
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  public void clear() {
    for (int i = 0; i < getSizeInventory(); i++) {
      inventoryStacks[i] = null;
    }
  }
}
