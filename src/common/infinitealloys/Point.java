package infinitealloys;

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

	public boolean equals(Point point2) {
		return x == point2.x && y == point2.y && z == point2.z;
	}

	public boolean equals(int x2, int y2, int z2) {
		return x == x2 && y == y2 && z == z2;
	}

	@Override
	public String toString() {
		return "Point " + x + ", " + y + ", " + z;
	}

	public double distanceTo(Point point2) {
		return distanceTo(point2.x, point2.y, point2.z);
	}

	public double distanceTo(int x2, int y2, int z2) {
		int distX = x - x2;
		int distY = y - y2;
		int distZ = z - z2;
		return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
	}
}
