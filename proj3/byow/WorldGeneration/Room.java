package byow.WorldGeneration;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Room implements Serializable {
    private int width;
    private int height;
    private Position botL;
    private Position topR;
    private Position door;

    public Room(int w, int h, Position p, WorldParameters worldParam) {
        width = w;
        height = h;
        botL = p;
        topR = new Position(botL.getX() + width - 1, botL.getY() + height - 1);
        door = randomRoomPos(width, height, botL, topR, worldParam);
    }

    /**
     * Returns a random position along the edge of a room.
     * @param width to be used as the width of a room
     * @param height to be used as the height of a room
     * @param botL to be used as the bottom-left corner of a room
     * @param topR to be used as the top-right corner of a rom
     * @return a random position along the edge of a room
     */
    private static Position randomRoomPos(int width, int height,
                                          Position botL, Position topR, WorldParameters worldParam) {
        Random random = new Random(worldParam.getSeed());
        int options = random.nextInt(4);
        Position doorPos;
        switch (options) {
            case 0: { // bottom wall
                int randomX = random.nextInt(width - 2) + 1;
                doorPos = new Position(botL.getX() + randomX, botL.getY());
                break;
            }
            case 1: { // left wall
                int randomY = random.nextInt(height - 2) + 1;
                doorPos = new Position(botL.getX(), botL.getY() + randomY);
                break;
            }
            case 2: { // top wall
                int randomX = random.nextInt(width - 2) + 1;
                doorPos = new Position(topR.getX() - randomX, topR.getY());
                break;
            }
            case 3: { // right wall
                int randomY = random.nextInt(height - 2) + 1;
                doorPos = new Position(topR.getX(), topR.getY() - randomY);
                break;
            }
            default: {
                return null;
            }
        }
        return doorPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Position getBotL() {
        return botL;
    }

    public Position getTopR() {
        return topR;
    }

    public Position getDoor() {
        return door;
    }

    public static Room smallerRoom(Room r1, Room r2, int orientation) {
        Room returnedRoom;
        switch (orientation) {
            case 0: { // x-coordinate compared
                if (r1.getDoor().getX() > r2.getDoor().getX()) {
                    returnedRoom = r2;
                } else {
                    returnedRoom = r1;
                }
                break;
            }
            case 1: { // y-coordinate compared
                if (r1.getDoor().getY() > r2.getDoor().getY()) {
                    returnedRoom = r2;
                } else {
                    returnedRoom = r1;
                }
                break;
            }
            default: {
                return null;
            }
        }
        return returnedRoom;
    }

    /**
     * Draws the walls of a room and adds it to the world.
     * @param world to have the room added to
     * @param p used as the room position
     * @param w used as the room width
     * @param h used as the room height
     */
    public static void addRoom(TETile[][] world, Position p, int w, int h) {
        for (int x = 0; x < w; x++) {
            world[p.getX() + x][p.getY()] = Tileset.WALL;
            world[p.getX() + x][p.getY() + h - 1] = Tileset.WALL;
        }
        for (int y = 0; y < h; y++) {
            world[p.getX()][p.getY() + y] = Tileset.WALL;
            world[p.getX() + w - 1][p.getY() + y] = Tileset.WALL;
        }
    }

    /**
     * Fills the room with floor tiles.
     * @param world to have floor tiles added to
     * @param room to be filled with floor tiles
     */
    private static void fillRoom(TETile[][] world, Room room) {
        for (int x = 1; x < room.getWidth() - 1; x++) {
            for (int y = 1; y < room.getHeight() - 1; y++) {
                world[room.botL.getX() + x][room.botL.getY() + y] = Tileset.FLOOR;
            }
        }
        world[room.door.getX()][room.door.getY()] = Tileset.FLOOR;
    }

    /**
     * Fills all existing rooms with floor tiles.
     * @param world to have floor tiles added to
     * @param rooms to be filled with floor tiles
     */
    public static void fillRooms(TETile[][] world, ArrayList<Room> rooms) {
        for (Room room : rooms) {
            fillRoom(world, room);
        }
    }

    /**
     * Fills secret room with floor tiles.
     * @param world to have floor tiles added to
     * @param room to be filled with floor tiles
     */
    public static void fillSecretRoom(TETile[][] world, Room room) {
        for (int x = 1; x < room.getWidth() - 1; x++) {
            for (int y = 1; y < room.getHeight() - 1; y++) {
                world[room.botL.getX() + x][room.botL.getY() + y] = Tileset.FLOOR;
            }
        }
        world[room.door.getX()][room.door.getY()] = Tileset.UNLOCKED_DOOR;
    }

    /**
     * Checks for overlap between the new room and existing rooms.
     * @param roomsMap to be used for comparing the new room to the existing rooms in the world
     * @param width to be used as the new room width
     * @param height to be used as the new room height
     * @param startPos to be used as the new room bottom-left corner
     * @return true if the room overlaps with an existing room in roomsMap, false otherwise
     */
    public static boolean checkOverlap(ArrayList<Room> roomsMap, int width,
                                       int height, Position startPos) {
        Position botL = startPos;
        Position topL = new Position(startPos.getX(), startPos.getY() + height - 1);
        Position topR = new Position(startPos.getX() + width - 1, startPos.getY() + height - 1);
        Position botR = new Position(startPos.getX() + width - 1, startPos.getY());

        for (Room room : roomsMap) {
            if (botL.getX() <= room.topR.getX() && botL.getX() >= room.botL.getX()
                    && botL.getY() <= room.topR.getY() && botL.getY() >= room.botL.getY()) {
                return true;
            }
            if (topL.getX() <= room.topR.getX() && topL.getX() >= room.botL.getX()
                    && topL.getY() <= room.topR.getY() && topL.getY() >= room.botL.getY()) {
                return true;
            }
            if (topR.getX() <= room.topR.getX() && topR.getX() >= room.botL.getX()
                    && topR.getY() <= room.topR.getY() && topR.getY() >= room.botL.getY()) {
                return true;
            }
            if (botR.getX() <= room.topR.getX() && botR.getX() >= room.botL.getX()
                    && botR.getY() <= room.topR.getY() && botR.getY() >= room.botL.getY()) {
                return true;
            }
        }
        return false;
    }
}
