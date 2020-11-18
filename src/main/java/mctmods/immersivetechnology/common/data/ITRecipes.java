package mctmods.immersivetechnology.common.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.ResourceLocation;

public class ITRecipes extends RecipeProvider {

	private final Map<String, Integer> PATH_COUNT=new HashMap<>();

	protected Consumer<IFinishedRecipe> out;
	public ITRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> out) {
		this.out=out;
		itemRecipes();
		blockRecipes();
	}

	private void blockRecipes() {
	}

	private void itemRecipes() {
	}

	private ResourceLocation rl(String string) {
		if(PATH_COUNT.containsKey(string)) {
			int count=PATH_COUNT.get(string) + 1;
			PATH_COUNT.put(string, count);
			return new ResourceLocation(ImmersiveTechnology.MODID, string + count);
		}
		PATH_COUNT.put(string, 1);
		return new ResourceLocation(ImmersiveTechnology.MODID, string);
	}

}
