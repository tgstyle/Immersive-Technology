package mctmods.immersivetechnology.client.render;

import blusunrize.immersiveengineering.client.ClientUtils;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntitySteelSheetmetalTankMaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TileRendererSteelSheetmetalTank extends TileRendererMultiblockBase<TileEntitySteelSheetmetalTankMaster> {

	public static final TileRendererSteelSheetmetalTank renderer = new TileRendererSteelSheetmetalTank();

	@Override
	public void render(TileEntitySteelSheetmetalTankMaster tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(!tile.formed || !tile.getWorld().isBlockLoaded(tile.getPos(), false)) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x+.5, y, z+.5);
		FluidStack fs = tile.tank.getFluid();
		GlStateManager.translate(0, 3.5f, 0);
		float baseScale = .0625f;
		GlStateManager.scale(baseScale, -baseScale, baseScale);
		float xx = -.5f / baseScale;
		float zz = 1.5f / baseScale;

		GlStateManager.disableLighting();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		for(int i = 0; i < 4; i++) {
			GlStateManager.translate(xx, 0, zz);
			ClientUtils.drawColouredRect(6, 0, 4, 16, 0xFF333333); //background
			if(fs != null) {
				float h = fs.amount / (float)tile.tank.getCapacity();
				GlStateManager.translate(0, 0, 0.05);
				ClientUtils.drawRepeatedFluidSprite(fs, 6, 0 + (1 - h) * 16, 4, h * 16); //actual fluid render
				GlStateManager.translate(0, 0, -0.05);
			}
			GlStateManager.translate(-xx, 0, -zz);
			GlStateManager.rotate(90, 0, 1, 0);
		}

		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

}