package infinitealloys.util;

import infinitealloys.client.EnumHelp;
import infinitealloys.client.block.TileEntityComputerRenderer;
import infinitealloys.client.block.TileEntityEnergyStorageRenderer;
import infinitealloys.client.block.TileEntityMachineRenderer;
import infinitealloys.client.block.TileEntityMetalForgeRenderer;
import infinitealloys.client.block.TileEntityPastureRenderer;
import infinitealloys.client.block.TileEntityXrayRenderer;
import infinitealloys.client.gui.GuiComputer;
import infinitealloys.client.gui.GuiEnergyStorage;
import infinitealloys.client.gui.GuiMachine;
import infinitealloys.client.gui.GuiMetalForge;
import infinitealloys.client.gui.GuiPasture;
import infinitealloys.client.gui.GuiXray;
import infinitealloys.inventory.ContainerComputer;
import infinitealloys.inventory.ContainerEnergyStorage;
import infinitealloys.inventory.ContainerMachine;
import infinitealloys.inventory.ContainerMetalForge;
import infinitealloys.inventory.ContainerPasture;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TEEEnergyStorage;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumMachine {

	COMPUTER("Computer", TEMComputer.class, ContainerComputer.class, GuiComputer.class, TileEntityComputerRenderer.class),
	METAL_FORGE("MetalForge", TEEMetalForge.class, ContainerMetalForge.class, GuiMetalForge.class, TileEntityMetalForgeRenderer.class),
	XRAY("Xray", TEEXray.class, ContainerXray.class, GuiXray.class, TileEntityXrayRenderer.class),
	PASTURE("Pasture", TEEPasture.class, ContainerPasture.class, GuiPasture.class, TileEntityPastureRenderer.class),
	ENERGY_STORAGE("EnergyStorage", TEEEnergyStorage.class, ContainerEnergyStorage.class, GuiEnergyStorage.class, TileEntityEnergyStorageRenderer.class, "currentRK");

	private String name;
	private Class temClass;
	private Class containerClass;
	private Class guiClass;
	private Class temrClass;

	/** An array of the names of fields in the TE that should be saved when the block is destroyed and restored when it is placed back down, e.g. currentRK for the ESU. */
	private String[] persistentFields;

	private EnumMachine(String name, Class<? extends TileEntityMachine> temClass, Class<? extends ContainerMachine> containerClass, Class<? extends GuiMachine> guiClass,
			Class<? extends TileEntityMachineRenderer> temrClass, String... persistentFields) {
		this.name = name;
		this.temClass = temClass;
		this.containerClass = containerClass;
		this.guiClass = guiClass;
		this.temrClass = temrClass;
		this.persistentFields = persistentFields;
	}

	public String getName() {
		return name;
	}

	public Class getTEMClass() {
		return temClass;
	}

	public ContainerMachine getContainer(InventoryPlayer inventoryPlayer, TileEntityMachine tem) {
		try {
			return (ContainerMachine)containerClass.getConstructor(InventoryPlayer.class, TileEntityMachine.class).newInstance(inventoryPlayer, tem);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public GuiMachine getGui(InventoryPlayer inventoryPlayer, TileEntityMachine tem) {
		try {
			return (GuiMachine)guiClass.getConstructor(InventoryPlayer.class, temClass).newInstance(inventoryPlayer, tem);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public TileEntityMachineRenderer getTEMR() {
		try {
			return (TileEntityMachineRenderer)temrClass.getConstructor().newInstance();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getPersistentFields() {
		return persistentFields;
	}

	public boolean stackValidForSlot(int index, ItemStack itemstack) {
		switch(this) { // Switch first based on the type of machine, then based on the index of the slot
			case METAL_FORGE:
				switch(index) {
					case 0:
						return false;
					default:
						return MachineHelper.getIngotNum(itemstack) != -1;
				}
			case XRAY:
				return MachineHelper.isDetectable(itemstack);
			case ENERGY_STORAGE:
				return TileEntityFurnace.getItemBurnTime(itemstack) > 0;
			default:
				return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumHelp[] getHelpBoxes() {
		switch(this) {
			case COMPUTER:
				return new EnumHelp[] { EnumHelp.CP_UPGRADE, EnumHelp.CP_TAB };
			case METAL_FORGE:
				return new EnumHelp[] { EnumHelp.MF_UPGRADE, EnumHelp.MF_PROGRESS, EnumHelp.MF_ENERGY, EnumHelp.MF_OUTPUT, EnumHelp.MF_SUPPLY, EnumHelp.MF_PRESETS, EnumHelp.MF_SELECTION };
			case XRAY:
				return new EnumHelp[] { EnumHelp.XR_UPGRADE, EnumHelp.XR_PROGRESS, EnumHelp.XR_ENERGY, EnumHelp.XR_ORE, EnumHelp.XR_SEARCH, EnumHelp.XR_RESULTS };
			case PASTURE:
				return new EnumHelp[] { EnumHelp.PS_UPGRADE, EnumHelp.PS_ENERGY, EnumHelp.PS_CREATURES };
			case ENERGY_STORAGE:
				return new EnumHelp[] { EnumHelp.ES_UPGRADE, EnumHelp.ES_PROGRESS, EnumHelp.ES_ENERGY, EnumHelp.ES_SUPPLY, EnumHelp.ES_RK };
			default:
				return null;
		}
	}
}
