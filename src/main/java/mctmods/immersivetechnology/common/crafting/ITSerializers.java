package mctmods.immersivetechnology.common.crafting;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ITSerializers {

	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersiveTechnology.MODID);

}
