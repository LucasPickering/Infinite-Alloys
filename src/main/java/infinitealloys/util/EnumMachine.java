package infinitealloys.util;

import infinitealloys.client.EnumHelp;
import infinitealloys.client.gui.*;
import infinitealloys.client.model.block.*;
import infinitealloys.client.render.TileEntityMachineRenderer;
import infinitealloys.inventory.*;
import infinitealloys.tile.*;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumMachine {

	COMPUTER("Computer", TEMComputer.class, ContainerComputer.class, GuiComputer.class, ModelComputer.class),
	METAL_FORGE("MetalForge", TEEMetalForge.class, ContainerMetalForge.class, GuiMetalForge.class, ModelMetalForge.class),
	XRAY("Xray", TEEXray.class, ContainerXray.class, GuiXray.class, ModelXray.class),
	PASTURE("Pasture", TEEPasture.class, ContainerPasture.class, GuiPasture.class, ModelPasture.class),
	ENERGY_STORAGE("EnergyStorage", TEEEnergyStorage.class, ContainerEnergyStorage.class, GuiEnergyStorage.class, ModelEnergyStorage.class, "currentRK");

	public final String name;
	public final Class temClass;
	public final Class containerClass;
	public final Class guiClass;
	public final Class modelClass;

	/** An array of the names of fields in the TE that should be saved when the block is destroyed and restored when it is placed back down, e.g. currentRK for the ESU. */
	public final String[] persistentFields;

	private EnumMachine(String name, Class<? extends TileEntityMachine> temClass, Class<? extends ContainerMachine> containerClass, Class<? extends GuiMachine> guiClass,
			Class<? extends ModelBase> modelClass, String... persistentFields) {
		this.name = name;
		this.temClass = temClass;
		this.containerClass = containerClass;
		this.guiClass = guiClass;
		this.modelClass = modelClass;
		this.persistentFields = persistentFields;
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
			return new TileEntityMachineRenderer(name, (ModelBase)modelClass.newInstance());
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
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
