package mctmods.immersivetechnology.common.data;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.data.models.LoadedModelBuilder;
import blusunrize.immersiveengineering.common.data.models.LoadedModelProvider;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import mctmods.immersivetechnology.common.util.fluids.ITFluid;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ITItemModels extends LoadedModelProvider {

	ITBlockStates blockStates;
	public ITItemModels(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, ITBlockStates blockstates) {
		super(dataGenerator, ImmersiveTechnology.MODID, "item", existingFileHelper);
		this.blockStates=blockstates;
	}

	@Override
	public String getName() {
		return "Item Models";
	}

	@Override
	protected void registerModels() {
		genericItem(ITContent.Items.salt);
		for(ITFluid fluid:ITFluid.FLUIDS) createBucket(fluid);
	}

	private Matrix4 createMatrix(Vector3d translation, Vector3d rotationAngle, double scale) {
		Matrix4 matrix=new Matrix4().setIdentity();
		matrix.translate(translation.x/16D, translation.y/16D, translation.z/16D);
		if(rotationAngle!=null) {
			if(rotationAngle.x!=0.0)
				matrix.rotate(Math.toRadians(rotationAngle.x), 1, 0, 0);

			if(rotationAngle.y!=0.0)
				matrix.rotate(Math.toRadians(rotationAngle.y), 0, 1, 0);

			if(rotationAngle.z!=0.0)
				matrix.rotate(Math.toRadians(rotationAngle.z), 0, 0, 1);
		}
		matrix.scale(scale, scale, scale);
		return matrix;
	}

	private LoadedModelBuilder obj(IItemProvider item, String model) {
		return getBuilder(item).loader(forgeLoc("obj")).additional("model", modLoc("models/" + model)).additional("flip-v", true);
	}

	private void genericItem(Item item) {
		if(item==null) {
			StackTraceElement where=new NullPointerException().getStackTrace()[1];
			ITDataGenerator.log.warn("Skipping null item. ({} -> {})", where.getFileName(), where.getLineNumber());
			return;
		}
		String name=name(item);
		getBuilder(name).parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", modLoc("item/" + name));
	}

	private void createBucket(Fluid fluid) {
		getBuilder(fluid.getFilledBucket()).loader(forgeLoc("bucket")).additional("fluid", fluid.getRegistryName()).parent(new UncheckedModelFile(forgeLoc("item/bucket")));
	}

	private LoadedModelBuilder getBuilder(IItemProvider item) {
		return getBuilder(name(item));
	}

	private String name(IItemProvider item) {
		return item.asItem().getRegistryName().getPath();
	}

	protected ResourceLocation itLoc(String string) {
		return new ResourceLocation(ImmersiveEngineering.MODID, string);
	}

	protected ResourceLocation forgeLoc(String string) {
		return new ResourceLocation("forge", string);
	}

}
