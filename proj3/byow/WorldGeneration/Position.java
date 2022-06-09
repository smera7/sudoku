package byow.WorldGeneration;

import java.io.Serializable;

public class Position implements Serializable {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the Position object
     * @return the x-coordinate of the Position object
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Position object
     * @return the y-coordinate of the Position object
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the smaller position with respect to the passed orientation (x, y).
     * @param p1 to be compared to p2
     * @param p2 to be compared to p1
     * @param orientation to be used when comparing the two passed Position objects
     * @return the smaller position with respect to the passed orientation setting
     */
    public static Position smallerPos(Position p1, Position p2, int orientation) {
        Position returnedPos;
        switch (orientation) {
            case 0: { // x-coordinate compared
                if (p1.x > p2.x) {
                    returnedPos = p2;
                } else {
                    returnedPos = p1;
                }
                break;
            }
            case 1: { // y-coordinate compared
                if (p1.y > p2.y) {
                    returnedPos = p2;
                } else {
                    returnedPos = p1;
                }
            }
            break;
            default: {
                return null;
            }
        }
        return returnedPos;
    }

    /**
     * Returns the larger position with respect to the passed orientation (x, y).
     * @param p1 to be compared to p2
     * @param p2 to be compared to p1
     * @param orientation to be used when comparing the two passed Position objects
     * @return the larger position with respect to the passed orientation setting
     */
    public static Position largerPos(Position p1, Position p2, int orientation) {
        Position returnedPos;
        switch (orientation) {
            case 0: { // x-coordinate compared
                if (p1.x > p2.x) {
                    returnedPos = p1;
                } else {
                    returnedPos = p2;
                }
                break;
            }
            case 1: { // y-coordinate compared
                if (p1.y > p2.y) {
                    returnedPos = p1;
                } else {
                    returnedPos = p2;
                }
            }
            break;
            default: {
                return null;
            }
        }
        return returnedPos;
    }
}
