package mctmods.immersivetechnology.common.blocks.connectors.tileentities;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGuiTile;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConnectorRedstone;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.api.ITLib;
import mctmods.immersivetechnology.common.util.network.MessageTileSync;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTimer extends TileEntityConnectorRedstone implements IGuiTile {
	private Direction face;
	private BlockPos position;

	private int redstoneChannelsending = 0;
	private int lastOutput = 0;
	private int target = 40;
	private int tick = 1;

	private final int maxTarget = 600;
	private final int minTarget = 10;

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		nbt.putInt("redstoneChannelsending", redstoneChannelsending);
		nbt.putInt("target", target);
		nbt.putInt("tick", tick);
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		redstoneChannelsending = nbt.getInt("redstoneChannelsending");
		target = nbt.getInt("target");
		tick = nbt.getInt("tick");
	}

	public int getTarget() {
		return this.target;
	}

	private void setTarget(int increment) {
		if(increment < 0) {
			if(target != minTarget) {
				if(target < 200 && target > 100) {
					this.target -= 20;
				} else if(target < 100) {
					this.target -= 10;
				} else {
					this.target -= 40;
				}
			}
		} else if(increment > 0) {
			if(target != maxTarget) {
				if(target < 200 && target > 100) {
					this.target += 20;
				} else if(target < 100) {
					this.target += 10;
				} else {
					this.target += 40;
				}
			}
		}
		tick = 1;
	}

	private boolean stopTimer(BlockPos pos) {
		if(world.isSidePowered(pos, face)) return true;
		return false;
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(face == null) face = facing == Direction.SOUTH ? Direction.UP : facing == Direction.NORTH ? Direction.DOWN : facing == Direction.WEST ? Direction.NORTH : Direction.SOUTH;
			if(position == null) position = this.getPos().offset(face);
			if(!stopTimer(position)) {
				if(tick == target) {
					this.lastOutput = 1;
					this.tick = 1;
					this.rsDirty = true;
				} else {
					this.tick++;
					if(this.lastOutput == 1) {
						this.lastOutput = 0;
					}
				}
			}
		}
		super.update();
	}

	@Override
	public boolean isRSInput() {
		return true;
	}

	@Override
	public boolean isRSOutput() {
		return true;
	}

	@Override
	public void updateInput(byte[] signals) {
		signals[redstoneChannelsending] = (byte) Math.max(lastOutput, signals[redstoneChannelsending]);
		rsDirty = false;
	}

	@Override
	public boolean hammerUseSide(Direction side, PlayerEntity player, float hitX, float hitY, float hitZ) {
		redstoneChannelsending = (redstoneChannelsending + 1) % 16;
		markDirty();
		wireNetwork.updateValues();
		onChange();
		this.markContainingBlockForUpdate(null);
		world.addBlockEvent(getPos(), this.getBlockType(), 254, 0);
		return true;
	}

	@Override
	public void receiveMessageFromClient(CompoundNBT message) {
		if(!message.hasKey("buttonId")) return;
		int id = message.getInteger("buttonId");
		setTarget(id == 0 ? 1 : - 1);
		markDirty();
		CompoundNBT tag = new CompoundNBT();
		tag.setInteger("target", target);
		BlockPos center = getPos();
		ImmersiveTechnology.packetHandler.sendToAllTracking(new MessageTileSync(this, tag), new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 0));
	}

	@Override
	public void receiveMessageFromServer(CompoundNBT message) {
		if(!message.hasKey("target")) return;
		target = message.getInteger("target");
	}

	@Override
	public Vec3d getRaytraceOffset(IImmersiveConnectable link) {
		Direction side = facing.getOpposite();
		return new Vec3d(.5 + side.byIndexOffsetX() * .375, .5 + side.byIndexOffsetY() * .375, .5 + side.byIndexOffsetZ() * .375);
	}

	@Override
	public Vec3d getConnectionOffset(Connection con) {
		Direction side = facing.getOpposite();
		double conRadius = con.cableType.getRenderDiameter() / 2;
		return new Vec3d(.5 + side.byIndexOffsetX() * (.375 - conRadius), .5 + side.byIndexOffsetY() * (.375 - conRadius), .5 + side.byIndexOffsetZ() * (.375 - conRadius));
	}

	@Override
	public String getCacheKey(BlockState object) {
		return redstoneChannel + ";" + redstoneChannelsending;
	}

	@Override
	public String[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
		if(!hammer)	return null;
		float time = (float) this.target / 20;
		return new String[] {I18n.format(Lib.DESC_INFO + "redstoneChannel.send", I18n.format("item.fireworksCharge." + DyeColor.byMetadata(redstoneChannelsending).getUnlocalizedName())), I18n.format(String.valueOf(time).toString() + " Sec.")};
	}

	@Override
	public boolean useNixieFont(PlayerEntity player, RayTraceResult mop) {
		return false;
	}

	@Override
	public boolean canOpenGui() {
		return true;
	}

	@Override
	public int getGuiID() {
		return ITLib.GUIID_Timer;
	}

	@Override
	public TileEntity getGuiMaster() {
		return this;
	}

	@Override
	public float[] getBlockBounds() {
		switch(facing) {
		case NORTH:
			return new float[] {.25f, 0, 0, .75f, .75f, 1};
		case SOUTH:
			return new float[] {.25f, .25f, 0, .75f, 1, 1};
		case EAST:
			return new float[] {0, .25f, .25f, 1, .75f, 1};
		case WEST:
			return new float[] {0, .25f, 0, 1, .75f, .75f};
		default:
			return new float[] {.25f, 0, .25f, .75f, 1, 1};
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getTextureReplacement(BlockState object, String material) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldRenderGroup(BlockState object, String group) {
		if(MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) return false;
		if("glass".equals(group)) return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColour(BlockState object, String group) {
		if("colour_out".equals(group)) return 0xff000000 | DyeColor.byMetadata(this.redstoneChannelsending).getColorValue();
		return 0xffffffff;
	}

}