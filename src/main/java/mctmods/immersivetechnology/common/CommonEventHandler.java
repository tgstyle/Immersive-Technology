package mctmods.immersivetechnology.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventHandler {

	@SubscribeEvent
	public void onEntityJoiningWorld(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof PlayerEntity) {
			if(event.getEntity() instanceof FakePlayer) {
				return;
			}
			if(ITConfig.MISCELLANEOUS.autounlock_recipes.get()) {
				List<IRecipe<?>> l = new ArrayList<IRecipe<?>>();
				Collection<IRecipe<?>> recipes=event.getWorld().getRecipeManager().getRecipes();
				recipes.forEach(recipe-> {
					ResourceLocation name = recipe.getId();
					if(name.getNamespace()==ImmersiveTechnology.MODID) {
						if(recipe.getRecipeOutput().getItem() != null) {
							l.add(recipe);
						}
					}
				});
				((PlayerEntity) event.getEntity()).unlockRecipes(l);
			}
		}
	}

	public static Map<ResourceLocation, List<BlockPos>> napalmPositions = new HashMap<>();
	public static Map<ResourceLocation, List<BlockPos>> toRemove = new HashMap<>();

}
