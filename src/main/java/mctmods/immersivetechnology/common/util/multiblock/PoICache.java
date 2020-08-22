package mctmods.immersivetechnology.common.util.multiblock;

import net.minecraft.util.Direction;

public class PoICache {

    public Direction facing;
    public int position;

    public PoICache(Direction facing, PoIJSONSchema poi, boolean isMirrored) {
        this(poi.facing.LocalToGlobal(facing), poi.position, poi.facing, isMirrored);
    }

    public PoICache(Direction facing, int position, LocalFacing localFacing, boolean isMirrored) {
        this.position = position;
        this.facing = isMirrored && (localFacing == LocalFacing.LEFT || localFacing == LocalFacing.RIGHT)? facing.getOpposite() : facing;
    }

    public boolean isPoI(Direction facing, int position) {
        return this.position == position && this.facing == facing;
    }
}
