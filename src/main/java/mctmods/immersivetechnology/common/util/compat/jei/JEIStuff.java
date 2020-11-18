package mctmods.immersivetechnology.common.util.compat.jei;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIStuff implements IModPlugin {

	private static final ResourceLocation ID=new ResourceLocation(ImmersiveTechnology.MODID, "main");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper=registration.getJeiHelpers().getGuiHelper();
	}

}
