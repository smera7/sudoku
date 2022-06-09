package byow.WorldGeneration;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;

public class Hallway implements Serializable {
    private Position corner;
    private Position start;
    private Position end;
    private int orientation;

    public Hallway(Position corner, Position start, Position end, int orientation) {
        this.start = start;
        this.end = end;
        this.corner = corner;
        this.orientation = orientation;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    /**
     * Adds a vertical hallway to the world.
     * @param world to have a vertical hallway added to
     * @param p used as the starting position of the hallway
     * @param height used as the height of the hallway to be drawn
     * @return the position of the top end of the hallway
     */
    public static Position addVerticalHallway(TETile[][] world, Position p, int height) {
        for (int currY = 0; currY < height; currY++) {
            world[p.getX() - 1][p.getY() + currY] = Tileset.WALL;
            world[p.getX() + 1][p.getY() + currY] = Tileset.WALL;
        }
        return new Position(p.getX(), p.getY() + height);
    }

    /**
     * Adds a horizontal hallway to the world.
     * @param world to have a horizontal hallway added to
     * @param p used as the starting position of the hallway
     * @param width used as the width of the hallway to be drawn
     * @return the position of the right end of the hallway
     */
    public static Position addHorizontalHallway(TETile[][] world, Position p, int width) {
        for (int currX = 0; currX < width; currX++) {
            world[p.getX() + currX][p.getY() - 1] = Tileset.WALL;
            world[p.getX() + currX][p.getY() + 1] = Tileset.WALL;
        }
        return new Position(p.getX() + width, p.getY());
    }

    private static void fillHallway(TETile[][] world, Position p, int length, int orientation) {
        switch (orientation) {
            case 0: { // horizontal hallway
                for (int x = 0; x <= length; x++) {
                    world[p.getX() + x][p.getY()] = Tileset.FLOOR;
                }
            }
                break;
            case 1: { // vertical hallway
                for (int x = 0; x < length; x++) {
                    world[p.getX()][p.getY() + x] = Tileset.FLOOR;
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    public static void fillHallwayList(TETile[][] world, ArrayList<Hallway> hallwaysList) {
        for (Hallway hallway : hallwaysList) {
            switch (hallway.orientation) {
                case 0: { // horizontal hallway
                    // fills horizontal hallway
                    fillHallway(world, hallway.start,
                            hallway.corner.getX() - hallway.start.getX() + 1, 0);

                    // fills vertical hallway
                    fillHallway(world, Position.smallerPos(hallway.corner, hallway.end, 1),
                            Math.abs(hallway.corner.getY() - hallway.end.getY() + 1), 1);
                    break;
                }
                case 1: { // vertical hallway
                    // fills vertical hallway
                    fillHallway(world, hallway.start,
                            hallway.corner.getY() - hallway.start.getY() + 1, 1);

                    // fills horizontal hallway
                    fillHallway(world, Position.smallerPos(hallway.corner, hallway.end, 0),
                            Math.abs(hallway.corner.getX() - hallway.end.getX() + 1), 0);
                    break;
                } default: {
                    return;
                }
            }
        }
    }

    public static boolean checkSurroundings(TETile[][] world, Position p) {
        return world[p.getX()][p.getY() - 1] == Tileset.NOTHING
                || world[p.getX() - 1][p.getY()] == Tileset.NOTHING
                || world[p.getX()][p.getY() + 1] == Tileset.NOTHING
                || world[p.getX() + 1][p.getY()] == Tileset.NOTHING;
    }
}
