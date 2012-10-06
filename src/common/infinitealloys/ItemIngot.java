package infinitealloys;

import java.util.List;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemIngot extends ItemIA {

	private String[] metalNames = { "Iron", "Copper", "Tin", "Zinc", "Aluminum", "Magnesium", "Titanium", "Awesome", "Amazing" };

	public ItemIngot(int id, int texture) {
		super(id, texture);
		setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return "IA Ingot " + itemstack.itemID + "x" + itemstack.getItemDamage();
	}

	@Override
	public int getIconFromDamage(int i) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, List list) {
		if(itemstack.itemID != InfiniteAlloys.alloyIngot.shiftedIndex) return;
		float[] metalMasses = new float[InfiniteAlloys.metalCount];
		float totalMass = 0;
		for(int i = 0; i < InfiniteAlloys.metalCount; i++) {
			metalMasses[i] = InfiniteAlloys.intAtPositionOctal(itemstack.getItemDamage(), i) * InfiniteAlloys.densities[i];
			totalMass += metalMasses[i];
		}
		for(int i = 0; i < InfiniteAlloys.metalCount; i++) {
			int percentage = (int)(metalMasses[i] / totalMass * 100F);
			if(percentage != 0)
				list.add(percentage + "% " + metalNames[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativetabs, List list) {
		if(id == InfiniteAlloys.ingot.shiftedIndex)
			for(int i = 0; i < InfiniteAlloys.oreCount; i++)
				list.add(new ItemStack(id, 1, i));
	}
}
