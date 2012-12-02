package infinitealloys.client;

import infinitealloys.BlockMachine;
import infinitealloys.InfiniteAlloys;
import infinitealloys.Point;
import infinitealloys.References;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMachine;
import infinitealloys.handlers.PacketHandler;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import universalelectricity.core.electricity.ElectricInfo;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class GuiMachine extends GuiContainer {

	public static Rectangle ENERGY_METER = new Rectangle(0, 0, 10, 32);
	public static Rectangle TAB_LEFT_OFF = new Rectangle(10, 0, 24, 24);
	public static Rectangle TAB_LEFT_ON = new Rectangle(34, 0, 28, 24);
	public static Rectangle TAB_RIGHT_OFF = new Rectangle(62, 0, 29, 24);
	public static Rectangle TAB_RIGHT_ON = new Rectangle(91, 0, 28, 24);
	public static Rectangle PROGRESS_BAR = new Rectangle(119, 0, 108, 18);
	public static Rectangle UP_ARROW = new Rectangle(10, 24, 16, 16);
	public static Rectangle DOWN_ARROW = new Rectangle(26, 24, 16, 16);
	public static Rectangle CHECK = new Rectangle(42, 24, 16, 16);

	protected java.awt.Point topLeft = new java.awt.Point();
	protected java.awt.Point energyMeter = new java.awt.Point();
	protected TileEntityMachine tem;
	protected infinitealloys.Point controllingComputer = new infinitealloys.Point();
	protected GuiMachineTab controllerTab;
	protected ArrayList<GuiMachineTab> machineTabs = new ArrayList<GuiMachineTab>();

	public GuiMachine(int xSize, int ySize, TileEntityMachine tileEntity, Container container) {
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
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
			texts.add(InfiniteAlloys.getStringLocalization("upgrade.name"));
			colors.add(0xffffff);
			for(int i = 0; i < References.upgradeCount; i++) {
				int damage = (int)Math.pow(2, i);
				if(tem.isPrereqUpgrade(new ItemStack(InfiniteAlloys.upgrade, 1, damage)) && tem.hasUpgrade(damage << 1) || !tem.hasUpgrade(damage))
					continue;
				texts.add(InfiniteAlloys.getStringLocalization("upgrade." + References.upgradeNames[i] + ".name"));
				colors.add(0xaaaaaa);
			}
			drawTextBox(texts, colors, mouseX, mouseY);
		}
		if(mouseInZone(mouseX, mouseY, energyMeter.x + topLeft.x, energyMeter.y + ENERGY_METER.height - tem.getJoulesScaled(ENERGY_METER.height) + topLeft.y, ENERGY_METER.width, tem.getJoulesScaled(ENERGY_METER.height)))
			drawTextBox(ElectricInfo.getDisplayShort(tem.joules, ElectricInfo.ElectricUnit.JOULES), 0xffffff, mouseX, mouseY);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture("extras");
		drawTexturedModalRect(energyMeter.x, energyMeter.y, ENERGY_METER.x, ENERGY_METER.y + ENERGY_METER.height - tem.getJoulesScaled(ENERGY_METER.height), ENERGY_METER.width, tem.getJoulesScaled(ENERGY_METER.height));
		machineTabs.clear();
		Point cont = TileEntityMachine.controllers.get(Minecraft.getMinecraft().thePlayer.username);
		if(cont != null) {
			TileEntityComputer tec = ((TileEntityComputer)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z));
			controllerTab = new GuiMachineTab(itemRenderer, -24, 6, (TileEntityMachine)mc.theWorld.getBlockTileEntity(cont.x, cont.y, cont.z), true, tem.coordsEquals(cont.x, cont.y, cont.z));
			controllerTab.drawButton(mc);
			for(int i = 0; i < tec.networkCoords.size(); i++) {
				Point coords = tec.networkCoords.get(i);
				machineTabs.add(new GuiMachineTab(itemRenderer, i / 5 * 197 - 24, i % 5 * 25 + 36, (TileEntityMachine)mc.theWorld.getBlockTileEntity(coords.x, coords.y, coords.z), i / 5 == 0, tem.coordsEquals(coords.x, coords.y, coords.z)));
				machineTabs.get(i).drawButton(mc);
			}
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(String text, int color, int mouseX, int mouseY) {
		ArrayList<String> texts = new ArrayList<String>();
		ArrayList<Integer> colors = new ArrayList<Integer>();
		texts.add(text);
		colors.add(color);
		drawTextBox(texts, colors, mouseX, mouseY);
	}

	protected void drawTextBox(List<String> text, List<Integer> colors, int mouseX, int mouseY) {
		if(!text.isEmpty()) {
			int boxWidth = 0;
			for(String line : text) {
				int lineWidth = fontRenderer.getStringWidth(line);
				if(lineWidth > boxWidth)
					boxWidth = lineWidth;
			}
			mouseX += 12;
			mouseY -= 12;
			int var9 = 8;
			if(text.size() > 1)
				var9 += 2 + (text.size() - 1) * 10;
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
			for(int i = 0; i < text.size(); i++) {
				fontRenderer.drawStringWithShadow(text.get(i), mouseX, mouseY, colors.get(i));
				if(i == 0)
					mouseY += 2;
				mouseY += 10;
			}
			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
		}
	}

	protected void bindTexture(String texture) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(References.TEXTURE_PATH + "gui/" + texture + ".png"));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(controllerTab != null && controllerTab.mousePressed(mouseX, mouseY)) {
			World world = Minecraft.getMinecraft().theWorld;
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			int x = controllerTab.tem.xCoord;
			int y = controllerTab.tem.yCoord;
			int z = controllerTab.tem.zCoord;
			if(!tem.coordsEquals(x, y, z)) {
				((BlockMachine)Block.blocksList[world.getBlockId(x, y, z)]).openGui(world, (EntityPlayer)player, controllerTab.tem, true);
				PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z));
			}
			return;
		}
		for(GuiMachineTab tab : machineTabs) {
			if(tab.mousePressed(mouseX, mouseY)) {
				World world = Minecraft.getMinecraft().theWorld;
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				int x = tab.tem.xCoord;
				int y = tab.tem.yCoord;
				int z = tab.tem.zCoord;
				if(!tem.coordsEquals(x, y, z)) {
					((BlockMachine)Block.blocksList[world.getBlockId(x, y, z)]).openGui(world, (EntityPlayer)player, tab.tem, true);
					PacketDispatcher.sendPacketToServer(PacketHandler.getPacketOpenGui(x, y, z));
				}
				return;
			}
		}
	}

	protected boolean mouseInZone(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
		return mouseX >= xStart && mouseY >= yStart && mouseX < xStart + width && mouseY < yStart + height;
	}
}
