package mctmods.immersivetechnology.common.data;

import mctmods.immersivetechnology.api.ITTags;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;

public class ITBlockTags extends BlockTagsProvider {

	@SuppressWarnings("deprecation")
	public ITBlockTags(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(ITTags.Blocks.fluidValve).add(ITContent.Blocks.fluidValve);
	}

}
