package mctmods.immersivetechnology.common.blocks;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraft.fluid.Fluid;

	/*
	@author BluSunrize
	*/
public class BlockITFluid extends BlockFluidClassic {
	private int flammability = 0;
	private int fireSpread = 0;
	private PotionEffect[] potionEffects;

	public BlockITFluid(String name, Fluid fluid, Material material) {
		super(fluid, material);
		this.setUnlocalizedName(ImmersiveTechnology.MODID + "." + name);
		this.setCreativeTab(ImmersiveTechnology.creativeTab);
		ITContent.registeredITBlocks.add(this);
	}

	public BlockITFluid setFlammability(int flammability, int fireSpread) {
		this.flammability = flammability;
		this.fireSpread = fireSpread;
		return this;
	}

	public BlockITFluid setPotionEffects(PotionEffect... potionEffects) {
		this.potionEffects = potionEffects;
		return this;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, Direction face) {
		return this.flammability;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, Direction face) {
		return fireSpread;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, Direction face) {
		return this.flammability > 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity) {
		if(potionEffects != null && entity instanceof EntityLivingBase) for(PotionEffect effect : potionEffects) if(effect != null) ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(effect));
	}

}