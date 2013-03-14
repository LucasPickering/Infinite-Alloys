package infinitealloys.item;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.util.Funcs;
import infinitealloys.util.Consts;
import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id) {
		super(id);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "IAalloyIngot";
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void func_94581_a(IconRegister iconRegister) {
		iconIndex = iconRegister.func_94245_a("IAingot");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		float[] metalMasses = new float[Consts.METAL_COUNT];
		float totalMass = 0;
		int alloy;
		int damage = itemstack.getItemDamage() - 1;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(damage >= 0 && damage < Consts.VALID_ALLOY_COUNT)
			alloy = InfiniteAlloys.instance.worldData.getValidAlloys()[damage];
		else
			return;
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			metalMasses[i] = Funcs.intAtPos(Consts.alloyRadix, Consts.METAL_COUNT, alloy, i);
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			float percentage = Math.round(metalMasses[i] / totalMass * 10000F) / 100F;
			if(percentage != 0)
				list.add(percentage + "% " + Funcs.getLoc("metal." + Consts.metalNames[Consts.METAL_COUNT - 1 - i] + ".name"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		int colorCount = 0;
		int redTot = 0, blueTot = 0, greenTot = 0;
		int alloy = 0;
		if(itemstack.hasTagCompound())
			alloy = itemstack.getTagCompound().getInteger("alloy");
		else if(itemstack.getItemDamage() > 0)
			alloy = InfiniteAlloys.instance.worldData.getValidAlloys()[itemstack.getItemDamage() - 1];
		for(int i = 0; i < Consts.METAL_COUNT; i++) {
			for(int j = 0; j < Funcs.intAtPos(Consts.alloyRadix, Consts.METAL_COUNT, alloy, i); j++) {
				int ingotColor = Consts.metalColors[Consts.METAL_COUNT - 1 - i];
				colorCount++;
				redTot += ingotColor >> 16 & 255;
				greenTot += ingotColor >> 8 & 255;
				blueTot += ingotColor & 255;
			}
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
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
	}
}
