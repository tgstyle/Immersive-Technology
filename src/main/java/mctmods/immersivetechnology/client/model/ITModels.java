package mctmods.immersivetechnology.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ImmersiveTechnology.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ITModels {

	@SubscribeEvent
	public static void init(FMLConstructModEvent event) {
	}

	private static final Map<String, ITModel> MODELS = new HashMap<>();

	public static void add(String id, ITModel model) {
		if(MODELS.containsKey(id)) {
			ImmersiveTechnology.log.error("Duplicate ID, \" {} \" already used by {} . Skipping.", id, MODELS.get(id).getClass());
		} else {
			model.init();
			MODELS.put(id, model);
		}
	}

	public static Supplier<ITModel> getSupplier(String id) {
		return () -> MODELS.get(id);
	}

	public static Collection<ITModel> getModels() {
		return Collections.unmodifiableCollection(MODELS.values());
	}

}
