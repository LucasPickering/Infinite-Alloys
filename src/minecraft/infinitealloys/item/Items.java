package infinitealloys.item;

import infinitealloys.util.References;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class Items {

	public static int multiID;
	public static int ingotID;
	public static int alloyIngotID;
	public static int upgradeID;
	public static int gpsID;
	public static int alloyBookID;

	public static Item multi;
	public static Item ingot;
	public static Item alloyIngot;
	public static Item upgrade;
	public static Item gps;
	public static Item alloyBook;

	public static Icon[] multiIcons = new Icon[References.MULTI_ITEM_COUNT];
	public static Icon[] upgradeIcons = new Icon[References.UPGRADE_COUNT];
}
