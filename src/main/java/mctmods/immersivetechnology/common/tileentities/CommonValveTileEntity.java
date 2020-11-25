package mctmods.immersivetechnology.common.tileentities;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.util.TranslationKey;
import mctmods.immersivetechnology.common.util.network.MessageTileSync;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class CommonValveTileEntity extends IEBaseTileEntity implements IEBlockInterfaces.IDirectionalTile, ITickableTileEntity,
		IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IGuiTile {

	final TranslationKey overlayNormal;
	final TranslationKey overlaySneakingFirstLine;
	final TranslationKey overlaySneakingSecondLine;
	final int GuiID;

	public CommonValveTileEntity(TranslationKey overlayNormal, TranslationKey overlaySneakingFirstLine, TranslationKey overlaySneakingSecondLine, int GuiID) {
		this.overlayNormal = overlayNormal;
		this.overlaySneakingFirstLine = overlaySneakingFirstLine;
		this.overlaySneakingSecondLine = overlaySneakingSecondLine;
		this.GuiID = GuiID;
	}

	public Direction facing = Direction.NORTH;

	public int packetLimit = -1;
	public int timeLimit = -1;
	public int keepSize = -1;
	public byte redstoneMode = 0;

	public long acceptedAmount;
	public long lastAcceptedAmount;
	public int secondCounter;
	public int minuteCounter;
	public long average;
	public long lastAverage;
	public int packets;
	public int packetAverage;
	public int lastPacketAverage;

	public long[] averages = new long[60];
	public long[] packetTotals = new long[60];

	public void efficientMarkDirty() {//!!!!!!! only use it within update() function !!!!!!!
		world.getChunkAt(this.getPos()).markDirty();
	}

	public void calculateAverages() {
		long sum = 0;
		for(long avg : averages) sum += avg;
		average = sum / 60;
		sum = 0;
		for(long avg : packetTotals) sum += avg;
		packetAverage = (int)sum;
	}

	@Override
	public void tick() {
		if(world.isRemote) return;
		efficientMarkDirty();
		if(++secondCounter < 20) return;
		if(average == 0 && acceptedAmount > 0) {//pre-populate averages to avoid slow build up
			for(int i = 0; i < 60; i++) averages[i] = acceptedAmount;
			packetTotals[minuteCounter] = packets;
			calculateAverages();
		}
		if(averages[minuteCounter] != acceptedAmount || packetTotals[minuteCounter] != packets) {
			averages[minuteCounter] = acceptedAmount;
			packetTotals[minuteCounter] = packets;
			calculateAverages();
		}
		if(lastAverage != average || lastPacketAverage != packetAverage) notifyNearbyClients(new CompoundNBT());
		lastAcceptedAmount = acceptedAmount;
		acceptedAmount = 0;
		packets = 0;
		secondCounter = 0;
		if(++minuteCounter == 60) {
			lastPacketAverage = packetAverage;
			lastAverage = average;
			minuteCounter = 0;
		}
	}

	@Override
	public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && !Utils.isHammer(heldItem)) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("packetLimit", packetLimit);
			tag.putInt("timeLimit", timeLimit);
			tag.putInt("keepSize", keepSize);
			ImmersiveTechnology.packetHandler.sendTo(new MessageTileSync(this, tag), (ServerPlayerEntity) player);
			return true;
		} else if(player.isSneaking() && Utils.isHammer(heldItem)) {
			if(++redstoneMode > 2) redstoneMode = 0;
			String translationKey;
			switch(redstoneMode) {
				case 1: translationKey = TranslationKey.OVERLAY_REDSTONE_NORMAL.location; break;
				case 2: translationKey = TranslationKey.OVERLAY_REDSTONE_INVERTED.location; break;
				default: translationKey = TranslationKey.OVERLAY_REDSTONE_OFF.location;
			}
			ChatUtils.sendServerNoSpamMessages(player, new TranslationTextComponent(translationKey));
			efficientMarkDirty();
			return true;
		}
		return false;
	}

	@Override
	public String[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
		return player.isSneaking()? new String[] { overlaySneakingFirstLine.format((double)average / 20), overlaySneakingSecondLine.format(packetAverage)} : new String[]{ overlayNormal.format(acceptedAmount) };
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		facing = Direction.byIndex(nbt.getByte("facing"));
		packetLimit = nbt.getInt("packetLimit");
		timeLimit = nbt.getInt("timeLimit");
		keepSize = nbt.getInt("keepSize");
		redstoneMode = nbt.getByte("redstoneMode");
		if(Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) return;
		lastAcceptedAmount = acceptedAmount = nbt.getLong("acceptedAmount");
		secondCounter = nbt.getInt("secondCounter");
		long avg = nbt.getLong("averages");
		for(int i = 0; i < 60; i++) averages[i] = avg;
		calculateAverages();
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		nbt.putByte("facing", (byte)facing.getIndex());
		nbt.putInt("packetLimit", packetLimit);
		nbt.putInt("timeLimit", timeLimit);
		nbt.putInt("keepSize", keepSize);
		nbt.putByte("redstoneMode", redstoneMode);
		if(Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) return;
		nbt.putLong("acceptedAmount", acceptedAmount);
		nbt.putInt("secondCounter", secondCounter);
		calculateAverages();
		nbt.putLong("averages", average);
	}

	@Override
	public boolean canOpenGui() {
		return true;
	}

	@Override
	public int getGuiID() {
		return GuiID;
	}

	@Nullable
	@Override
	public TileEntity getGuiMaster() {
		return this;
	}

	@SideOnly(Side.CLIENT)
	public abstract void showGui();

	@SideOnly(Side.CLIENT)
	@Override
	public void receiveMessageFromServer(CompoundNBT message) {
		if(message.contains("packetLimit")) {
			packetLimit = message.getInt("packetLimit");
			timeLimit = message.getInt("timeLimit");
			keepSize = message.getInt("keepSize");
			showGui();
		} else {
			packetAverage = message.getInt("packets");
			average = message.getLong("average");
			acceptedAmount = message.getLong("acceptedAmount");
		}
	}

	@Override
	public void receiveMessageFromClient(CompoundNBT message) {
		packetLimit = message.getInt("packetLimit");
		timeLimit = message.getInt("timeLimit");
		keepSize = message.getInt("keepSize");
		efficientMarkDirty();
	}

	public void notifyNearbyClients(CompoundNBT tag) {
		tag.putInt("packets", Math.max(packets, packetAverage));
		tag.putLong("average", average);
		tag.putLong("acceptedAmount", acceptedAmount);
		BlockPos center = getPos();
		ImmersiveTechnology.packetHandler.sendToAllTracking(new MessageTileSync(this, tag), new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 0));
	}

	@Override
	public boolean useNixieFont(PlayerEntity player, RayTraceResult mop) {
		return false;
	}



	@Override
	public Direction getFacing() {
		return this.facing;
	}

	@Override
	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	@Override
	public int getFacingLimitation() {
		return 0;
	}

	@Override
	public boolean mirrorFacingOnPlacement(LivingEntity placer) {
		return false;
	}

	@Override
	public boolean canHammerRotate(Direction side, float hitX, float hitY, float hitZ, LivingEntity entity) {
		return !entity.isSneaking();
	}

	@Override
	public boolean canRotate(Direction axis) {
		return true;
	}

	public int getRSPower() {
		int toReturn = 0;
		for(Direction directions : EnumSet.complementOf(EnumSet.of(facing, facing.getOpposite()))) {
			toReturn = Math.max(world.getRedstonePower(pos.offset(directions,-1), directions), toReturn);
		}
		return toReturn;
	}

	public static int longToInt(long value) {
		return value > Integer.MAX_VALUE? Integer.MAX_VALUE : value < Integer.MIN_VALUE? Integer.MIN_VALUE : (int) value;
	}

}