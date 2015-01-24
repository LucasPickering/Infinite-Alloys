package infinitealloys.util;

import net.minecraft.util.EnumFacing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FuncsTest {

  @Test
  public void testYawToFacing_South() throws Exception {
    assertEquals(EnumFacing.SOUTH, Funcs.yawToFacing(-45F));
    assertEquals(EnumFacing.SOUTH, Funcs.yawToFacing(0F));
    assertEquals(EnumFacing.SOUTH, Funcs.yawToFacing(44.9F));
  }

  @Test
  public void testYawToFacing_West() throws Exception {
    assertEquals(EnumFacing.WEST, Funcs.yawToFacing(45F));
    assertEquals(EnumFacing.WEST, Funcs.yawToFacing(90F));
    assertEquals(EnumFacing.WEST, Funcs.yawToFacing(134.9F));
  }

  @Test
  public void testYawToFacing_North() throws Exception {
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(135F));
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(180F));
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(-180F));
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(-135.1F));
  }

  @Test
  public void testYawToFacing_East() throws Exception {
    assertEquals(EnumFacing.EAST, Funcs.yawToFacing(-135F));
    assertEquals(EnumFacing.EAST, Funcs.yawToFacing(-90F));
    assertEquals(EnumFacing.EAST, Funcs.yawToFacing(-45.1F));
  }
}