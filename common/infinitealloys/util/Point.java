package infinitealloys.util;

public class Point {

	public int x, y, z;

	public Point() {}

	public Point(int x, int y, int z) {
		set(x, y, z);
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Point p2) {
		this.x = p2.x;
		this.y = p2.y;
		this.z = p2.z;
	}

	public boolean equals(int x2, int y2, int z2) {
		return x == x2 && y == y2 && z == z2;
	}

	public boolean equals(Point p2) {
		return equals(p2.x, p2.y, p2.z);
	}

	public double distanceTo(int x2, int y2, int z2) {
		return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2) + (z - z2) * (z - z2));
	}

	public double distanceTo(Point p2) {
		return Math.sqrt((x - p2.x) * (x - p2.x) + (y - p2.y) * (y - p2.y) + (z - p2.z) * (z - p2.z));
	}

	@Override
	public String toString() {
		return "Point " + x + ", " + y + ", " + z;
	}
}