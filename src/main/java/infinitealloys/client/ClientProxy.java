package infinitealloys.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import infinitealloys.block.IABlocks;
import infinitealloys.client.render.RenderBoss;
import infinitealloys.client.render.TileEntityMachineRenderer;
import infinitealloys.core.CommonProxy;
import infinitealloys.item.IAItems;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumBoss;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;

public final class ClientProxy extends CommonProxy {

  @Override
  public void initBlocks() {
    super.initBlocks();
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      Funcs.registerBlockModel(IABlocks.ore, i, "ore");
      ModelBakery.addVariantName(Item.getItemFromBlock(IABlocks.ore),
                                 Consts.MOD_ID + ":" + EnumMetal.values()[i].getName());
    }
  }

  @Override
  public void initItems() {
    super.initItems();
    Funcs.registerItemModel(IAItems.machineComponent, "machineComponent");
    Funcs.registerItemModel(IAItems.upgradeComponent, "upgradeComponent");

    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      Funcs.registerItemModel(IAItems.ingot, i, "ingot");
    }

    Funcs.registerItemModel(IAItems.internetWand, "internetWand");

    for (EnumUpgrade upgradeType : EnumUpgrade.values()) {
      Item item = IAItems.upgrades[upgradeType.ordinal()];
      for (int i = 0; i < upgradeType.tiers; i++) {
        String variantName = upgradeType.name + (i + 1);
        Funcs.registerItemModel(item, i, variantName);
        ModelBakery.addVariantName(item, Consts.MOD_ID + ":" + variantName);
      }
    }
  }

  @Override
  public void initHandlers() {
    super.initHandlers();
    MinecraftForge.EVENT_BUS.register(gfxHandler);
  }

  @Override
  public void initRendering() {
//    gfxHandler.renderID = RenderingRegistry.getNextAvailableRenderId();
    for (EnumMachine machine : EnumMachine.values()) {
      ClientRegistry.bindTileEntitySpecialRenderer(machine.temClass,
                                                   new TileEntityMachineRenderer(machine));
    }
//    RenderingRegistry.registerBlockHandler(gfxHandler);
    try {
      for (EnumBoss boss : EnumBoss.values()) {
        RenderingRegistry.registerEntityRenderingHandler(boss.entityClass, new RenderBoss(boss));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
