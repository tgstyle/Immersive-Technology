package mctmods.immersivetechnology.common.blocks;

import mctmods.immersivetechnology.api.client.MechanicalEnergyAnimation;
import net.minecraft.util.Direction;

public class ITBlockInterfaces {

	public interface IMechanicalEnergy {
		boolean isMechanicalEnergyTransmitter();
		boolean isMechanicalEnergyReceiver();

		Direction getMechanicalEnergyOutputFacing();
		Direction getMechanicalEnergyInputFacing();

		int inputToCenterDistance();
		int outputToCenterDistance();

		int getEnergy();
		MechanicalEnergyAnimation getAnimation();
	}

}