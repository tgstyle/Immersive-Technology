package mctmods.immersivetechnology.client;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ImmersiveTechnology.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class BlockRenderLayers {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ITContent.Fluids.steam, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ITContent.Fluids.steam.getFlowingFluid(), RenderType.getTranslucent());
	}

}
