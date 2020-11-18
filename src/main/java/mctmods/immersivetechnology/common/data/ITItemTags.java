package mctmods.immersivetechnology.common.data;

import mctmods.immersivetechnology.api.ITTags;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

public class ITItemTags extends ItemTagsProvider {

	@SuppressWarnings("deprecation")
	public ITItemTags(DataGenerator dataGenerator, BlockTagsProvider existingFileHelper) {
		super(dataGenerator, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(ITTags.Items.salt).add(ITContent.Items.salt);
	}

}
