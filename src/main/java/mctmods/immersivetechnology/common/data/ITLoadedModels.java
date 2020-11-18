package mctmods.immersivetechnology.common.data;

import java.util.HashMap;
import java.util.Map;

import blusunrize.immersiveengineering.common.data.models.LoadedModelBuilder;
import blusunrize.immersiveengineering.common.data.models.LoadedModelProvider;
import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ITLoadedModels extends LoadedModelProvider {

	final Map<ResourceLocation, LoadedModelBuilder> models=new HashMap<>();

	public ITLoadedModels(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, ImmersiveTechnology.MODID, "block", existingFileHelper);
	}

	@Override
	protected void registerModels() {
		super.generatedModels.putAll(models);
	}

	public void backupModels() {
		models.putAll(super.generatedModels);
	}

	@Override
	public String getName() {
		return "Loaded Models";
	}

}
