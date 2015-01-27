package infinitealloys.util;

/**
 * A 3D coordinate
 */
public class Point3 {

  public int x, y, z;

  /**
   * Create an "invalid" point. The negative y, which is impossible in the world, will indicate that
   * this point is essentially null
   */
  public Point3() {
    y = -1;
  }

  public Point3(int x, int y, int z) {
    set(x, y, z);
  }

  public void set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(Point3 p2) {
    x = p2.x;
    y = p2.y;
    z = p2.z;
  }

  public boolean equals(int x2, int y2, int z2) {
    return x == x2 && y == y2 && z == z2;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Point3) {
      return equals(((Point3) o).x, ((Point3) o).y, ((Point3) o).z);
    }
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