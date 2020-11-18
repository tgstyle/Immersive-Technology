package mctmods.immersivetechnology.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class FuidValveBlock extends ITBlockBase {

	public FuidValveBlock() {
		super("fluidvalve", Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F, 10.0F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
	}

}
