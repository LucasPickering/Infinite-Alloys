package infinitealloys.util;

/** A 3D coordinate */
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
		x = p2.x;
		y = p2.y;
		z = p2.z;
	}

	public boolean equals(int x2, int y2, int z2) {
		return x == x2 && y == y2 && z == z2;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Point)
			return equals(((Point)o).x, ((Point)o).y, ((Point)o).z);
		return false;
	}

	public double distanceTo(int x2, int y2, int z2) {
		return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2) + (z - z2) * (z - z2));
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}