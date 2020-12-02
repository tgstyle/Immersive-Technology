package mctmods.immersivetechnology.common.tileentities;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.*;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.util.TranslationKey;
import mctmods.immersivetechnology.common.util.network.MessageTileSync;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public abstract class CommonValveTileEntity extends IEBaseTileEntity implements IDirectionalTile, ITickableTileEntity, IBlockOverlayText, IPlayerInteraction, IInteractionObjectIE, IHammerInteraction {

	NonNullList<ItemStack> inventory = NonNullList.withSize(27, ItemStack.EMPTY);
	public ResourceLocation lootTable;

	final TranslationKey overlayNormal;
	final TranslationKey overlaySneakingFirstLine;
	final TranslationKey overlaySneakingSecondLine;
	final int GuiID;

	public CommonValveTileEntity(TranslationKey overlayNormal, TranslationKey overlaySneakingFirstLine, TranslationKey overlaySneakingSecondLine) {
		this.overlayNormal = overlayNormal;
		this.overlaySneakingFirstLine = overlaySneakingFirstLine;
		this.overlaySneakingSecondLine = overlaySneakingSecondLine;
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
	public ITextComponent[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
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
	public boolean canUseGui(PlayerEntity player) {
		return true;
	}

	@Override
	public IInteractionObjectIE getGuiMaster() {
		return this;
	}

	@Nonnull
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
		if(this.lootTable!=null) {
			LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
			this.lootTable = null;
			LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld)this.world);
			contextBuilder.withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos));
			if(player!=null) contextBuilder.withLuck(player.getLuck());
			LootContext context = contextBuilder.build(LootParameterSets.CHEST);
			Random rand = new Random();

			List<ItemStack> list = loottable.generate(context);
			List<Integer> listSlots = Lists.newArrayList();
			for(int i = 0; i < inventory.size(); i++)
				if(inventory.get(i).isEmpty())
					listSlots.add(i);
			Collections.shuffle(listSlots, rand);
			if(!listSlots.isEmpty()) {
				Utils.shuffleLootItems(list, listSlots.size(), rand);
				for(ItemStack itemstack : list) {
					int slot = listSlots.remove(listSlots.size()-1);
					inventory.set(slot, itemstack);
				}
				this.markDirty();
			}
		}
		return IInteractionObjectIE.super.createMenu(id, playerInventory, player);
	}

	@Override
	public void receiveMessageFromServer(CompoundNBT message) {
		if(message.contains("packetLimit")) {
			packetLimit = message.getInt("packetLimit");
			timeLimit = message.getInt("timeLimit");
			keepSize = message.getInt("keepSize");
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
	public PlacementLimitation getFacingLimitation() {
		return null;
	}

	@Override
	public boolean mirrorFacingOnPlacement(LivingEntity placer) {
		return false;
	}

	@Override
	public boolean hammerUseSide(Direction side, PlayerEntity player, Hand hand, Vector3d hitVec) {
		return !player.isSneaking();
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