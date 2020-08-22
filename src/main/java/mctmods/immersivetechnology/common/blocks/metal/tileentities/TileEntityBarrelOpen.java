package mctmods.immersivetechnology.common.blocks.metal.tileentities;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import mctmods.immersivetechnology.common.Config.ITConfig.Barrels;
import mctmods.immersivetechnology.common.util.ITFluidTank;
import mctmods.immersivetechnology.common.util.TranslationKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Random;

public class TileEntityBarrelOpen extends TileEntityBarrelSteel {

	private static int tankSize = Barrels.barrel_open_tankSize;
	private static int transferSpeed = Barrels.barrel_open_transferSpeed;

	@Override
	public void createTank() {
		tank = new ITFluidTank(tankSize, this);
	}

	private int lastRandom = 0;
	private int sleep = 0;

	private static Random RANDOM = new Random();

	@Override
	public void update() {
		if(world.isRemote) return;
		if(tank.getFluidAmount() != tank.getCapacity()) {
			int random = 1 + RANDOM.nextInt(100);
			if(random == lastRandom) {
				if(tank.getFluid() == null || tank.getFluid().getFluid() == FluidRegistry.WATER) {
					float temp = world.getBiomeProvider().getTemperatureAtHeight(world.getBiome(pos).getTemperature(pos), pos.getY());
					if(world.isRaining() && world.canSeeSky(pos) && temp > 0.05F && temp < 2.0F) {
						int amount = 100;
						if(world.isThundering()) amount = 200;
						tank.fill(new FluidStack(FluidRegistry.WATER, amount), true);
					} else if(temp >= 2.0F) {
						tank.drain(Math.min(100, tank.getFluidAmount()), true);
					}
				}
			}
			lastRandom = random;
		}
		for(int index = 0; index < 2; index++) {
			if(tank.getFluidAmount() > 0 && sideConfig[index] == 1) {
				if(tank.getFluidAmount() > 0) {
					Direction face = Direction.byIndex(index);
					IFluidHandler output = FluidUtil.getFluidHandler(world, getPos().offset(face), face.getOpposite());
					if(output != null) {
						if(sleep == 0) {
							FluidStack accepted = Utils.copyFluidStackWithAmount(tank.getFluid(), Math.min(transferSpeed, tank.getFluidAmount()), false);
							accepted.amount = output.fill(Utils.copyFluidStackWithAmount(accepted, accepted.amount, true), false);
							if(accepted.amount > 0) {
								int drained = output.fill(Utils.copyFluidStackWithAmount(accepted, accepted.amount, false), true);
								tank.drain(drained, true);
								sleep = 0;
							} else {
								sleep = 20;
							}
						} else {
							sleep--;
						}
					}
				}
			}
		}
	}

	@Override
	public String[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
		if(Utils.isFluidRelatedItemStack(player.getHeldItem(Hand.MAIN_HAND))) {
			FluidStack fluid = tank.getFluid();
			return (fluid != null)?
					new String[]{TranslationKey.OVERLAY_OSD_BARREL_NORMAL_FIRST_LINE.format(fluid.getTranslationKey(), fluid.amount)}:
					new String[]{TranslationKey.GUI_EMPTY.text()};
		}
		return null;
	}

	@Override
	public boolean toggleSide(int side, PlayerEntity p) {
		return false;
	}

	@Override
	public boolean isFluidValid(FluidStack fluid) {
		return fluid != null && fluid.getFluid() != null && !fluid.getFluid().isGaseous(fluid);
	}

	@Override
	public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
		FluidStack fluid = FluidUtil.getFluidContained(heldItem);
		if(!isFluidValid(fluid)) {
			ChatUtils.sendServerNoSpamMessages(player, new TranslationTextComponent(Lib.CHAT_INFO + "noGasAllowed"));
			return true;
		}
		if(FluidUtil.interactWithFluidHandler(player, hand, tank)) {
			return true;
		}
		return false;
	}

}