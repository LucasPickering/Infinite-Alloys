package infinitealloys.util;

import io.netty.buffer.ByteBuf;

/**
 * A 3D coordinate
 */
public class Point3 {

  /**
   * Create a new {@code Point3} instance from the next three values in the given {@link ByteBuf}
   *
   * @param bytes the {@link ByteBuf} to be read from
   * @return a new {@code Point3} with the values from {@code bytes}
   */
  public static Point3 readFromByteBuf(ByteBuf bytes) {
    return new Point3(bytes.readInt(), bytes.readInt(), bytes.readInt());
  }

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

  /**
   * Write this {@code Point3}'s data to the given {@link io.netty.buffer.ByteBuf}
   *
   * @param bytes the {@link io.netty.buffer.ByteBuf} to be written to
   */
  public void writeToByteBuf(ByteBuf bytes) {
    bytes.writeInt(x);
    bytes.writeInt(y);
    bytes.writeInt(z);
  }
}