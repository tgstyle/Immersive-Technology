package mctmods.immersivetechnology.common.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid=ImmersiveTechnology.MODID, bus=Bus.MOD)
public class ITDataGenerator {

	public static final Logger log=LogManager.getLogger(ImmersiveTechnology.MODID + "/DataGenerator");

	@SubscribeEvent
	public static void generate(GatherDataEvent event) {
		if(event.includeServer()) {
			DataGenerator generator=event.getGenerator();
			ITBlockTags blockTags=new ITBlockTags(generator);
			generator.addProvider(new ITBlockTags(generator));
			generator.addProvider(new ITItemTags(generator, blockTags));
			generator.addProvider(new ITFluidTags(generator));
			generator.addProvider(new ITRecipes(generator));
			ITLoadedModels loadedModels=new ITLoadedModels(generator, event.getExistingFileHelper());
			ITBlockStates blockstates=new ITBlockStates(generator, event.getExistingFileHelper(), loadedModels);
			generator.addProvider(blockstates);
			generator.addProvider(loadedModels);
			generator.addProvider(new ITItemModels(generator, event.getExistingFileHelper(), blockstates));
		}
	}

}
