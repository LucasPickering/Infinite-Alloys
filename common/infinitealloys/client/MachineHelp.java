package infinitealloys.client;

import infinitealloys.tile.TileEntityAnalyzer;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.tile.TileEntityMetalForge;
import infinitealloys.tile.TileEntityPrinter;
import infinitealloys.tile.TileEntityXray;

public enum MachineHelp {

	/*
	 * CP - Computer MF - Metal Forge AZ - Analyzer PR - Printer XR - X-ray */

	ENERGY("energy", 0xff8900, 12, 6, 12, 34),
	CP_UPGRADE("upgrade", 0x9c00ff, 139, 42, 18, 18), CP_TAB("cpTab", 0xff8900, -24, 6, 27, 24), CP_ADD("cpAdd", 0x00ff16, 6, 5, 158, 20),
	MF_UPGRADE("upgrade", 0x9c00ff, 147, 7, 18, 18), MF_PROGRESS("progress", 0x00ff16, 30, 13, 110, 20),
	MF_BOOK("mfBook", 0xffff00, 7, 51, 18, 18), MF_OUTPUT("mfOutput", 0x0000ff, 143, 47, 26, 26), MF_SUPPLY("mfSupply", 0xff0000, 7, 81, 162, 36),
	MF_PRESETS("mfPresets", 0xff00ff, 39, 51, 18, 18), MF_INGOTS("mfIngots", 0x00ffff, 64, 40, 74, 38),
	AZ_UPGRADE("upgrade", 0x9c00ff, 171, 7, 18, 18), AZ_PROGRESS("progress", 0x00ff16, 53, 56, 110, 20), AZ_BOOK("azBook", 0xffff00, 171, 32, 18, 18),
	AZ_INPUT("azInput", 0xff0000, 27, 57, 18, 18), AZ_OUTPUT("azOutput", 0x0000ff, 171, 57, 18, 18), AZ_INGOTS("azIngots", 0x00ffff, 26, 9, 144, 36),
	PR_UPGRADE("upgrade", 0x9c00ff, 147, 5, 18, 18), PR_PROGRESS("progress", 0x00ff16, 30, 13, 110, 20),
	PR_INPUT("prInput", 0xff0000, 11, 43, 18, 18), PR_SUPPLY("prSupply", 0x0000ff, 79, 43, 18, 18), PR_OUTPUT("prOutput", 0xffff00, 147, 43, 18, 18),
	XR_UPGRADE("upgrade", 0x9c00ff, 167, 5, 18, 18), XR_PROGRESS("progress", 0x00ff16, 53, 4, 110, 20), XR_ORE("xrOre", 0xff0000, 31, 5, 18, 18),
	XR_SEARCH("xrSearch", 0x00ffff, 68, 27, 80, 20), XR_RESULTS("xrResults", 0xff00ff, 7, 48, 160, 102);

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

	private MachineHelp(String name, int color, int x, int y, int w, int h) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static MachineHelp[] getBoxesForTEM(TileEntityMachine tem) {
		if(tem instanceof TileEntityComputer)
			return new MachineHelp[] { CP_UPGRADE, CP_TAB, CP_ADD };
		if(tem instanceof TileEntityMetalForge)
			return new MachineHelp[] { ENERGY, MF_UPGRADE, MF_PROGRESS, MF_BOOK, MF_OUTPUT, MF_SUPPLY, MF_PRESETS, MF_INGOTS };
		if(tem instanceof TileEntityAnalyzer)
			return new MachineHelp[] { ENERGY, AZ_UPGRADE, AZ_PROGRESS, AZ_BOOK, AZ_INPUT, AZ_OUTPUT, AZ_INGOTS };
		if(tem instanceof TileEntityPrinter)
			return new MachineHelp[] { ENERGY, PR_UPGRADE, PR_PROGRESS, PR_INPUT, PR_SUPPLY, PR_OUTPUT };
		if(tem instanceof TileEntityXray)
			return new MachineHelp[] { ENERGY, XR_UPGRADE, XR_PROGRESS, XR_ORE, XR_SEARCH, XR_RESULTS };
		return null;
	}
}
