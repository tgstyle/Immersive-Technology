package mctmods.immersivetechnology.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class CommonProxy {

	@SuppressWarnings("unused")
	private static final Logger log=LogManager.getLogger(ImmersiveTechnology.MODID + "/CommonProxy");

	public void construct() {}

	public void setup() {}

	public void completed() {
	}

	public void registerContainersAndScreens() {
	}

	public void preInit() {
	}

	public void preInitEnd() {
	}

	public void init() {
	}

	public void postInit() {
	}

	public void serverAboutToStart() {
	}

	public void serverStarting() {
	}

	public void serverStarted() {
	}

	public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer) {
	}

	public void handleEntitySound(SoundEvent soundEvent, Entity e, boolean active, float volume, float pitch) {
	}

	public void drawUpperHalfSlab(MatrixStack transform, ItemStack stack) {
	}

	public World getClientWorld() {
		return null;
	}

	public PlayerEntity getClientPlayer() {
		return null;
	}

	protected static ResourceLocation modLoc(String str) {
		return new ResourceLocation(ImmersiveTechnology.MODID, str);
	}

}
