package infinitealloys.client;

import infinitealloys.util.Consts;
import infinitealloys.util.MachineHelper;

public enum EnumHelp {

	/* CP - Computer, MF - Metal Forge, AZ - Analyzer, XR - X-ray, PS - Pasture, ES - Energy Storage, IW - Internet Wand */

	CP_UPGRADE("upgrade", 0x9c00ff, 139, 42, 18, 18), CP_TAB("cpTab", 0xff8900, -24, 6, 27, 24),

	MF_UPGRADE("upgrade", 0x9c00ff, 147, 7, 18, 18), MF_PROGRESS("progress", 0x00ff16, 30, 13, 110, 20), MF_ENERGY("energy", 0xfff600, 8, 14, 18, 18),
	MF_OUTPUT("mfOutput", 0x0000ff, 143, 47, 26, 26), MF_SUPPLY("mfSupply", 0xff0000, 6, 80, 164, 38), MF_PRESETS("mfPresets", 0xff00ff, 39, 51, 18, 18),
	MF_SELECTION("mfSelection", 0x00ffff, 64, 41, 74, 38),

	AZ_UPGRADE("upgrade", 0x9c00ff, 150, 7, 18, 18), AZ_PROGRESS("progress", 0x00ff16, 27, 6, 110, 20), AZ_ENERGY("energy", 0xfff600, 6, 7, 18, 18),
	AZ_SUPPLY("azSupply", 0xff0000, 15, 56, 146, 20), AZ_INGOTS("azIngots", 0x00ffff, 3, 27, 158, 28),

	XR_UPGRADE("upgrade", 0x9c00ff, 167, 5, 18, 18), XR_PROGRESS("progress", 0x00ff16, 53, 4, 110, 20), XR_ENERGY("energy", 0xfff600, 8, 5, 18, 18),
	XR_ORE("xrOre", 0xff0000, 31, 5, 18, 18), XR_SEARCH("xrSearch", 0x00ffff, 68, 30, 80, 20), XR_RESULTS("xrResults", 0xff00ff, 7, 50, 160, 102),

	PS_UPGRADE("upgrade", 0x9c00ff, 140, 43, 18, 18), PS_ENERGY("energy", 0xfff600, 17, 39, 18, 18), PS_CREATURES("psCreatures", 0x00ffff, 42, 4, 74, 88),

	ES_UPGRADE("upgrade", 0x9c00ff, 184, 39, 18, 18), ES_PROGRESS("progress", 0x00ff16, 69, 38, 110, 20), ES_ENERGY("energy", 0xfff600, 30, 3, 18, 18),
	ES_SUPPLY("esSupply", 0xff0000, 11, 20, 56, 56),

	IW_ADD_TO_WAND("iwAddToWand", 0xff0000, 6, 6, 70, 20), IW_ADD_SELECTED("iwAddSelected", 0x00ff00, 79, 6, 70, 20),
	IW_LIST("iwList", 0x9c00ff, 6, 30, 125, 209), IW_REMOVE("iwRemove", 0x00ffff, 133, 30, 16, 209);

	/** Name used to get the title and info from localization */
	public final String name;
	/** Hexadecimal color of outline box */
	public final int color;
	/** X coord of outline box */
	public final int x;
	/** Y coord of outline box */
	public final int y;
	/** Width of outline box */
	public final int w;
	/** Height of outline box */
	public final int h;

	private EnumHelp(String name, int color, int x, int y, int w, int h) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static EnumHelp[] getBoxes(int id) {
		switch(id) {
			case MachineHelper.COMPUTER:
				return new EnumHelp[] { CP_UPGRADE, CP_TAB };
			case MachineHelper.METAL_FORGE:
				return new EnumHelp[] { MF_UPGRADE, MF_PROGRESS, MF_ENERGY, MF_OUTPUT, MF_SUPPLY, MF_PRESETS, MF_SELECTION };
			case MachineHelper.ANALYZER:
				return new EnumHelp[] { AZ_UPGRADE, AZ_PROGRESS, AZ_ENERGY, AZ_SUPPLY, AZ_INGOTS };
			case MachineHelper.XRAY:
				return new EnumHelp[] { XR_UPGRADE, XR_PROGRESS, XR_ENERGY, XR_ORE, XR_SEARCH, XR_RESULTS };
			case MachineHelper.PASTURE:
				return new EnumHelp[] { PS_UPGRADE, PS_ENERGY, PS_CREATURES };
			case MachineHelper.ENERGY_STORAGE:
				return new EnumHelp[] { ES_UPGRADE, ES_PROGRESS, ES_ENERGY, ES_SUPPLY };
			case Consts.MACHINE_COUNT:
				return new EnumHelp[] { IW_ADD_SELECTED, IW_ADD_TO_WAND, IW_LIST, IW_REMOVE };
		}
		return null;
	}
}
