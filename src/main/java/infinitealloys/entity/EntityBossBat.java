package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public final class EntityBossBat extends EntityIABoss {

  public EntityBossBat(World world) {
    super(world, EnumBoss.BAT.alloy);
    setSize(2.5F, 3.6F);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300);
  }

  @Override
  public void moveEntityWithHeading(float strafe, float forward) {
    double d0;

    if (isInWater()) {
      d0 = posY;
      moveFlying(strafe, forward, isAIEnabled() ? 0.04F : 0.02F);
      moveEntity(motionX, motionY, motionZ);
      motionX *= 0.800000011920929D;
      motionY *= 0.800000011920929D;
      motionZ *= 0.800000011920929D;
      motionY -= 0.02D;

      if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY
                                                                      + 0.6000000238418579D
                                                                      - posY
                                                                      + d0,
                                                             motionZ)) {
        motionY = 0.30000001192092896D;
      }
    } else if (handleLavaMovement()) {
      d0 = posY;
      moveFlying(strafe, forward, 0.02F);
      moveEntity(motionX, motionY, motionZ);
      motionX *= 0.5D;
      motionY *= 0.5D;
      motionZ *= 0.5D;
      motionY -= 0.02D;

      if (isCollidedHorizontally
          && isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579D - posY + d0,
                                      motionZ)) {
        motionY = 0.30000001192092896D;
      }
    } else {
      float f2 = 0.91F;

      if (onGround) {
        f2 = worldObj.getBlock(MathHelper.floor_double(posX),
                               MathHelper.floor_double(boundingBox.minY) - 1,
                               MathHelper.floor_double(posZ)).slipperiness * 0.91F;
      }

      float f3 = 0.16277136F / (f2 * f2 * f2);
      float f4;

      if (onGround) {
        f4 = getAIMoveSpeed() * f3;
      } else {
        f4 = jumpMovementFactor;
      }

      moveFlying(strafe, forward, f4);
      f2 = 0.91F;

      if (onGround) {
        f2 = worldObj.getBlock(MathHelper.floor_double(posX),
                               MathHelper.floor_double(boundingBox.minY) - 1,
                               MathHelper.floor_double(posZ)).slipperiness * 0.91F;
      }

      moveEntity(motionX, motionY, motionZ);

      motionY *= 0.9800000190734863D;
      motionX *= (double) f2;
      motionZ *= (double) f2;
    }
  }

  @Override
  protected void updateAITasks() {
    super.updateAITasks();

    double xFactor;
    double yFactor;
    double zFactor;
    if (isWithinHomeDistanceCurrentPosition()) {
      xFactor = (rand.nextDouble() - 0.2D) * Math.signum(motionX);
      yFactor = (rand.nextDouble() - 0.5D) * Math.signum(motionY);
      zFactor = (rand.nextDouble() - 0.2D) * Math.signum(motionZ);
    } else {
      xFactor = (getHomePosition().posX - posX);
      yFactor = (getHomePosition().posY - posY);
      zFactor = (getHomePosition().posZ - posZ);
    }

    motionX += Math.signum(xFactor) * 0.05D;
    motionY += Math.signum(yFactor) * 0.05D + 0.01D;
    motionZ += Math.signum(zFactor) * 0.05D;

    float header = (float) (Math.atan2(motionZ, motionX) * 180D / Math.PI) - rotationYaw - 90F;
    rotationYaw += MathHelper.wrapAngleTo180_float(header);
    moveForward = 0.5F;
  }

  @Override
  protected boolean canTriggerWalking() {
    return false;
  }

  @Override
  protected void fall(float p_70069_1_) {
  }

  @Override
  protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {
  }

  @Override
  public boolean doesEntityNotTriggerPressurePlate() {
    return true;
  }
}
