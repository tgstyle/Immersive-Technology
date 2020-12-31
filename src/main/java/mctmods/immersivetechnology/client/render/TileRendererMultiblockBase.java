package mctmods.immersivetechnology.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public abstract class TileRendererMultiblockBase<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    public TileRendererMultiblockBase() {
        setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }
}
