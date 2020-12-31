package mctmods.immersivetechnology.api;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public class ITProperties {

    public enum Render_Type implements IStringSerializable {
        HIDDEN, NORMAL, MIRRORED, DYNAMIC;

        @Override
        public String getName() {
            return this.toString().toLowerCase(Locale.ENGLISH);
        }
    }

    public static final PropertyEnum<Render_Type> RENDER = PropertyEnum.create("render", Render_Type.class);

}
