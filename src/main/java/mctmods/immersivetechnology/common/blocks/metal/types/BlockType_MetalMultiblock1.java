package mctmods.immersivetechnology.common.blocks.metal.types;

import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.common.blocks.BlockITBase;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityGasTurbineMaster;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityGasTurbineSlave;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityHeatExchangerMaster;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityHeatExchangerSlave;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;

public enum BlockType_MetalMultiblock1 implements IStringSerializable, BlockITBase.IBlockEnum {

    GAS_TURBINE(ITUtils.appendModName("multiblocks/gas_turbine"), x -> new TileEntityGasTurbineMaster()),
    GAS_TURBINE_SLAVE(ITUtils.appendModName("multiblocks/gas_turbine"), x -> new TileEntityGasTurbineSlave()),
    HEAT_EXCHANGER(ITUtils.appendModName("multiblocks/heat_exchanger"), x -> new TileEntityHeatExchangerMaster()),
    HEAT_EXCHANGER_SLAVE(ITUtils.appendModName("multiblocks/heat_exchanger"), x -> new TileEntityHeatExchangerSlave());

    private final String path;
    private final Function<?, TileEntity> func;

    BlockType_MetalMultiblock1(String path, Function<?, TileEntity> func) {
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
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ENGLISH);
    }

}