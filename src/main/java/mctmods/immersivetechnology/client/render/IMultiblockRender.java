package mctmods.immersivetechnology.client.render;

import mctmods.immersivetechnology.common.util.multiblock.IMultipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMultiblockRender extends IMultipart {

    @SideOnly(Side.CLIENT)
    default void renderDynamic(double x, double y, double z, float partialTicks) {}

    @SideOnly(Side.CLIENT)
    default boolean canRenderDynamicInLayer(BlockRenderLayer layer) { return false; }

    @SideOnly(Side.CLIENT)
    boolean canRenderInLayer(BlockRenderLayer layer);

    @SideOnly(Side.CLIENT)
    IBlockState renderState();

    @SideOnly(Side.CLIENT)
    int getLightLevel();

    default boolean isStillValid(IBlockAccess world) {
        return world.getTileEntity(getBlockPos()) == This();
    }

    default BlockPos getBlockPos() {
        return This().getPos();
    }
    
    AxisAlignedBB getRenderAABB();

    @SideOnly(Side.CLIENT)
    default void registerRenderer() {
        if (isPlayerInSameWorld()) MultiblockRenderSystem.add(this);
    }

    @SideOnly(Side.CLIENT)
    default boolean isPlayerInSameWorld() {
        return This().getWorld() == Minecraft.getMinecraft().player.world;
    }
}
