package mctmods.immersivetechnology.common.util.multiblock;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Direction;

public enum LocalFacing {

    @SerializedName("0")
    DOWN(0),
    @SerializedName("1")
    UP(1),
    @SerializedName("2")
    FORWARD(2),
    @SerializedName("3")
    BACK(3),
    @SerializedName("4")
    LEFT(4),
    @SerializedName("5")
    RIGHT(5);

    private final int index;

    LocalFacing(int index) {
        this.index = index;
    }

    public Direction LocalToGlobal(Direction origin) {
        if(origin == null) return null;
        switch(this.index) {
            case 0: return RotateDown(origin);
            case 1: return RotateUp(origin);
            case 2: return origin;
            case 3: return origin.getOpposite();
            case 4: return origin.rotateYCCW();
            case 5: return origin.rotateY();
        }
        return null;
    }

    private Direction RotateDown(Direction origin) {
        switch(origin) {
            case UP: return Direction.SOUTH;
            case DOWN: return Direction.NORTH;
            default: return Direction.DOWN;
        }
    }

    private Direction RotateUp(Direction origin) {
        switch(origin) {
            case UP: return Direction.NORTH;
            case DOWN: return Direction.SOUTH;
            default: return Direction.UP;
        }
    }
}
