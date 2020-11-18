package mctmods.immersivetechnology.common.data;

import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.client.models.split.SplitModelLoader;
import blusunrize.immersiveengineering.common.data.models.LoadedModelBuilder;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import mctmods.immersivetechnology.common.util.fluids.ITFluid;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ITBlockStates extends BlockStateProvider {

	private static final ResourceLocation FORGE_LOADER=new ResourceLocation("forge","obj");
	final ITLoadedModels loadedModels;
	final ExistingFileHelper exFileHelper;

	public ITBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper, ITLoadedModels loadedModels) {
		super(gen, ImmersiveTechnology.MODID, exFileHelper);
		this.loadedModels=loadedModels;
		this.exFileHelper=exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		// Multiblocks

		// Blocks
		simpleBlockWithItem(ITContent.Blocks.fluidValve);

		// Fluids
		for(ITFluid fluid:ITFluid.FLUIDS) {
			ResourceLocation still=fluid.getAttributes().getStillTexture();
			ModelFile model = this.loadedModels.getBuilder("block/fluid/" + fluid.getRegistryName().getPath()).texture("particle", still);
			getVariantBuilder(fluid.block).partialState().setModels(new ConfiguredModel(model));
		}
		loadedModels.backupModels();
	}

	private LoadedModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror) {
		UnaryOperator<BlockPos> transform = UnaryOperator.identity();
		if(mirror) {
			@SuppressWarnings("deprecation")
			Vector3i size = mb.getSize();
			transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
		}
		final Vector3i offset = mb.getMasterFromOriginOffset();
		@SuppressWarnings("deprecation")
		Stream<Vector3i> partsStream=mb.getStructure().stream()
				.filter(info -> !info.state.isAir())
				.map(info -> info.pos)
				.map(transform)
				.map(p -> p.subtract(offset));
		LoadedModelBuilder out=this.loadedModels.withExistingParent(getMultiblockPath(block) + add, mcLoc("block"))
				.texture("texture", texture)
				.texture("particle", texture)
				.additional("flip-v", true)
				.additional("model", model)
				.additional("detectCullableFaces", false)
				.additional(SplitModelLoader.BASE_LOADER, FORGE_LOADER)
				.additional(SplitModelLoader.DYNAMIC, false)
				.loader(SplitModelLoader.LOCATION);
		JsonArray partsJson=partsStream.collect(POSITIONS_TO_JSON);
		out.additional(SplitModelLoader.PARTS, partsJson);
		return out;
	}

	private static final Collector<Vector3i, JsonArray, JsonArray> POSITIONS_TO_JSON = Collector.of(
			JsonArray::new,
			(arr, vec) -> {
				JsonArray posJson = new JsonArray();
				posJson.add(vec.getX());
				posJson.add(vec.getY());
				posJson.add(vec.getZ());
				arr.add(posJson);
			} ,
			(a, b) -> {
				JsonArray arr = new JsonArray();
				arr.addAll(a);
				arr.addAll(b);
				return arr;
			}
	);

	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates}
	 * @param idleTexture */
	private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture) {
		createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
	}

	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex) {
		Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
		VariantBlockStateBuilder builder = getVariantBuilder(b);
		boolean[] possibleMirrorStates;
		if(mirroredState != null) possibleMirrorStates = new boolean[] {false, true};
		else possibleMirrorStates = new boolean[1];
		for(boolean mirrored:possibleMirrorStates)
			for(Direction dir:facing.getAllowedValues()) {
				final int angleY;
				final int angleX;
				if(facing.getAllowedValues().contains(Direction.UP)) {
					angleX = -90 * dir.getYOffset();
					if(dir.getAxis() != Axis.Y)	angleY = getAngle(dir, rotationOffset);
					else angleY = 0;
				} else {
					angleY = getAngle(dir, rotationOffset);
					angleX = 0;
				}
				ModelFile model = mirrored ? mirroredModel : masterModel;
				PartialBlockstate partialState = builder.partialState().with(facing, dir);
				if(mirroredState != null) partialState = partialState.with(mirroredState, mirrored);
				partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
			}
	}

	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private int getAngle(Direction dir, int offset) {
		return (int) ((dir.getHorizontalAngle() + offset) % 360);
	}

	private String getMultiblockPath(Block b) {
		return "multiblock/" + getPath(b);
	}

	private String getPath(Block b) {
		return b.getRegistryName().getPath();
	}

	private void itemModelWithParent(Block block, ModelFile parent) {
		getItemBuilder(block).parent(parent)
			.texture("particle", modLoc("block/" + getPath(block)));
	}

	private void simpleBlockWithItem(Block block) {
		ModelFile file=cubeAll(block);
		getVariantBuilder(block).partialState().setModels(new ConfiguredModel(file));
		itemModelWithParent(block, file);
	}

	private ItemModelBuilder getItemBuilder(Block block) {
		return itemModels().getBuilder(modLoc("item/" + getPath(block)).toString());
	}

}
