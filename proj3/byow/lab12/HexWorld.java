package byow.lab12;

import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int SEED = 4;
    private static final Random RANDOM = new Random(SEED);


    /**
     * Add a row to the tesselation with the desired tile
     * @param world to be used as the world that the row is added to
     * @param startPos to be used as the starting position of the row
     * @param size to be used as the width of the row
     * @param t to be used as the the type of tile to add
     */
    private static void addTiles(TETile[][] world, Position startPos, int size, TETile t) {
        for (int i = 0; i < size; i++) {
            world[startPos.x + i][startPos.y] = TETile.colorVariant(t, 32, 32, 32, RANDOM);
        }
    }

    private static void initializeWorld(TETile[][] world, TETile t) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = t;
            }
        }
    }

    private static int rowWidth(int size, int row) {
        if (row < size) {
            return size + 2 * row;
        } else {
            return size + 2 * (2 * size - row - 1);
        }
    }

    /**
     * Return the new starting position from the passed in row
     */
    private static Position newPos(Position p, int row, int size) {
        Position newPosition;

        if (row < size) {
            newPosition = new Position(p.x - row, p.y + row);
        } else {
            newPosition = new Position(p.x - 2 * size + row + 1, p.y + row);
        }
        return newPosition;
    }

    /**
     * Add a hexagon to tesselation.
     * @param world to be drawn on
     * @param p the bottom left coordinate of the hexagon
     * @param size the hexagon size
     * @param t tile to be drawn
     */
    public static void addHexagon(TETile[][] world, Position p, int size, TETile t) {
        if (size < 2) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 2 * size; i++) {
            Position startPos = newPos(p, i, size);
            int width = rowWidth(size, i);
            addTiles(world, startPos, width, t);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(10);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.FLOOR;
            case 3: return Tileset.AVATAR;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.LOCKED_DOOR;
            case 6: return Tileset.MOUNTAIN;
            case 7: return Tileset.SAND;
            case 8: return Tileset.UNLOCKED_DOOR;
            case 9: return Tileset.TREE;
            default: return Tileset.WATER;
        }
    }

    /**
     * Draw hexes in the world vertically
     * @param world to be drawn in
     * @param p the starting position of the first hexagon to be drawn
     * @param size used as the drawn hexagon size
     * @param numHexagons used as the number of vertical hexagon layout
     */
    public static void drawRandomHexes(TETile[][] world, Position p, int size, int numHexagons) {
        Position startPos = p;
        for (int i = 0; i < numHexagons; i++) {
            addHexagon(world, startPos, size, randomTile());
            startPos = new Position(startPos.x, startPos.y + 2 * size);
        }
    }

    public static void buildTesselation(TETile[][] world, Position p, int size) {
        Position startPos = p;
        int numHexes = 3;
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0: case 1: {
                    drawRandomHexes(world, startPos, size, numHexes);
                    startPos = bottomRNeighbor(p, size);
                    numHexes += 1;
                    break;
                }
                case 2: case 3: case 4: {
                    drawRandomHexes(world, startPos, size, numHexes);
                    startPos = topRNeighbor(p, size);
                    numHexes -= 1;
                    break;
                }
            }
        }
    }

    private static Position bottomRNeighbor(Position p, int size) {
        Position startPos = p;
        startPos.x += 2 * size - 1;
        startPos.y -= size;
        return startPos;
    }

    private static Position topRNeighbor(Position p, int size) {
        Position startPos = p;
        startPos.x += 2 * size - 1;
        startPos.y += size;
        return startPos;
    }

    public static void main(String[] args) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);
        TETile[][] hexWorld = new TETile[WIDTH][HEIGHT];

        Position bottomLeft = new Position(15, 15);
        initializeWorld(hexWorld, Tileset.NOTHING);

        buildTesselation(hexWorld, bottomLeft, 3);
        renderer.renderFrame(hexWorld);
    }
}
