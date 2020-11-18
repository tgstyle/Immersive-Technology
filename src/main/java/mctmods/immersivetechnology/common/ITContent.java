package mctmods.immersivetechnology.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.blocks.FuidValveBlock;
import mctmods.immersivetechnology.common.blocks.ITBlockBase;
import mctmods.immersivetechnology.common.items.ITItemBase;
import mctmods.immersivetechnology.common.util.fluids.ITFluid;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = ImmersiveTechnology.MODID, bus=Bus.MOD)
public class ITContent {

	public static final Logger log=LogManager.getLogger(ImmersiveTechnology.MODID + "/Content");
	public static final List<Block> registeredITBlocks = new ArrayList<>();
	public static final List<Item> registeredITItems = new ArrayList<>();
	public static final List<Fluid> registeredITFluids = new ArrayList<>();

	public static class Multiblock {
		//public static Block amultiblock;
	}

	public static class Fluids {
		public static ITFluid steam;
	}

	public static class Blocks {
		public static ITBlockBase fluidValve;
	}

	public static class Items {
		public static ITItemBase salt;
	}

	public static void populate() {
		Fluids.steam = new ITFluid("steam", 1000, 2250);

		//Multiblock.amultiblock=new AMultiBlockBlock();

		Blocks.fluidValve=new FuidValveBlock();

		Items.salt = new ITItemBase("salt");
	}

	public static void preInit() {
	}

	public static void init() {
		//MultiblockHandler.registerMultiblock(AMultiBlock.INSTANCE);
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
		//registerTile(event, AMultiBlockTileEntity.class, Multiblock.amultiblock);
	}

	public static <T extends TileEntity> void registerTile(RegistryEvent.Register<TileEntityType<?>> event, Class<T> tile, Block... valid) {
		String s = tile.getSimpleName();
		s = s.substring(0, s.indexOf("TileEntity")).toLowerCase(Locale.ENGLISH);
		TileEntityType<T> type = createType(tile, valid);
		type.setRegistryName(ImmersiveTechnology.MODID, s);
		event.getRegistry().register(type);
		try {
			tile.getField("TYPE").set(null, type);
		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		log.debug("Registered TileEntity: {} as {} ", tile, type.getRegistryName());
	}

	private static <T extends TileEntity> TileEntityType<T> createType(Class<T> typeClass, Block... valid) {
		Set<Block> validSet = new HashSet<>(Arrays.asList(valid));
		@SuppressWarnings("deprecation")
		TileEntityType<T> type = new TileEntityType<>(() -> {
			try {
				return typeClass.newInstance();
			} catch(InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		} , validSet, null);
		return type;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		for(Block block:registeredITBlocks) {
			try {
				event.getRegistry().register(block);
			} catch(Throwable e) {
				log.error("Failed to register block. ({})", block);
				throw e;
			}
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		for(Item item:registeredITItems) {
			try {
				event.getRegistry().register(item);
			} catch(Throwable e) {
				log.error("Failed to register item. ({} , {})", item, item.getRegistryName());
				throw e;
			}
		}
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event) {
		for(Fluid fluid:registeredITFluids) {
			try {
				event.getRegistry().register(fluid);
			} catch(Throwable e) {
				log.error("Failed to register fluid. ({} , {})", fluid, fluid.getRegistryName());
				throw e;
			}
		}
	}

}
