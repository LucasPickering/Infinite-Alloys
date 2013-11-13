package infinitealloys.tile;

public enum EnumTEUpgradable {

	COMPUTER("computer", TEUComputer.class),
	METAL_FORGE("metalforge", TEMMetalForge.class),
	ANALYZER("analyzer", TEMAnalyzer.class),
	PRINTER("printer", TEMPrinter.class),
	XRAY("xray", TEMXray.class),
	PASTURE("pasture", TEMPasture.class),
	RK_STORAGE("rkstorage", TEUEnergyStorage.class),
	GENERATOR("generator", TEMGenerator.class);

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
