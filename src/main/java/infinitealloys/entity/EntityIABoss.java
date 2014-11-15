package infinitealloys.entity;

import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class EntityIABoss extends EntityMob {

	private final EnumAlloy alloy;

	/** @param alloyID the alloy that is unlocked by the upgrade that this boss drops */
	public EntityIABoss(World world, EnumAlloy alloy) {
		super(world);
		setSize(2F, 8F);
		this.alloy = alloy;
		isImmuneToFire = true;
		experienceValue = 50;
		setHomeArea((int)posX, (int)posY, (int)posZ, 15); // Fix the entity to a certain area

		// All the AI tasks needed for proper operation
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	@Override
	protected Item getDropItem() {
		return IAItems.upgrades[Consts.ALLOY_UPG];
	}

	@Override
	protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
		if(hitByPlayer)
			entityDropItem(new ItemStack(IAItems.upgrades[Consts.ALLOY_UPG], 1, alloy.ordinal()), 0F);
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}
}
