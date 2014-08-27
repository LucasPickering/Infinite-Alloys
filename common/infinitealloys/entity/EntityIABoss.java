package infinitealloys.entity;

import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class EntityIABoss extends EntityMob {

	private final int alloyID;

	/** @param alloyID the ID of the alloy that is unlocked by the upgrade that this boss drops */
	public EntityIABoss(World world, int alloyID) {
		super(world);
		this.alloyID = alloyID;
        this.isImmuneToFire = true;
        this.experienceValue = 50;
	}

	@Override
	protected Item getDropItem() {
		return IAItems.upgrades[Consts.ALLOY_UPG];
	}

	@Override
	protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
		if(hitByPlayer)
			entityDropItem(new ItemStack(IAItems.upgrades[Consts.ALLOY_UPG], 1, alloyID), 0F);
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}
}
