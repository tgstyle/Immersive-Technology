package mctmods.immersivetechnology.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.*;

public class MultiblockRenderSystem {

    public static class ITFakeTileEntity extends TileEntity {
        @Override
        public boolean shouldRenderInPass(int pass) {
            renderLayer(pass);
            return false;
        }
    }

    static Set<TileEntity> addCollection = Collections.singleton(new ITFakeTileEntity());

    static final Set<IMultiblockRender> toRender = new HashSet<>();
    static final RegionRenderCacheBuilder bufferCache = new RegionRenderCacheBuilder();
    static final ICamera icamera = new Frustum();
    static double x;
    static double y;
    static double z;

    static int tick = 0;

    public static void add(IMultiblockRender toAdd) {
        if (toRender.contains(toAdd)) return;
        toRender.add(toAdd);
    }

    public static void remove(IMultiblockRender toRemove) {
        toRender.remove(toRemove);
    }

    public static void clear() {
        toRender.clear();
    }

    static void renderLayer(int currentPass) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        if (entity == null) return;
        final double partialTicks = mc.getRenderPartialTicks();

        x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        float renderDistanceSq = mc.gameSettings.renderDistanceChunks * 16;
        renderDistanceSq *= renderDistanceSq;

        icamera.setPosition(x, y, z);

        /*if (currentPass == 0) {
            tick++;
            if (tick >= 20) {
                buildCache(mc);
                tick = 0;
            }
        }*/

        buildCache(mc);

        RenderHelper.disableStandardItemLighting();

        BlockRenderLayer blockRenderLayer = BlockRenderLayer.values()[currentPass];
        final BufferBuilder bufferBuilder = bufferCache.getWorldRendererByLayer(blockRenderLayer);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        drawBuffer(bufferBuilder);
        GlStateManager.popMatrix();

        for (IMultiblockRender iRender : toRender) {
            if (!iRender.canRenderDynamicInLayer(blockRenderLayer) ||
                    iRender.getBlockPos().distanceSq(x,y,z) > renderDistanceSq ||
                    !icamera.isBoundingBoxInFrustum(iRender.getRenderAABB()))
                continue;
            iRender.renderDynamic(
                    iRender.getBlockPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                    iRender.getBlockPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                    iRender.getBlockPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                    (float)partialTicks);
        }
    }

    public static void installHook(final RenderWorldLastEvent event) {
        Minecraft.getMinecraft().renderGlobal.updateTileEntities(new HashSet<>(),addCollection);
    }

    public static void buildCache(final Minecraft minecraft) {
        final IBlockAccess blockAccess = minecraft.world;
        final BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        float renderDistanceSq = (minecraft.gameSettings.renderDistanceChunks+1) * 16;
        renderDistanceSq *= renderDistanceSq;
        icamera.setPosition(x, y, z);

        for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
            final BufferBuilder bufferBuilder = bufferCache.getWorldRendererByLayer(blockRenderLayer);
            ForgeHooksClient.setRenderLayer(blockRenderLayer);
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            Iterator<IMultiblockRender> iterator = toRender.iterator();
            while (iterator.hasNext()) {
                IMultiblockRender iRender = iterator.next();
                if (!iRender.isStillValid(blockAccess)) {
                    iterator.remove();
                    continue;
                }
                else if (!iRender.canRenderInLayer(blockRenderLayer) ||
                        !iRender.isPlayerInSameWorld() ||
                        iRender.getBlockPos().distanceSq(x,y,z) > renderDistanceSq ||
                        !icamera.isBoundingBoxInFrustum(iRender.getRenderAABB())) continue;
                renderQuadsFlat(blockAccess, iRender, bufferBuilder, blockRendererDispatcher);
            }
            bufferBuilder.finishDrawing();
        }
        ForgeHooksClient.setRenderLayer(null);
    }

    private static void drawBuffer(final BufferBuilder bufferBuilderIn) {
        if (bufferBuilderIn.getVertexCount() == 0) return;
        VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
        int i = vertexformat.getSize();
        ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
        List<VertexFormatElement> list = vertexformat.getElements();

        for (int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexformatelement = list.get(j);
            bytebuffer.position(vertexformat.getOffset(j));
            vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
        }

        GlStateManager.glDrawArrays(bufferBuilderIn.getDrawMode(), 0, bufferBuilderIn.getVertexCount());
        int i1 = 0;

        for (int j1 = list.size(); i1 < j1; ++i1) {
            VertexFormatElement vertexformatelement1 = list.get(i1);
            vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
        }
    }

    private static void renderQuadsFlat(IBlockAccess blockAccessIn, IMultiblockRender te, BufferBuilder buffer, BlockRendererDispatcher dispatcher)  {
        BlockPos posIn = te.getBlockPos();
        IBlockState stateIn = te.renderState().getActualState(blockAccessIn, posIn);
        long rand = MathHelper.getPositionRandom(posIn);
        List<BakedQuad> list = dispatcher.getModelForState(stateIn).getQuads(stateIn, null, rand);

        Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
        double d0 = (double)posIn.getX() + vec3d.x;
        double d1 = (double)posIn.getY() + vec3d.y;
        double d2 = (double)posIn.getZ() + vec3d.z;

        int lightmap = stateIn.getPackedLightmapCoords(blockAccessIn, posIn);
        int k = Minecraft.getMinecraft().getBlockColors().getColor(stateIn, (World) blockAccessIn, posIn);
        for (int i = 0; i < list.size(); ++i) {
            BakedQuad bakedquad = list.get(i);
            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(lightmap, lightmap, lightmap, lightmap);

            if (EntityRenderer.anaglyphEnable) k = TextureUtil.anaglyphColor(k);

            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;

            buffer.putColorMultiplier(f, f1, f2, 4);
            buffer.putColorMultiplier(f, f1, f2, 3);
            buffer.putColorMultiplier(f, f1, f2, 2);
            buffer.putColorMultiplier(f, f1, f2, 1);

            buffer.putPosition(d0, d1, d2);
        }
    }
}
