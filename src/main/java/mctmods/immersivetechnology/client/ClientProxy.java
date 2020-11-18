package mctmods.immersivetechnology.client;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.TextSplitter;
import blusunrize.lib.manual.Tree.InnerNode;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.CommonProxy;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ImmersiveTechnology.MODID)
public class ClientProxy extends CommonProxy {

	@SuppressWarnings("unused")
	private static final Logger log=LogManager.getLogger(ImmersiveTechnology.MODID + "/ClientProxy");
	public static final String CAT_IT = "it";
	public static final KeyBinding keybind_preview_flip = new KeyBinding("key.immersivetechnology.projector.flip", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_3, "key.categories.gameplay");

	@Override
	public void construct() {}

	@Override
	public void registerContainersAndScreens() {
		super.registerContainersAndScreens();
	}

	@SuppressWarnings("unchecked")
	public <C extends Container, S extends Screen & IHasContainer<C>> void registerScreen(ResourceLocation name, IScreenFactory<C, S> factory) {
		ContainerType<C> type=(ContainerType<C>)GuiHandler.getContainerType(name);
		ScreenManager.registerFactory(type, factory);
	}

	@Override
	public void completed() {
		setupManualPages();
	}

	@Override
	public void preInit() {
	}

	@Override
	public void preInitEnd() {
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		keybind_preview_flip.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(keybind_preview_flip);
	}

	private static InnerNode<ResourceLocation, ManualEntry> IT_CATEGORY;
	public void setupManualPages() {
		ManualInstance man=ManualHelper.getManual();
		IT_CATEGORY=man.getRoot().getOrCreateSubnode(modLoc("main"), 100);
		man.addEntry(IT_CATEGORY, modLoc("fluidvalve"), 1);
	}

	protected static EntryData createContentTest(TextSplitter splitter) {
		return new EntryData("title", "subtext", "content");
	}

	static final DecimalFormat FORMATTER = new DecimalFormat("#,###.##");
	static ManualEntry entry;

	@Override
	public void postInit() {
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onModelBakeEvent(ModelBakeEvent event) {
	}

	@Override
	public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer) {
		TileEntityRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer((TileEntity) te);
		transform.push();
		transform.rotate(new Quaternion(0, -90, 0, true));
		transform.translate(0, 1, -4);
		tesr.render(te, 0, transform, buffer, 0xF000F0, 0);
		transform.pop();
	}

	@Override
	public void drawUpperHalfSlab(MatrixStack transform, ItemStack stack) {
		BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockState state = IEBlocks.MetalDecoration.steelScaffolding.get(MetalScaffoldingType.STANDARD).getDefaultState();
		IBakedModel model = blockRenderer.getBlockModelShapes().getModel(state);
		IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		transform.push();
		transform.translate(0.0F, 0.5F, 1.0F);
		blockRenderer.getBlockModelRenderer().renderModel(transform.getLast(), buffers.getBuffer(RenderType.getSolid()), state, model, 1.0F, 1.0F, 1.0F, -1, -1, EmptyModelData.INSTANCE);
		transform.pop();
	}

	@SuppressWarnings("resource")
	@Override
	public World getClientWorld() {
		return Minecraft.getInstance().world;
	}

	@SuppressWarnings("resource")
	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

}