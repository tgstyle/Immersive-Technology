package mctmods.immersivetechnology.common.blocks;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IIEMetaBlock;
import com.google.common.collect.Sets;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

	/*
	* @author BluSunrize
	*/

@SuppressWarnings("rawtypes")
public class BlockITBase<E extends Enum<E> & BlockITBase.IBlockEnum> extends Block implements IIEMetaBlock {
	protected static IProperty[] tempProperties;
	protected static IUnlistedProperty[] tempUnlistedProperties;
	public final String name;
	public final PropertyEnum<E> property;
	public final IProperty[] additionalProperties;
	public final IUnlistedProperty[] additionalUnlistedProperties;
	public final E[] enumValues;
	boolean[] isMetaHidden;
	boolean[] hasFlavour;
	protected Set<BlockRenderLayer> renderLayers = Sets.newHashSet(BlockRenderLayer.SOLID);
	protected Set<BlockRenderLayer>[] metaRenderLayers;
	protected Map<Integer, Integer> metaLightOpacities = new HashMap<>();
	protected Map<Integer, Float> metaHardness = new HashMap<>();
	protected Map<Integer, Integer> metaResistances = new HashMap<>();
	protected EnumPushReaction[] metaMobilityFlags;
	protected boolean[] canHammerHarvest;
	protected boolean[] metaNotNormalBlock;
	private boolean opaqueCube = false;

	@SuppressWarnings("unchecked")
	public BlockITBase(String name, Material material, PropertyEnum<E> mainProperty, Class<? extends ItemBlockITBase> itemBlock, Object... additionalProperties) {
		super(setTempProperties(material, mainProperty, additionalProperties));
		this.name = name;
		this.property = mainProperty;
		this.enumValues = mainProperty.getValueClass().getEnumConstants();
		this.isMetaHidden = new boolean[this.enumValues.length];
		this.hasFlavour = new boolean[this.enumValues.length];
		this.metaRenderLayers = new Set[this.enumValues.length];
		this.canHammerHarvest = new boolean[this.enumValues.length];
		this.metaMobilityFlags = new EnumPushReaction[this.enumValues.length];

		ArrayList<IProperty> propList = new ArrayList<>();
		ArrayList<IUnlistedProperty> unlistedPropList = new ArrayList<>();
		for(Object o : additionalProperties) {
			if(o instanceof IProperty) propList.add((IProperty) o);
			if(o instanceof IProperty[]) {
				propList.addAll(Arrays.asList(((IProperty[]) o)));
			}
			if(o instanceof IUnlistedProperty) unlistedPropList.add((IUnlistedProperty) o);
			if(o instanceof IUnlistedProperty[]) {
				unlistedPropList.addAll(Arrays.asList(((IUnlistedProperty[]) o)));
			}
		}
		this.additionalProperties = propList.toArray(new IProperty[propList.size()]);
		this.additionalUnlistedProperties = unlistedPropList.toArray(new IUnlistedProperty[unlistedPropList.size()]);
		this.setDefaultState(getInitDefaultState());
		String registryName = createRegistryName();
		this.setUnlocalizedName(registryName.replace(':', '.'));
		this.setCreativeTab(ImmersiveTechnology.creativeTab);
		this.adjustSound();
		ITContent.registeredITBlocks.add(this);
		try {
			ITContent.registeredITItems.add(itemBlock.getConstructor(Block.class).newInstance(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		lightOpacity = 255;
	}

	@Override
	public String getIEBlockName() {
		return this.name;
	}

	@Override
	public Enum[] getMetaEnums() {
		return enumValues;
	}

	@Override
	public IBlockState getInventoryState(int meta) {
		return getStateFromMeta(meta);
	}

	@Override
	public PropertyEnum<E> getMetaProperty() {
		return this.property;
	}

	@Override
	public boolean useCustomStateMapper() {
		return false;
	}

	@Override
	public String getCustomStateMapping(int meta, boolean itemBlock) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public StateMapperBase getCustomMapper() {
		return null;
	}

	@Override
	public boolean appendPropertiesToState() {
		return true;
	}

	public String getTranslationKey(ItemStack stack) {
		String subName = getStateFromMeta(stack.getItemDamage()).getValue(property).toString().toLowerCase(Locale.US);
		return super.getUnlocalizedName() + "." + subName;
	}

	protected static Material setTempProperties(Material material, PropertyEnum<?> property, Object... additionalProperties) {
		ArrayList<IProperty> propList = new ArrayList<>();
		ArrayList<IUnlistedProperty> unlistedPropList = new ArrayList<>();
		propList.add(property);
		for(Object o : additionalProperties) {
			if(o instanceof IProperty) propList.add((IProperty) o);
			if(o instanceof IProperty[]) {
				propList.addAll(Arrays.asList(((IProperty[]) o)));
			}
			if(o instanceof IUnlistedProperty) unlistedPropList.add((IUnlistedProperty) o);
			if(o instanceof IUnlistedProperty[]) {
				unlistedPropList.addAll(Arrays.asList(((IUnlistedProperty[]) o)));
			}
		}
		tempProperties = propList.toArray(new IProperty[propList.size()]);
		tempUnlistedProperties = unlistedPropList.toArray(new IUnlistedProperty[unlistedPropList.size()]);
		return material;
	}

	protected static Object[] combineProperties(Object[] currentProperties, Object... addedProperties) {
		Object[] array = new Object[currentProperties.length + addedProperties.length];
		for(int i = 0; i < currentProperties.length; i++) array[i] = currentProperties[i];
		for(int i = 0; i < addedProperties.length; i++) array[currentProperties.length + i] = addedProperties[i];
		return array;
	}

	public BlockITBase<?> setMetaHidden(int... meta) {
		for(int i : meta) if(i >= 0 && i < this.isMetaHidden.length) this.isMetaHidden[i] = true;
		return this;
	}

	public BlockITBase<?> setMetaUnhidden(int... meta) {
		for(int i : meta) if(i >= 0 && i < this.isMetaHidden.length) this.isMetaHidden[i] = false;
		return this;
	}

	public boolean isMetaHidden(int meta) {
		return this.isMetaHidden[Math.max(0, Math.min(meta, this.isMetaHidden.length - 1))];
	}

	public BlockITBase<?> setHasFlavour(int... meta) {
		if(meta == null || meta.length < 1) {
			for(int i = 0; i < hasFlavour.length; i++) this.hasFlavour[i] = true;
		} else {
			for(int i : meta) if(i >= 0 && i < this.hasFlavour.length) this.hasFlavour[i] = false;
		}
		return this;
	}

	public boolean hasFlavour(ItemStack stack) {
		return this.hasFlavour[Math.max(0, Math.min(stack.getItemDamage(), this.hasFlavour.length - 1))];
	}

	public BlockITBase<E> setBlockLayer(BlockRenderLayer... layer) {
		this.renderLayers = Sets.newHashSet(layer);
		return this;
	}

	public BlockITBase<E> setMetaBlockLayer(int meta, BlockRenderLayer... layer) {
		this.metaRenderLayers[Math.max(0, Math.min(meta, this.metaRenderLayers.length - 1))] = Sets.newHashSet(layer);
		return this;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		int meta = this.getMetaFromState(state);
		if(meta >= 0 && meta < metaRenderLayers.length&&metaRenderLayers[meta]!=null) return metaRenderLayers[meta].contains(layer);
		return renderLayers.contains(layer);
	}

	public BlockITBase<E> setMetaLightOpacity(int meta, int opacity) {
		metaLightOpacities.put(meta, opacity);
		return this;
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess w, BlockPos pos) {
		int meta = getMetaFromState(state);
		if(metaLightOpacities.containsKey(meta)) return metaLightOpacities.get(meta);
		return super.getLightOpacity(state, w, pos);
	}

	public BlockITBase<E> setMetaHardness(int meta, float hardness) {
		metaHardness.put(meta, hardness);
		return this;
	}

	/**
	* @deprecated call via {@link IBlockState#getBlockHardness(World,BlockPos)} whenever possible. Implementing/overriding
	* is fine.
	**/
	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		int meta = getMetaFromState(state);
		if(metaHardness.containsKey(meta))
			return metaHardness.get(meta);
		return super.getBlockHardness(state, world, pos);
	}

	public BlockITBase<E> setMetaExplosionResistance(int meta, int resistance) {
		metaResistances.put(meta, resistance);
		return this;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		int meta = getMetaFromState(world.getBlockState(pos));
		if(metaResistances.containsKey(meta)) return metaResistances.get(meta);
		return super.getExplosionResistance(world, pos, exploder, explosion);
	}

	public BlockITBase<E> setMetaMobilityFlag(int meta, EnumPushReaction flag) {
		metaMobilityFlags[meta] = flag;
		return this;
	}

	/**
	* @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
	**/
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		int meta = getMetaFromState(state);
		if(metaMobilityFlags[meta]==null) return EnumPushReaction.NORMAL;
		return metaMobilityFlags[meta];
	}

	public BlockITBase<E> setNotNormalBlock(int meta) {
		if(metaNotNormalBlock == null) metaNotNormalBlock = new boolean[this.enumValues.length];
		metaNotNormalBlock[meta] = true;
		return this;
	}

	public BlockITBase<E> setAllNotNormalBlock() {
		if(metaNotNormalBlock == null) metaNotNormalBlock = new boolean[this.enumValues.length];
		for(int i = 0; i < metaNotNormalBlock.length; i++) metaNotNormalBlock[i] = true;
		return this;
	}

	protected boolean normalBlockCheck(IBlockState state) {
		if(metaNotNormalBlock == null) return true;
		int meta = getMetaFromState(state);
		return (meta < 0 || meta >= metaNotNormalBlock.length) || !metaNotNormalBlock[meta];
	}

	/**
	* @return true if the state occupies all of its 1x1x1 cube
	* @deprecated prefer calling {@link IBlockState#isFullBlock()}
	**/
	@Override
	public boolean isFullBlock(IBlockState state) {
		return normalBlockCheck(state);
	}

	/**
	* @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
	**/
	@Override
	public boolean isFullCube(IBlockState state) {
		return normalBlockCheck(state);
	}

	/**
	* Used to determine ambient occlusion and culling when rebuilding chunks forrender
	* @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
	**/
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return normalBlockCheck(state);
	}

	/**
	* @deprecated call via {@link IBlockState#causesSuffocation()} whenever possible. Implementing/overriding is fine.
	**/
	@Override
	public boolean causesSuffocation(IBlockState state)	{
		if(metaNotNormalBlock==null) return true;
		int majority = 0;
		for(boolean b : metaNotNormalBlock) {
			if(b) {
				majority++;
			}
		}
		return majority < metaNotNormalBlock.length / 2;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return normalBlockCheck(state);
	}

	protected BlockStateContainer createNotTempBlockState() {
		IProperty[] array = new IProperty[1 + this.additionalProperties.length];
		array[0] = this.property;
		for(int i = 0; i < this.additionalProperties.length; i++) array[1 + i] = this.additionalProperties[i];
		if(this.additionalUnlistedProperties.length > 0) return new ExtendedBlockState(this, array, additionalUnlistedProperties);
		return new BlockStateContainer(this, array);
	}

	@SuppressWarnings("unchecked")
	protected IBlockState getInitDefaultState() {
		IBlockState state = this.blockState.getBaseState().withProperty(this.property, enumValues[0]);
		for(int i = 0; i < this.additionalProperties.length; i++) {
			if(this.additionalProperties[i] != null && !this.additionalProperties[i].getAllowedValues().isEmpty()) {
				state = applyProperty(state, additionalProperties[i], additionalProperties[i].getAllowedValues().iterator().next());
			}
		}
		return state;
	}

	@SuppressWarnings("unchecked")
	protected <V extends Comparable<V>> IBlockState applyProperty(IBlockState in, IProperty<V> prop, Object val) {
		return in.withProperty(prop, (V) val);
	}

	public void onITBlockPlacedBy(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {
	}

	public boolean canITBlockBePlaced(World world, BlockPos pos, IBlockState newState, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if(this.property != null) return createNotTempBlockState();
		if(tempUnlistedProperties.length > 0) return new ExtendedBlockState(this, tempProperties, tempUnlistedProperties);
		return new BlockStateContainer(this, tempProperties);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if(state == null || !this.equals(state.getBlock())) return 0;
		return state.getValue(this.property).getMeta();
	}

	protected E fromMeta(int meta) {
		if(meta < 0 || meta >= enumValues.length)
			meta = 0;
		return enumValues[meta];
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(this.property, fromMeta(meta));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(E type : this.enumValues) if(type.listForCreative() && !this.isMetaHidden[type.getMeta()]) list.add(new ItemStack(this, 1, type.getMeta()));
	}

	void adjustSound() {
		if(this.blockMaterial == Material.ANVIL) this.blockSoundType = SoundType.ANVIL;
		else if(this.blockMaterial == Material.CARPET || this.blockMaterial == Material.CLOTH) this.blockSoundType = SoundType.CLOTH;
		else if(this.blockMaterial == Material.GLASS || this.blockMaterial == Material.ICE) this.blockSoundType = SoundType.GLASS;
		else if(this.blockMaterial == Material.GRASS || this.blockMaterial == Material.TNT || this.blockMaterial == Material.PLANTS || this.blockMaterial == Material.VINE) this.blockSoundType = SoundType.PLANT;
		else if(this.blockMaterial == Material.GROUND) this.blockSoundType = SoundType.GROUND;
		else if(this.blockMaterial == Material.IRON) this.blockSoundType = SoundType.METAL;
		else if(this.blockMaterial == Material.SAND) this.blockSoundType = SoundType.SAND;
		else if(this.blockMaterial == Material.SNOW) this.blockSoundType = SoundType.SNOW;
		else if(this.blockMaterial == Material.ROCK) this.blockSoundType = SoundType.STONE;
		else if(this.blockMaterial == Material.WOOD || this.blockMaterial == Material.CACTUS) this.blockSoundType = SoundType.WOOD;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
		if(worldIn.isRemote && eventID == 255) {
			worldIn.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		return super.eventReceived(state, worldIn, pos, eventID, eventParam);
	}

	public BlockITBase<E> setMetaHammerHarvest(int meta) {
		canHammerHarvest[meta] = true;
		return this;
	}

	public BlockITBase<E> setHammerHarvest() {
		for(int i = 0; i < metaNotNormalBlock.length; i++)
			canHammerHarvest[i] = true;
		return this;
	}

	public boolean allowHammerHarvest(IBlockState blockState) {
		int meta = getMetaFromState(blockState);
		if(meta >= 0 && meta < canHammerHarvest.length) return canHammerHarvest[meta];
		return false;
	}

	public boolean allowWirecutterHarvest(IBlockState blockState) {
		return false;
	}

	public boolean isOpaqueCube() {
		return opaqueCube;
	}

	public BlockITBase<E> setOpaque(boolean isOpaque) {
		opaqueCube = isOpaque;
		fullBlock = isOpaque;
		return this;
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		if(allowHammerHarvest(state) && type.equals(Lib.TOOL_HAMMER)) return true;
		if(allowWirecutterHarvest(state) && type.equals(Lib.TOOL_WIRECUTTER)) return true;
		return super.isToolEffective(type, state);
	}

	public String createRegistryName() {
		return ImmersiveTechnology.MODID + ":" + name;
	}

	public interface IBlockEnum extends IStringSerializable {
		int getMeta();
		boolean listForCreative();
	}

	public abstract static class IPLadderBlock<E extends Enum<E> & IBlockEnum> extends BlockITBase<E> {
		public IPLadderBlock(String name, Material material, PropertyEnum<E> mainProperty, Class<? extends ItemBlockITBase> itemBlock, Object... additionalProperties) {
			super(name, material, mainProperty, itemBlock, additionalProperties);
		}

		@Override
		public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
			super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
			if(entityIn instanceof EntityLivingBase && !((EntityLivingBase) entityIn).isOnLadder() && isLadder(state, worldIn, pos, (EntityLivingBase) entityIn)) {
				float f5 = 0.15F;
				if(entityIn.motionX < -f5) entityIn.motionX = -f5;
				if(entityIn.motionX > f5) entityIn.motionX = f5;
				if(entityIn.motionZ < -f5) entityIn.motionZ = -f5;
				if(entityIn.motionZ > f5) entityIn.motionZ = f5;
				entityIn.fallDistance = 0.0F;
				if(entityIn.motionY < -0.15D) entityIn.motionY = -0.15D;
				if(entityIn.motionY < 0 && entityIn instanceof EntityPlayer && entityIn.isSneaking()) {
					entityIn.motionY = .05;
					return;
				}
				if(entityIn.collidedHorizontally) entityIn.motionY = .2;
			}
		}
	}

}