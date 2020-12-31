package mctmods.immersivetechnology.common.blocks.metal.types;

import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.common.blocks.BlockITBase;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;

public enum BlockType_MetalMultiblock implements IStringSerializable, BlockITBase.IBlockEnum {

	DISTILLER(ITUtils.appendModName("multiblocks/distiller"), x -> new TileEntityDistillerMaster()),
	SOLAR_TOWER(ITUtils.appendModName("multiblocks/solar_tower"), x -> new TileEntitySolarTowerMaster()),
	SOLAR_REFLECTOR(ITUtils.appendModName("multiblocks/solar_reflector"), x -> new TileEntitySolarReflectorMaster()),
	STEAM_TURBINE(ITUtils.appendModName("multiblocks/steam_turbine"), x -> new TileEntitySteamTurbineMaster()),
	BOILER(ITUtils.appendModName("multiblocks/boiler"), x -> new TileEntityBoilerMaster()),
	ALTERNATOR(ITUtils.appendModName("multiblocks/alternator"), x -> new TileEntityAlternatorMaster()),
	DISTILLER_SLAVE(ITUtils.appendModName("multiblocks/distiller"), x -> new TileEntityDistillerSlave()),
	SOLAR_TOWER_SLAVE(ITUtils.appendModName("multiblocks/solar_tower"), x -> new TileEntitySolarTowerSlave()),
	STEAM_TURBINE_SLAVE(ITUtils.appendModName("multiblocks/steam_turbine"), x -> new TileEntitySteamTurbineSlave()),
	BOILER_SLAVE(ITUtils.appendModName("multiblocks/boiler"), x -> new TileEntityBoilerSlave()),
	ALTERNATOR_SLAVE(ITUtils.appendModName("multiblocks/alternator"), x -> new TileEntityAlternatorSlave()),
	SOLAR_REFLECTOR_SLAVE(ITUtils.appendModName("multiblocks/solar_reflector"), x -> new TileEntitySolarReflectorSlave()),
	STEEL_TANK(ITUtils.appendModName("multiblocks/steel_tank"), x -> new TileEntitySteelSheetmetalTankMaster()),
	STEEL_TANK_SLAVE(ITUtils.appendModName("multiblocks/steel_tank"), x -> new TileEntitySteelSheetmetalTankSlave()),
	COOLING_TOWER(ITUtils.appendModName("multiblocks/cooling_tower"), x -> new TileEntityCoolingTowerMaster()),
	COOLING_TOWER_SLAVE(ITUtils.appendModName("multiblocks/cooling_tower"), x -> new TileEntityCoolingTowerSlave());

	private final String path;
	private final Function<?, TileEntity> func;

	BlockType_MetalMultiblock(String path, Function<?, TileEntity> func) {
		this.path = path;
		this.func = func;
	}

	public TileEntity createTE() {
		return func.apply(null);
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	@Override
	public boolean listForCreative() {
		return false;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase(Locale.ENGLISH);
	}

	public String getPath() {
		return path;
	}
}