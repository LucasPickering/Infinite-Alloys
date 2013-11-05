package infinitealloys.tile;

public enum EnumTEUpgradable {

	COMPUTER("Computer", TEUComputer.class),
	METAL_FORGE("Metal Forge", TEMMetalForge.class),
	ANALYZER("Analyzer", TEMAnalyzer.class),
	PRINTER("Printer", TEMPrinter.class),
	XRAY("X-ray", TEMXray.class),
	PASTURE("Pasture", TEMPasture.class),
	RK_STORAGE("RK Storage Unit", TEUEnergyStorage.class),
	GENERATOR("Generator", TEMGenerator.class);

	private final String name;
	private final Class<? extends TileEntityUpgradable> teuClass;

	private EnumTEUpgradable(String name, Class<? extends TileEntityUpgradable> teuClass) {
		this.name = name;
		this.teuClass = teuClass;
	}

	public String getName() {
		return name;
	}

	public Class<? extends TileEntityUpgradable> getTeuClass() {
		return teuClass;
	}
}
