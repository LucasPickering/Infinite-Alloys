package infinitealloys.client;

import java.util.ArrayList;
import infinitealloys.ContainerMachine;
import infinitealloys.IAValues;
import infinitealloys.TileEntityComputer;
import infinitealloys.handlers.PacketHandler;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class GuiComputer extends GuiMachine {

	private TileEntityComputer tec;
	private ArrayList<GuiMachineButton> machineButtons = new ArrayList<GuiMachineButton>();

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(tileEntity, new ContainerMachine(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 216;
		tec = tileEntity;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		for(GuiMachineButton button : machineButtons)
			button.drawButton(mc, mouseX, mouseY);
		updateGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int k = mc.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	private void updateGui() {
		machineButtons.clear();
		for(int i = 0; i < tec.networkCoords.size(); i++) {
			Vec3 coords = tec.networkCoords.get(i);
			machineButtons.add(new GuiMachineButton(width / 2 - 73 + i / 5 * 84, height / 2 - 101 + i % 5 * 24, (int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(GuiMachineButton button : machineButtons)
			if(button.mousePressed(mouseX, mouseY)) {
				World world = Minecraft.getMinecraft().theWorld;
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				int x = button.blockX;
				int y = button.blockY;
				int z = button.blockZ;
				player.closeScreen();
				//Block.blocksList[world.getBlockId(x, y, z)].onBlockActivated(world, x, y, z, player, 0, 0, 0, 0);
				//PacketDispatcher.sendPacketToServer(PacketHandler.getComputerPacketOpenGui(x, y, z));
			}
	}
}
