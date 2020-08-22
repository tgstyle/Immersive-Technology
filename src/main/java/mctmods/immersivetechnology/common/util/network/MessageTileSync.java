package mctmods.immersivetechnology.common.util.network;

import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageTileSync implements IMessage {
	BlockPos pos;
	CompoundNBT nbt;

	public MessageTileSync(TileEntityIEBase tile, CompoundNBT nbt) {
		this.pos = tile.getPos();
		this.nbt = nbt;
	}

	public MessageTileSync() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ());
		ByteBufUtils.writeTag(buf, this.nbt);
	}

	public static class HandlerServer implements IMessageHandler<MessageTileSync, IMessage> {
		@Override
		public IMessage onMessage(MessageTileSync message, MessageContext ctx) {
			WorldServer world = ctx.getServerHandler().player.getServerWorld();
			world.addScheduledTask(() -> {
				if(world.isBlockLoaded(message.pos)) {
					TileEntity tile = world.getTileEntity(message.pos);
					if(tile instanceof TileEntityIEBase)
						((TileEntityIEBase)tile).receiveMessageFromClient(message.nbt);
				}
			});
			return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class HandlerClient implements IMessageHandler<MessageTileSync, IMessage>	{
		@Override
		public IMessage onMessage(MessageTileSync message, MessageContext ctx) {
			Minecraft.getInstance().addScheduledTask(() -> {
				World world = Minecraft.getInstance().world;
				if(world!=null) {
					TileEntity tile = world.getTileEntity(message.pos);
					if(tile instanceof TileEntityIEBase)
						((TileEntityIEBase)tile).receiveMessageFromServer(message.nbt);
				}
			});
			return null;
		}
	}

}