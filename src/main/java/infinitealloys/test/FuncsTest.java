package infinitealloys.test;

import net.minecraft.util.EnumFacing;

import org.junit.Test;

import infinitealloys.util.Funcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class FuncsTest {

  @Test
  public void testIntAtPos() throws Exception {
    assertEquals(0, Funcs.intAtPos(0, 10, 0));
    assertEquals(3, Funcs.intAtPos(1234, 10, 1));
    assertEquals(15, Funcs.intAtPos(0xf0f, 16, 2));
    assertEquals(1, Funcs.intAtPos(0b100, 2, 2));
    assertEquals(0, Funcs.intAtPos(111, 10, 5));
    assertEquals(-5, Funcs.intAtPos(-512, 10, 2));
  }

  @Test
  public void testMouseInZone() throws Exception {
    assertTrue(Funcs.mouseInZone(10, 5, 10, 5, 20, 20));
    assertTrue(Funcs.mouseInZone(30, 25, 10, 5, 20, 20));
    assertTrue(Funcs.mouseInZone(15, 15, 10, 5, 20, 20));
    assertFalse(Funcs.mouseInZone(10, 10, 11, 11, 10, 10));
  }

  @Test
  public void testReduceAlloy() throws Exception {
    assertEquals(0, Funcs.reduceAlloy(0));
    assertEquals(1, Funcs.reduceAlloy(1));
    assertEquals(1122, Funcs.reduceAlloy(1122));
    assertEquals(1122, Funcs.reduceAlloy(2244));
    assertEquals(33121, Funcs.reduceAlloy(99363));
  }

  @Test
  public void testAbbreviateNum() throws Exception {
    assertEquals("0", Funcs.abbreviateNum(0));
    assertEquals("11", Funcs.abbreviateNum(11));
    assertEquals("999", Funcs.abbreviateNum(999));

    assertEquals("1.00K", Funcs.abbreviateNum(1000));
    assertEquals("15.8K", Funcs.abbreviateNum(15759));
    assertEquals("999K", Funcs.abbreviateNum(999000));

    assertEquals("1.00M", Funcs.abbreviateNum(1000000));
    assertEquals("15.8M", Funcs.abbreviateNum(15750000));
    assertEquals("999M", Funcs.abbreviateNum(999000000));

    assertEquals("1.00B", Funcs.abbreviateNum(1000000000));
    assertEquals("2.15B", Funcs.abbreviateNum(Integer.MAX_VALUE));
  }

  @Test
  public void testYawToFacing() throws Exception {
    assertEquals(EnumFacing.SOUTH, Funcs.yawToFacing(-45F));
    assertEquals(EnumFacing.SOUTH, Funcs.yawToFacing(0F));

    assertEquals(EnumFacing.WEST, Funcs.yawToFacing(45F));
    assertEquals(EnumFacing.WEST, Funcs.yawToFacing(90F));

    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(135F));
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(180F));
    assertEquals(EnumFacing.NORTH, Funcs.yawToFacing(-180F));

    assertEquals(EnumFacing.EAST, Funcs.yawToFacing(-135F));
    assertEquals(EnumFacing.EAST, Funcs.yawToFacing(-90F));
  }

  @Test
  public void testFacingToYaw() throws Exception {
    assertEquals(0, Funcs.facingToYaw(EnumFacing.SOUTH));
    assertEquals(90, Funcs.facingToYaw(EnumFacing.WEST));
    assertEquals(180, Funcs.facingToYaw(EnumFacing.NORTH));
    assertEquals(-90, Funcs.facingToYaw(EnumFacing.EAST));
  }
}