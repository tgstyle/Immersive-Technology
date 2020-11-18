package mctmods.immersivetechnology.common.network;

import java.util.function.Function;

import blusunrize.immersiveengineering.common.network.IMessage;
import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ITPacketHandler {

	public static final String NET_VERSION="1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(ImmersiveTechnology.MODID, "main")).networkProtocolVersion(()->NET_VERSION).serverAcceptedVersions(NET_VERSION::equals).clientAcceptedVersions(NET_VERSION::equals).simpleChannel();

	public static void preInit() {
	}

	private static int id=0;
	public static <T extends IMessage> void registerMessage(Class<T> type0, Function<PacketBuffer, T> decoder) {
		INSTANCE.registerMessage(id ++, type0, IMessage::toBytes, decoder, (type1, ctx) -> {
			type1.process(ctx);
			ctx.get().setPacketHandled(true);
		});
	}

	public static <MSG> void sendToPlayer(PlayerEntity player, MSG message) {
		if(message == null || !(player instanceof ServerPlayerEntity)) return;
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
	}

	public static <MSG> void sendToServer(MSG message) {
		if(message == null) return;
		INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static <MSG> void sendToDimension(RegistryKey<World> dim, MSG message) {
		if(message == null) return;
		INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), message);
	}

	public static <MSG> void sendAll(MSG message) {
		if(message == null) return;
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}

}
