package infinitealloys.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBossBat extends EntityIABoss {

	public EntityBossBat(World world) {
		super(world, 4);
		setSize(2F, 3.6F);
	}

	public void onUpdate() {
		super.onUpdate();

		this.motionY *= 0.6000000238418579D;
	}

	protected void updateAITasks() {
		super.updateAITasks();

/*		this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * 0.10000000149011612D;
		this.motionY += (Math.signum(d1) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
		this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * 0.10000000149011612D;
		float f = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
		float f1 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
		this.moveForward = 0.5F;
		this.rotationYaw += f1;*/
	}
}
