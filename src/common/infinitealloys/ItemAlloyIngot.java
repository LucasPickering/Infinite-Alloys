package infinitealloys;

import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.ItemStack;

public class ItemAlloyIngot extends ItemIA {

	public ItemAlloyIngot(int id, int texture) {
		super(id, texture);
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		return "IA Alloy Ingot";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, List list) {
		float[] metalMasses = new float[IAValues.metalCount];
		float totalMass = 0;
		for(int i = 0; i < IAValues.metalCount; i++) {
			metalMasses[i] = InfiniteAlloys.intAtPositionRadix(4, IAValues.metalCount, itemstack.getItemDamage() + IAValues.alloyDamageOffset, i);
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < IAValues.metalCount; i++) {
			float percentage = Math.round(metalMasses[i] / totalMass * 10000F) / 100F;
			if(percentage != 0)
				list.add(percentage + "% " + IAValues.metalNames[IAValues.metalCount - 1 - i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int damage, int renderPass) {
		ArrayList<Integer> redVals = new ArrayList<Integer>();
		ArrayList<Integer> greenVals = new ArrayList<Integer>();
		ArrayList<Integer> blueVals = new ArrayList<Integer>();
		for(int i = 0; i < IAValues.metalCount; i++) {
			for(int j = 0; j < InfiniteAlloys.intAtPositionRadix(4, IAValues.metalCount, damage + IAValues.alloyDamageOffset, i); j++) {
				String ingotColor = InfiniteAlloys.addLeadingZeros(Integer.toHexString(IAValues.metalColors[IAValues.metalCount - 1 - i]), 6);
				redVals.add(Integer.parseInt(ingotColor.substring(0, 2), 16));
				greenVals.add(Integer.parseInt(ingotColor.substring(2, 4), 16));
				blueVals.add(Integer.parseInt(ingotColor.substring(4), 16));
			}
		}
		int redAvg = 0, greenAvg = 0, blueAvg = 0;
		if(!redVals.isEmpty()) {
			for(int red : redVals)
				redAvg += red;
			redAvg /= redVals.size();
		}
		if(!greenVals.isEmpty()) {
			for(int green : greenVals)
				greenAvg += green;
			greenAvg /= greenVals.size();
		}
		if(!blueVals.isEmpty()) {
			for(int blue : blueVals)
				blueAvg += blue;
			blueAvg /= blueVals.size();
		}
		return Integer.parseInt(Integer.toHexString(redAvg) + Integer.toHexString(greenAvg) + Integer.toHexString(blueAvg), 16);
	}
}
