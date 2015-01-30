package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import infinitealloys.util.EnumBoss;

public class EntityBossBat extends EntityIABoss {

  private ChunkCoordinates spawnPosition;

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
  public void onUpdate() {
    super.onUpdate();
    motionY *= 0.6000000238418579D;
  }

  @Override
  protected void updateAITasks() {
    super.updateAITasks();
    if (spawnPosition != null && (
        !worldObj.isAirBlock(spawnPosition.posX, spawnPosition.posY, spawnPosition.posZ)
        || spawnPosition.posY < 1)) {
      spawnPosition = null;
    }

    if (spawnPosition == null || rand.nextInt(30) == 0
        || spawnPosition.getDistanceSquared((int) posX, (int) posY, (int) posZ) < 4.0F) {
      spawnPosition = new ChunkCoordinates((int) posX + rand.nextInt(7) - rand.nextInt(7),
                                           (int) posY + rand.nextInt(6) - 2,
                                           (int) posZ + rand.nextInt(7) - rand.nextInt(7));
    }

    double d0 = spawnPosition.posX + 0.5D - posX;
    double d1 = spawnPosition.posY + 1D - posY;
    double d2 = spawnPosition.posZ + 0.5D - posZ;
    motionX += (Math.signum(d0) * 0.5D - motionX) * 0.10000000149011612D;
    motionY += (Math.signum(d1) * 0.699999988079071D - motionY) * 0.10000000149011612D;
    motionZ += (Math.signum(d2) * 0.5D - motionZ) * 0.10000000149011612D;
    float f = (float) (Math.atan2(motionZ, motionX) * 180.0D / Math.PI) - 90.0F;
    float f1 = MathHelper.wrapAngleTo180_float(f - rotationYaw);
    moveForward = 0.5F;
    rotationYaw += f1;
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
