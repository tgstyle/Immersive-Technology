package mctmods.immersivetechnology.common;

import java.lang.reflect.Field;

import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;

import blusunrize.immersiveengineering.common.config.IEServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ITConfig {

	public static final Miscellaneous MISCELLANEOUS;
	public static final ForgeConfigSpec ALL;

	static {
		ForgeConfigSpec.Builder builder=new ForgeConfigSpec.Builder();
		MISCELLANEOUS=new Miscellaneous(builder);
		ALL=builder.build();
	}

	private static Config rawConfig;
	public static Config getRawConfig() {
		if(rawConfig==null) {
			try {
				Field childConfig = ForgeConfigSpec.class.getDeclaredField("childConfig");
				childConfig.setAccessible(true);
				rawConfig = (Config) childConfig.get(IEServerConfig.CONFIG_SPEC);
				Preconditions.checkNotNull(rawConfig);
			} catch(Exception x) {
				throw new RuntimeException(x);
			}
		}
		return rawConfig;
	}

	public static class Miscellaneous{
		public final BooleanValue autounlock_recipes;
		Miscellaneous(ForgeConfigSpec.Builder builder){
			builder.push("Miscellaneous");
			autounlock_recipes=builder
					.comment("Automatically unlock IP recipes for new players, default=true")
					.define("autounlock_recipes", true);
			builder.pop();
		}
	}

}
