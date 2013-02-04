package infinitealloys.client;

import infinitealloys.FuncHelper;
import infinitealloys.Point;
import infinitealloys.References;
import infinitealloys.block.BlockMachine;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TileEntityComputer;
import infinitealloys.tile.TileEntityMachine;
import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricInfo;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class GuiMachine extends GuiContainer {

	public static final Rectangle ENERGY_METER = new Rectangle(0, 0, 10, 32);
	public static final Rectangle TAB_LEFT_OFF = new Rectangle(10, 0, 24, 24);
	public static final Rectangle TAB_LEFT_ON = new Rectangle(34, 0, 28, 24);
	public static final Rectangle TAB_RIGHT_OFF = new Rectangle(62, 0, 29, 24);
	public static final Rectangle TAB_RIGHT_ON = new Rectangle(91, 0, 28, 24);
	public static final Rectangle PROGRESS_BAR = new Rectangle(119, 0, 108, 18);
	public static final Rectangle XRAY_GRID = new Rectangle(227, 0, 16, 16);
	public static final Rectangle UP_ARROW = new Rectangle(10, 24, 16, 16);
	public static final Rectangle DOWN_ARROW = new Rectangle(26, 24, 16, 16);
	public static final Rectangle CHECK = new Rectangle(42, 24, 16, 16);
	public static final Rectangle BLOCK_BG_OFF = new Rectangle(58, 24, 36, 18);
	public static final Rectangle BLOCK_BG_ON = new Rectangle(94, 24, 36, 18);

	private String texture;
	protected java.awt.Point topLeft = new java.awt.Point();
	protected java.awt.Point energyMeter = new java.awt.Point();
	protected java.awt.Point progressBar = new java.awt.Point();
	protected TileEntityMachine tem;
	protected infinitealloys.Point controllingComputer = new infinitealloys.Point();
	protected GuiMachineTab controllerTab;
	protected ArrayList<GuiMachineTab> machineTabs = new ArrayList<GuiMachineTab>();

	public GuiMachine(int xSize, int ySize, TileEntityMachine tileEntity, Container container, String texture) {
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
		this.texture = texture;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		energyMeter.setLocation(13, 7 + ENERGY_METER.height - tem.getJoulesScaled(ENERGY_METER.height));
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(func_74188_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
			ArrayList<String> texts = new ArrayList<String>();
			ArrayList<Integer> colors = new ArrayList<Integer>();
			texts.add(FuncHelper.getLoc("upgrade.name"));
			colors.add(0xffffff);
			for(int i = 0; i < References.upgradeCount; i++) {
				int damage = (int)Math.pow(2, i);
				if(TEHelper.isPrereqUpgrade(new ItemStack(Items.upgrade, 1, damage)) && tem.hasUpgrade(damage << 1) || !tem.hasUpgrade(damage))
					continue;
				texts.add(FuncHelper.getLoc("upgrade." + References.upgradeNames[i] + ".name"));
				colors.add(0xaaaaaa);
			}
			int[] colorsA = new int[colors.size()];
			for(int i = 0; i < colors.size(); i++)
				colorsA[i] = colors.get(i);
			drawTextBox(texts.toArray(new String[texts.size()]), colorsA, mouseX, mouseY);
		}
		int joulesScaled = tem.getJoulesScaled(ENERGY_METER.height);
		if(tem.maxJoules > 0 && mouseInZone(mouseX, mouseY, topLeft.x + energyMeter.x, topLeft.y + energyMeter.y + joulesScaled - ENERGY_METER.height, ENERGY_METER.width, ENERGY_METER.height)) {
			int joulesGained = tem.joulesGained;
			int joulesUsed = tem.getJoulesUsed();
			drawTextBox(new String[] { joulesString(tem.getJoules()), joulesString(joulesGained) + "/t IN", joulesString(joulesUsed) + "/t OUT" }, new int[] { 0xffffff, 0x00ff00, 0xff0000 }, mouseX, mouseY);
		}
		if(tem.ticksToProcess > 0 && mouseInZone(mouseX, mouseY, topLeft.x + progressBar.x, topLeft.y + progressBar.y, PROGRESS_BAR.width, PROGRESS_BAR.height))
			drawTextBox(tem.getProcessProgressScaled(100) + "%", 0xffffff, mouseX, mouseY);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		bindTexture(texture);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture("extras");
		if(tem.maxJoules > 0) {
			int joulesScaled = tem.getJoulesScaled(ENERGY_METER.height);
			drawTexturedModalRect(energyMeter.x, energyMeter.y, ENERGY_METER.x, ENERGY_METER.y + ENERGY_METER.height - joulesScaled, ENERGY_METER.width, joulesScaled);
		}
		if(tem.ticksToProcess > 0) {
			int progressScaled = tem.getJoulesScaled(PROGRESS_BAR.height);
			drawTexturedModalRect(progressBar.x, progressBar.y, PROGRESS_BAR.x, PROGRESS_BAR.y, tem.getProcessProgressScaled(PROGRESS_BAR.width), PROGRESS_BAR.height);
		}
		machineTabs.clear();
		Point cont = TEHelper.controllers.get(mc.thePlayer.username);
		if(cont != null) {
			TileEntityComputer tec = ((TileEntityComputer)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z));
			controllerTab = new GuiMachineTab(mc, itemRenderer, -24, 6, (TileEntityMachine)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z), true, tem.coordsEquals(cont.x, cont.y, cont.z));
			controllerTab.drawButton();
			for(int i = 0; i < tec.networkCoords.size(); i++) {
				Point coords = tec.networkCoords.get(i);
				machineTabs.add(new GuiMachineTab(mc, itemRenderer, i / 5 * 197 - 24, i % 5 * 25 + 36, (TileEntityMachine)mc.theWorld.getBlockTileEntity(coords.x, coords.y, coords.z), i / 5 == 0, tem.coordsEquals(coords.x, coords.y, coords.z)));
				machineTabs.get(i).drawButton();
			}
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(String text, int color, int mouseX, int mouseY) {
		drawTextBox(new String[] { text }, new int[] { color }, mouseX, mouseY);
	}

	protected void drawTextBox(String[] lines, int[] colors, int mouseX, int mouseY) {
		if(lines.length > 0) {
			int boxWidth = 0;
			for(String line : lines)
				boxWidth = Math.max(boxWidth, fontRenderer.getStringWidth(line));
			mouseX += 12;
			mouseY -= 12;
			int var9 = 8;
			if(lines.length > 1)
				var9 += 2 + (lines.length - 1) * 10;
			int var10 = -267386864;
			drawGradientRect(mouseX - 3, mouseY - 4, mouseX + boxWidth + 3, mouseY - 3, var10, var10);
			drawGradientRect(mouseX - 3, mouseY + var9 + 3, mouseX + boxWidth + 3, mouseY + var9 + 4, var10, var10);
			drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY + var9 + 3, var10, var10);
			drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + var9 + 3, var10, var10);
			drawGradientRect(mouseX + boxWidth + 3, mouseY - 3, mouseX + boxWidth + 4, mouseY + var9 + 3, var10, var10);
			int var11 = 1347420415;
			int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
			drawGradientRect(mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + var9 + 3 - 1, var11, var12);
			drawGradientRect(mouseX + boxWidth + 2, mouseY - 3 + 1, mouseX + boxWidth + 3, mouseY + var9 + 3 - 1, var11, var12);
			drawGradientRect(mouseX - 3, mouseY - 3, mouseX + boxWidth + 3, mouseY - 3 + 1, var11, var11);
			drawGradientRect(mouseX - 3, mouseY + var9 + 2, mouseX + boxWidth + 3, mouseY + var9 + 3, var12, var12);
			for(int i = 0; i < lines.length; i++) {
				fontRenderer.drawStringWithShadow(lines[i], mouseX, mouseY, colors[i]);
				mouseY += i == 0 ? 12 : 10;
			}
			zLevel = 0F;
			itemRenderer.zLevel = 0F;
		}
	}

	public static void bindTexture(String texture) {
		Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getTexture(References.TEXTURE_PATH + "gui/" + texture + ".png"));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(controllerTab != null && controllerTab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
			int x = controllerTab.tem.xCoord;
			int y = controllerTab.tem.yCoord;
			int z = controllerTab.tem.zCoord;
			if(!tem.coordsEquals(x, y, z)) {
				((BlockMachine)FuncHelper.getBlock(world, x, y, z)).openGui(world, player, controllerTab.tem, false);
				PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z, false));
			}
			return;
		}
		for(GuiMachineTab tab : machineTabs) {
			if(tab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
				int x = tab.tem.xCoord;
				int y = tab.tem.yCoord;
				int z = tab.tem.zCoord;
				if(!tem.coordsEquals(x, y, z)) {
					((BlockMachine)FuncHelper.getBlock(world, x, y, z)).openGui(world, player, tab.tem, true);
					PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z, true));
				}
				return;
			}
		}
	}

	protected boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
		return mouseX >= xStart && mouseY >= yStart && mouseX < xStart + width && mouseY < yStart + height;
	}

	protected String joulesString(double joules) {
		return ElectricInfo.getDisplayShort(joules, ElectricInfo.ElectricUnit.JOULES);
	}
}
