package mctmods.immersivetechnology.client;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import mctmods.immersivetechnology.common.blocks.BlockITMultiblock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ITCustomStateMapper extends StateMapperBase {
    public static HashMap<String, StateMapperBase> stateMappers = new HashMap<>();

    public static StateMapperBase getStateMapper(IEBlockInterfaces.IIEMetaBlock metaBlock) {
        String key = metaBlock.getIEBlockName();
        StateMapperBase mapper = stateMappers.get(key);
        if (mapper == null) {
            mapper = metaBlock.getCustomMapper();
            if (mapper == null) mapper = new ITCustomStateMapper();
            stateMappers.put(key, mapper);
        }
        return mapper;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        try {
            String prop;
            ResourceLocation rl;
            if (state.getBlock() instanceof BlockITMultiblock) {
                BlockITMultiblock<?> block = (BlockITMultiblock<?>)state.getBlock();
                prop = getPropertyString(state.getProperties());
                rl = new ResourceLocation(block.enumValues[state.getBlock().getMetaFromState(state)].getPath());
            } else {
                rl = Block.REGISTRY.getNameForObject(state.getBlock());
                IEBlockInterfaces.IIEMetaBlock metaBlock = (IEBlockInterfaces.IIEMetaBlock)state.getBlock();
                String custom = metaBlock.getCustomStateMapping(state.getBlock().getMetaFromState(state), false);
                if (custom != null) rl = new ResourceLocation(rl.toString() + "_" + custom);
                prop = metaBlock.appendPropertiesToState() ? this.getPropertyString(state.getProperties()) : null;
            }
            return new ModelResourceLocation(rl, prop);
        } catch (Exception var6) {
            var6.printStackTrace();
            ResourceLocation rl = Block.REGISTRY.getNameForObject(state.getBlock());
            return new ModelResourceLocation(rl, this.getPropertyString(state.getProperties()));
        }
    }
}