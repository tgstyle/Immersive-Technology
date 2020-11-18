package mctmods.immersivetechnology.client.model;

import java.util.function.Function;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

public abstract class ITModel extends Model {

	public ITModel(Function<ResourceLocation, RenderType> renderTypeIn) {
		super(renderTypeIn);
	}

	public abstract void init();

}
