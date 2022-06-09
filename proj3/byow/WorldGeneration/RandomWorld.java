package byow.WorldGeneration;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class RandomWorld implements Serializable {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static Position avatarPos;
    private static Position secretAvatarPos;
    private static Position keyPos;
    private static Position doorPos;
    private static Position secretDoorPos;

//    private static final long SEED = 1234;
//    private static final Random RANDOM = new Random(SEED);

    /**
     * Initializes the world map with the passed TETile t.
     * @param world to be filled with the passed TETile t
     * @param t to be used as the tile to fill the world with
     */
    public static void initializeWorld(TETile[][] world, TETile t) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = t;
            }
        }
    }

    /**
     * To fill in the corner of a given position.
     * @param world to be used as the world map that is drawn in
     * @param cornerPos to be used as the reference position when filling a corner
     */
    private static void fillCorner(TETile[][] world, Position cornerPos) {
        for (int i = cornerPos.getX() - 1; i <= cornerPos.getX() + 1; i++) {
            for (int j = cornerPos.getY() - 1; j <= cornerPos.getY() + 1; j++) {
                world[i][j] = Tileset.WALL;
            }
        }
        world[cornerPos.getX()][cornerPos.getY()] = Tileset.FLOOR;
    }

    /**
     * Draws numRooms rooms with a random height and width.
     * @param world to have numRooms rooms added to it
     * @param worldParam to be used as world parameters
     * @return a list of Rooms that were added to the world map
     */
    private static ArrayList<Room> drawRooms(TETile[][] world, WorldParameters worldParam) {
        Random random = new Random(worldParam.getSeed() + 30);
        int maxNumRooms = 40;
        int roomMaxWidth = 10;
        int roomMaxHeight = 10;

        ArrayList<Room> roomsMap = new ArrayList<>();
        int numRooms = random.nextInt(maxNumRooms) + 10;
        int i = 0;
        while (i <= numRooms) {
            int roomWidth = random.nextInt(roomMaxWidth - 3) + 3;
            int roomHeight = random.nextInt(roomMaxHeight - 3) + 3;
            int roomXCoord = random.nextInt(WIDTH - roomWidth - 5) + 5;
            int roomYCoord = random.nextInt(HEIGHT - roomHeight - 5) + 5;
            Position startPos = new Position(roomXCoord, roomYCoord);
            if (Room.checkOverlap(roomsMap, roomWidth, roomHeight, startPos)) {
                continue;
            }
            Room.addRoom(world, startPos, roomWidth, roomHeight);
            Room room = new Room(roomWidth, roomHeight, startPos, worldParam);
            roomsMap.add(room);
            i++;
        }
        return roomsMap;
    }


    /**
     * Draws an L-shaped hallway between the doors of room1 and room2.
     * @param world to have a Hallway object added to it
     * @param room1 to be connected to room2 with a Hallway
     * @param room2 to be connected to room1 with a Hallway
     * @param numHallways to be used as the number of hallways to draw
     * @return a Hallway object
     */
    public static Hallway drawHallways(TETile[][] world, Room room1,
                                       Room room2, int numHallways) {
        int i = 0;
        while (i < numHallways) {
            Room leftMostRoom = Room.smallerRoom(room1, room2, 0);

            /* Door is on a vertical edge. */
            if (leftMostRoom.getDoor().getX() == leftMostRoom.getBotL().getX()
                    || leftMostRoom.getDoor().getX() == leftMostRoom.getBotL().getX()
                    + leftMostRoom.getWidth() - 1) {

                /* Draws a horizontal hallway starting from the left-most room's door. */
                Position leftMostDoor = leftMostRoom.getDoor();
                Position horHallPos = Hallway.addHorizontalHallway(world, leftMostDoor,
                        Math.abs(room2.getDoor().getX() - room1.getDoor().getX()));

                /*
                 * Determine whether the rightMostDoor or the end of horizontal hallway is lower and
                 * draws a vertical hallway starting from the lower position.
                 */
                Position rightMostDoor = Position.largerPos(room1.getDoor(),
                        room2.getDoor(), 0);
                Position vertHallwayStart = Position.smallerPos(horHallPos,
                        rightMostDoor, 1);
                Hallway.addVerticalHallway(world, vertHallwayStart,
                        Math.abs(room2.getDoor().getY() - room1.getDoor().getY()));

                fillCorner(world, horHallPos);
                return new Hallway(horHallPos, leftMostDoor, rightMostDoor, 0);

            } else {        // Door is on a horizontal edge.

                /* Draws a vertical hallway starting from the lower door of room1 and room2. */
                Position lowerDoorPos = Position.smallerPos(room1.getDoor(),
                        room2.getDoor(), 1);
                Position vertHallPos = Hallway.addVerticalHallway(world, lowerDoorPos,
                        Math.abs(room2.getDoor().getY() - room1.getDoor().getY()));

                /*
                 * Select the higher door of room1 or room2 as higherDoor and find whether the
                 * higherDoor or the top-end of the vertical hallway is closer to the left edge of
                 * the world. A horizontal hallway is drawn starting from the left-most position.
                 */
                Position higherDoor = Position.largerPos(room1.getDoor(), room2.getDoor(), 1);
                Position horHallwayStart = Position.smallerPos(vertHallPos, higherDoor, 0);
                Hallway.addHorizontalHallway(world, horHallwayStart,
                        Math.abs(room2.getDoor().getX() - room1.getDoor().getX()));

                fillCorner(world, vertHallPos);
                return new Hallway(vertHallPos, lowerDoorPos, higherDoor, 1);
            }
        }
        return null;
    }

    /**
     * Draws a world with rooms and hallways.
     * @param world to be used as the map where structures are drawn
     * @param worldParam to be used for world parameters
     */
    public static void drawMap(TETile[][] world, WorldParameters worldParam) {
        Random random = new Random(worldParam.getSeed() + 4321);

        ArrayList<Room> roomList = drawRooms(world, worldParam);
        ArrayList<Hallway> hallwaysList = new ArrayList<>();
        for (int i = 0; i < roomList.size() - 1; i++) {
            hallwaysList.add(drawHallways(world, roomList.get(i),
                    roomList.get(i + 1), roomList.size()));
        }

        /* Fills in rooms and hallways with floor tiles. */
        Room.fillRooms(world, roomList);
        Hallway.fillHallwayList(world, hallwaysList);

        avatarPos = drawAvatar(world, worldParam, roomList);
        keyPos = drawKey(world, worldParam, roomList);
        doorPos = drawLockedDoor(world, worldParam, roomList);

        /* Fills in missing edges that were created while drawing hallways. */
        for (Hallway hallway : hallwaysList) {
            if (Hallway.checkSurroundings(world, hallway.getEnd())) {
                world[hallway.getEnd().getX()][hallway.getEnd().getY()] = Tileset.WALL;
            }
            if (Hallway.checkSurroundings(world, hallway.getStart())) {
                world[hallway.getStart().getX()][hallway.getStart().getY()] = Tileset.WALL;
            }
        }
        for (int i = 0; i < world.length - 1; i++) {
            for (int j = 0; j < world[0].length - 1; j++) {
                if (world[i][j] == Tileset.FLOOR && (world[i + 1][j] == Tileset.NOTHING
                        || world[i - 1][j] == Tileset.NOTHING
                        || world[i][j + 1] == Tileset.NOTHING
                        || world[i][j - 1] == Tileset.NOTHING)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
//        drawHUD(world);
    }

    public static Position drawAvatar(TETile[][] world,
                                      WorldParameters worldParams, ArrayList<Room> roomList) {
        Random random = new Random(worldParams.getSeed() + 4321);
        Room startRoom = roomList.get(random.nextInt(roomList.size()));
        Position startPos = new Position(startRoom.getBotL().getX() + 1,
                startRoom.getBotL().getY() + 1);
        world[startPos.getX()][startPos.getY()] = Tileset.AVATAR;
        return startPos;
    }

    public static Position drawKey(TETile[][] world,
                                   WorldParameters worldParams, ArrayList<Room> roomList) {
        Random random = new Random(worldParams.getSeed() + 4231);
        Room startRoom = roomList.get(random.nextInt(roomList.size()));
        Position startPos = new Position(startRoom.getTopR().getX() - 1,
                startRoom.getTopR().getY() - 1);
        world[startPos.getX()][startPos.getY()] = Tileset.FLOWER;
        return startPos;
    }

    public static Position drawLockedDoor(TETile[][] world,
                                          WorldParameters worldParams, ArrayList<Room> roomList) {
        Random random = new Random(worldParams.getSeed() + 4132);
        while (true) {
            Room startRoom = roomList.get(random.nextInt(roomList.size()));
            Position startPos = new Position(startRoom.getBotL().getX(),
                    startRoom.getBotL().getY() + 1);
            if (world[startPos.getX()][startPos.getY()] == Tileset.WALL) {
                world[startPos.getX()][startPos.getY()] = Tileset.LOCKED_DOOR;
                return startPos;
            }
        }
    }

    public static void drawSecretRoom(TETile[][] world, WorldParameters worldParam) {
        int roomWidth = 80;
        int roomHeight = 30;
        Position startPos = new Position(0, 0);
        Room.addRoom(world, startPos, roomWidth, roomHeight);
        Room room = new Room(roomWidth, roomHeight, startPos, worldParam);
        Room.fillSecretRoom(world, room);
        secretDoorPos = room.getDoor();
        world[secretDoorPos.getX()][secretDoorPos.getY() - 1] = Tileset.AVATAR;
        secretAvatarPos = new Position(secretDoorPos.getX(), secretDoorPos.getY() - 1);
    }

    public static UserWorld createWorld(WorldParameters worldParam, TERenderer renderer) {
        TETile[][] worldFrame = new TETile[WIDTH][HEIGHT];
        initializeWorld(worldFrame, Tileset.NOTHING);
        drawMap(worldFrame, worldParam);
        renderer.renderFrame(worldFrame);
//        TETile[][] secretWorldFrame = new TETile[WIDTH][HEIGHT];
//        initializeWorld(secretWorldFrame, Tileset.NOTHING);
        //renderer.renderFrame(worldFrame);
        TETile[][] secretWorldFrame = new TETile[WIDTH][HEIGHT];
        initializeWorld(secretWorldFrame, Tileset.NOTHING);
//        drawSecretRoom(secretWorldFrame, worldParam);

        UserWorld userWorld = new UserWorld(worldFrame, avatarPos, keyPos, doorPos);
        return userWorld;
    }

    public static UserWorld transportWorld(WorldParameters worldParam, TERenderer renderer) {
        TETile[][] worldFrame = new TETile[WIDTH][HEIGHT];
        initializeWorld(worldFrame, Tileset.NOTHING);
        drawSecretRoom(worldFrame, worldParam);

        UserWorld userWorld = new UserWorld(worldFrame, secretAvatarPos, keyPos, secretDoorPos);
        return userWorld;
    }

//    public static void drawHUD(TETile[][] world) {
//        Position mousePos = new Position((int) StdDraw.mouseX(), (int) StdDraw.mouseY());
//        TETile x = world[mousePos.getX()][mousePos.getY()];
//        String desc = x.description();
//        Font f = new Font("Monaco", Font.BOLD, 15);
//        StdDraw.setFont(f);
//        StdDraw.text(10, 10, desc);
//        StdDraw.show();
//    }

    public static void main(String[] args) {
//        TERenderer renderer = new TERenderer();
//        renderer.initialize(WIDTH, HEIGHT);
//        TETile[][] roomWorld = new TETile[WIDTH][HEIGHT];
//
//        initializeWorld(roomWorld, Tileset.NOTHING);
//
//        drawMap(roomWorld, 17, 1, 1234);
//        renderer.renderFrame(roomWorld);
        WorldParameters tempWorldParam = new WorldParameters();
        TERenderer renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);
        createWorld(tempWorldParam, renderer);
    }
}
