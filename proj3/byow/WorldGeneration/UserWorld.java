package byow.WorldGeneration;

import java.io.Serializable;
import byow.TileEngine.TETile;

public class UserWorld implements Serializable {
    private TETile[][] mainWorld;
    //    private TETile[][] secretWorld;
    private Position avatarPos;
    private Position keyPos;
    private Position doorPos;
    private boolean hasKey;
    private boolean currentWorld;

    public UserWorld(TETile[][] world, Position avatarPos, Position keyPos, Position doorPos) {
        this.mainWorld = world;
//        this.secretWorld = secretWorld;
        this.avatarPos = avatarPos;
        this.keyPos = keyPos;
        this.doorPos = doorPos;
        hasKey = false;
        currentWorld = false;
    }

    public TETile[][] getWorld() {
        return mainWorld;
//        if (!currentWorld) {
//            return mainWorld;
//        } else {
//            return secretWorld;
//        }
    }

    public void switchWorld() {
        currentWorld = !currentWorld;
    }

    public TETile[][] getMainWorld() {
        return mainWorld;
    }

//    public TETile[][] getSecretWorld() {
//        return secretWorld;
//    }

    public Position getAvatarPos() {
        return avatarPos;
    }

    public Position getKeyPos() {
        return keyPos;
    }

    public Position getDoorPos() {
        return doorPos;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void getKey() {
        System.out.println("key got");
        this.hasKey = true;
    }

    public void changeAvatarPos(Position updatedAvatarPos) {
        avatarPos = updatedAvatarPos;
    }
}
