package mctmods.immersivetechnology.common.data;

import mctmods.immersivetechnology.api.ITTags;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;

public class ITFluidTags extends FluidTagsProvider {

	@SuppressWarnings("deprecation")
	public ITFluidTags(DataGenerator gen) {
		super(gen);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(ITTags.Fluids.steam).add(ITContent.Fluids.steam);
	}

}
