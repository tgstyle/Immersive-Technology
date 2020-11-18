package mctmods.immersivetechnology.common.util.compat.crafttweaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.util.ResourceLocation;

public class CTUtil {

	public static final Logger log = LogManager.getLogger(ImmersiveTechnology.MODID + "/CT-Compat");

	public static ResourceLocation ctLoc(String name) {
		return new ResourceLocation("crafttweaker", name);
	}

	public static ResourceLocation itLoc(String name) {
		return new ResourceLocation(ImmersiveTechnology.MODID, name);
	}

	public static ResourceLocation mcLoc(String name) {
		return new ResourceLocation("minecraft", name);
	}

}
