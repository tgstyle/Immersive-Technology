package mctmods.immersivetechnology.common.util.compat.opencomputers;

import mctmods.immersivetechnology.common.Config;
import mctmods.immersivetechnology.common.util.compat.ITCompatModule;

// Nearly the same thing as IE's OCHelper, so credit to BluSunrize for that

import li.cil.oc.api.API;

public class OCHelper extends ITCompatModule {

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		if (Config.ITConfig.Machines.Multiblock.enable_boiler) API.driver.add(new BoilerDriver());
		if (Config.ITConfig.Machines.Multiblock.enable_steamTurbine) API.driver.add(new SteamTurbineDriver());
		if (Config.ITConfig.Machines.Multiblock.enable_distiller) API.driver.add(new DistillerDriver());
		if (Config.ITConfig.Machines.Multiblock.enable_solarTower) API.driver.add(new SolarTowerDriver());
	}

	@Override
	public void postInit() {
		ComputerManualHelper.addManualContent();
	}
}
