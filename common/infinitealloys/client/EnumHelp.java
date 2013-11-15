package infinitealloys.client;

import infinitealloys.tile.TEEAnalyzer;
import infinitealloys.tile.TEEGenerator;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.tile.TEEPasture;
import infinitealloys.tile.TEEPrinter;
import infinitealloys.tile.TEEXray;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TEMEnergyStorage;
import infinitealloys.tile.TileEntityMachine;

public enum EnumHelp {

	/* CP - Computer, MF - Metal Forge, AZ - Analyzer, PR - Printer, XR - X-ray, PS - Pasture, ES - Energy Storage, GE - Generator */

	CP_UPGRADE("upgrade", 0x9c00ff, 139, 42, 18, 18), CP_TAB("cpTab", 0xff8900, -24, 6, 27, 24), CP_ADD("cpAdd", 0x00ff16, 6, 5, 158, 20),

	MF_UPGRADE("upgrade", 0x9c00ff, 147, 7, 18, 18), MF_PROGRESS("progress", 0x00ff16, 30, 13, 110, 20),
	MF_BOOK("mfBook", 0xffff00, 7, 51, 18, 18), MF_OUTPUT("mfOutput", 0x0000ff, 143, 47, 26, 26), MF_SUPPLY("mfSupply", 0xff0000, 7, 81, 162, 36),
	MF_PRESETS("mfPresets", 0xff00ff, 39, 51, 18, 18), MF_SELECTION("mfSelection", 0x00ffff, 64, 41, 74, 38),

	AZ_UPGRADE("upgrade", 0x9c00ff, 171, 7, 18, 18), AZ_PROGRESS("progress", 0x00ff16, 53, 56, 110, 20), AZ_BOOK("azBook", 0xffff00, 171, 32, 18, 18),
	AZ_INPUT("azInput", 0xff0000, 27, 57, 18, 18), AZ_OUTPUT("azOutput", 0x0000ff, 171, 57, 18, 18), AZ_INGOTS("azIngots", 0x00ffff, 26, 9, 144, 36),

	PR_UPGRADE("upgrade", 0x9c00ff, 147, 5, 18, 18), PR_PROGRESS("progress", 0x00ff16, 30, 13, 110, 20),
	PR_INPUT("prInput", 0xff0000, 11, 43, 18, 18), PR_SUPPLY("prSupply", 0x0000ff, 79, 43, 18, 18), PR_OUTPUT("prOutput", 0xffff00, 147, 43, 18, 18),

	XR_UPGRADE("upgrade", 0x9c00ff, 167, 5, 18, 18), XR_PROGRESS("progress", 0x00ff16, 53, 4, 110, 20), XR_ORE("xrOre", 0xff0000, 31, 5, 18, 18),
	XR_SEARCH("xrSearch", 0x00ffff, 68, 27, 80, 20), XR_RESULTS("xrResults", 0xff00ff, 7, 48, 160, 102),

	PS_UPGRADE("upgrade", 0x9c00ff, 140, 43, 18, 18), PS_CREATURES("psCreatures", 0x00ffff, 42, 4, 74, 88),

	ES_UPGRADE("upgrade", 0x9c00ff, 139, 42, 18, 18),

	GE_UPGRADE("upgrade", 0x9c00ff, 184, 39, 18, 18), GE_PROGRESS("progress", 0x00ff16, 70, 38, 110, 20), GE_SUPPLY("geSupply", 0xff0000, 11, 20, 56, 56);

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

	public static EnumHelp[] getBoxesForTEM(TileEntityMachine tem) {
		if(tem instanceof TEMComputer)
			return new EnumHelp[] { CP_UPGRADE, CP_TAB, CP_ADD };
		if(tem instanceof TEEMetalForge)
			return new EnumHelp[] { MF_UPGRADE, MF_PROGRESS, MF_BOOK, MF_OUTPUT, MF_SUPPLY, MF_PRESETS, MF_SELECTION };
		if(tem instanceof TEEAnalyzer)
			return new EnumHelp[] { AZ_UPGRADE, AZ_PROGRESS, AZ_BOOK, AZ_INPUT, AZ_OUTPUT, AZ_INGOTS };
		if(tem instanceof TEEPrinter)
			return new EnumHelp[] { PR_UPGRADE, PR_PROGRESS, PR_INPUT, PR_SUPPLY, PR_OUTPUT };
		if(tem instanceof TEEXray)
			return new EnumHelp[] { XR_UPGRADE, XR_PROGRESS, XR_ORE, XR_SEARCH, XR_RESULTS };
		if(tem instanceof TEEPasture)
			return new EnumHelp[] { PS_UPGRADE, PS_CREATURES };
		if(tem instanceof TEMEnergyStorage)
			return new EnumHelp[] { ES_UPGRADE };
		if(tem instanceof TEEGenerator)
			return new EnumHelp[] { GE_UPGRADE, GE_PROGRESS, GE_SUPPLY };
		return null;
	}
}
