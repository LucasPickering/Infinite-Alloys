package infinitealloys.util;

public enum EnumAlloy {

	ALLOY1("alloy1", 11, 55),
	ALLOY2("alloy2", 1111, 4477),
	ALLOY3("alloy3", 11111, 556688),
	ALLOY4("alloy4", 111111, 557799),
	ALLOY5("alloy5", 11110000, 55550000),
	ALLOY6("alloy6", 44444444, 99999999);

	public final String name;
	public final int min;
	public final int max;

	private EnumAlloy(String name, int min, int max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}
}
