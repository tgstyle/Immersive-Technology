package mctmods.immersivetechnology.common.tileentities;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockOverlayText;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.Config;
import mctmods.immersivetechnology.common.util.TranslationKey;
import mctmods.immersivetechnology.common.util.network.MessageTileSync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public abstract class TileEntityCommonOSD extends TileEntityIEBase implements ITickable, IBlockOverlayText {

	public long acceptedAmount = 0;
	public long lastAcceptedAmount = 0;
	public int secondCounter = 0;

	public void efficientMarkDirty() {//!!!!!!! only use it within update() function !!!!!!!
		world.getChunkFromBlockCoords(this.getPos()).markDirty();
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {

	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {

	}

	@Override
	public void update() {
		if(world.isRemote) return;
		if(++secondCounter < 20) return;
		notifyNearbyClients(new CompoundNBT());
		lastAcceptedAmount = acceptedAmount;
		acceptedAmount = 0;
		secondCounter = 0;
	}

	abstract public TranslationKey text();

	@Override
	public String[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
		return new String[]{ text().format(Config.ITConfig.Experimental.per_tick_trash_cans? ((float)lastAcceptedAmount)/20 : lastAcceptedAmount) };
	}

	@Override
	public void receiveMessageFromServer(CompoundNBT message) {
		lastAcceptedAmount = message.getLong("acceptedAmount");
	}

	public void notifyNearbyClients(CompoundNBT tag) {
		tag.setLong("acceptedAmount", acceptedAmount);
		BlockPos center = getPos();
		ImmersiveTechnology.packetHandler.sendToAllTracking(new MessageTileSync(this, tag), new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 0));
	}

	@Override
	public boolean useNixieFont(PlayerEntity player, RayTraceResult mop) {
		return false;
	}

}