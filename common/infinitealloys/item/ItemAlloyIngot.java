package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id) {
		super(id, "ingot");
		setCreativeTab(null);
		setHasSubtypes(true);
		setUnlocalizedName("IAalloyIngot");
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		// The each metal's content in the alloy. Each metal gets a number from 0 to Consts.ALLOY_RADIX to represent its content.
		final int[] metalContent = new int[Consts.METAL_COUNT];
		// The total amount of metal "pieces" in this
		int totalMass = 0;
		int alloy;
		final int damage = itemstack.getItemDamage() - 1;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(damage >= 0 && damage < Consts.VALID_ALLOY_COUNT)
			alloy = Funcs.getValidAlloys()[damage];
		else
			return;
		for(int i = 0; i < Consts.METAL_COUNT; i++)
			totalMass += metalContent[i] = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, i);
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			final float percentage = Math.round((float)metalContent[i] / (float)totalMass * 1000F) / 10F;
			if(percentage != 0)
				list.add(percentage + "% " + Funcs.getLoc("metal." + Consts.METAL_NAMES[i] + ".name"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		int colorCount = 0;
		int redTot = 0, greenTot = 0, blueTot = 0;
		int alloy = 0;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(itemstack.getItemDamage() > 0 && itemstack.getItemDamage() < Consts.VALID_ALLOY_COUNT)
			alloy = Funcs.getValidAlloys()[itemstack.getItemDamage() - 1];
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			final int ingotColor = Consts.metalColors[i];
			final int alloyAmt = Funcs.intAtPos(alloy, Consts.ALLOY_RADIX, i);
			colorCount += alloyAmt;
			redTot += (ingotColor >> 16 & 255) * alloyAmt; // Get the red byte from the ingot's hex color code
			greenTot += (ingotColor >> 8 & 255) * alloyAmt; // Get the green byte from the ingot's hex color code
			blueTot += (ingotColor & 255) * alloyAmt; // Get the blue byte from the ingot's hex color code
		}
		int redAvg = 0, greenAvg = 0, blueAvg = 0;
		if(colorCount != 0) {
			redAvg = redTot / colorCount;
			greenAvg = greenTot / colorCount;
			blueAvg = blueTot / colorCount;
		}
		return (redAvg << 16) + (greenAvg << 8) + blueAvg;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName();
	}
}
