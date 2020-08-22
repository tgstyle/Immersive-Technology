package mctmods.immersivetechnology.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.GuiIEContainerBase;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonIE;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.blocks.connectors.tileentities.TileEntityTimer;
import mctmods.immersivetechnology.common.gui.ContainerTimer;
import mctmods.immersivetechnology.common.util.network.MessageTileSync;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import org.lwjgl.opengl.GL11;

public class GuiTimer extends GuiIEContainerBase {
	TileEntityTimer tile;

	public GuiTimer(PlayerInventory playerInventory, TileEntityTimer tile) {
		super(new ContainerTimer(playerInventory, tile));
		this.tile=tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(new GuiButtonIE(0, guiLeft + 39, guiTop + 35, 16, 16, "+", "immersivetech:textures/gui/gui_timer.png", 176, 0));
		this.buttonList.add(new GuiButtonIE(1, guiLeft + 120, guiTop + 35, 16, 16, "-", "immersivetech:textures/gui/gui_timer.png", 176, 16));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		CompoundNBT tag = new CompoundNBT();
		tag.setInteger("buttonId", button.id);
		ImmersiveTechnology.packetHandler.sendToServer(new MessageTileSync(tile, tag));
		this.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture("immersivetech:textures/gui/gui_timer.png");
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		float time = (float)tile.getTarget() / 20;
		this.drawString(this.fontRenderer, String.valueOf(time).toString() + " Sec.", guiLeft + 68, guiTop + 40, 0xFFFFFF);
	}

}