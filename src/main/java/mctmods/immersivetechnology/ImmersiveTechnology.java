package mctmods.immersivetechnology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mctmods.immersivetechnology.client.ClientProxy;
import mctmods.immersivetechnology.common.CommonEventHandler;
import mctmods.immersivetechnology.common.CommonProxy;
import mctmods.immersivetechnology.common.ITConfig;
import mctmods.immersivetechnology.common.ITContent;
import mctmods.immersivetechnology.common.ITContent.Blocks;
import mctmods.immersivetechnology.common.crafting.RecipeReloadListener;
import mctmods.immersivetechnology.common.crafting.ITSerializers;
import mctmods.immersivetechnology.common.network.ITPacketHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImmersiveTechnology.MODID)
public class ImmersiveTechnology {

	public static final String MODID = "immersivetechnology";
	public static final Logger log = LogManager.getLogger(MODID);
	public static final ItemGroup creativeTab = new ItemGroup(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.fluidValve);
		}
	};

	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public static ImmersiveTechnology INSTANCE;

	public ImmersiveTechnology() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ITConfig.ALL);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		ITSerializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ITContent.populate();
		proxy.construct();
		proxy.registerContainersAndScreens();
	}

	public void setup(FMLCommonSetupEvent event) {
		proxy.setup();
		proxy.preInit();
		ITContent.preInit();
		ITPacketHandler.preInit();
		proxy.preInitEnd();
		ITContent.init();
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		proxy.init();
		proxy.postInit();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		proxy.completed();
	}

	public void serverAboutToStart(FMLServerAboutToStartEvent event) {
		proxy.serverAboutToStart();
	}

	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting();
	}

	public void registerCommand(RegisterCommandsEvent event) {
	}

	public void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getDataPackRegistries()));
	}

	public void serverStarted(FMLServerStartedEvent event) {
		proxy.serverStarted();
	}

}
