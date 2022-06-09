package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.WorldGeneration.Position;
import byow.WorldGeneration.UserWorld;
import byow.WorldGeneration.WorldParameters;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

import static byow.Core.Engine.loadWorld;
import static byow.WorldGeneration.RandomWorld.*;

public class KeyboardInteraction {

    /**
     * Creates a new world.
     * @param worldParameters
     * @param renderer
     * @return
     */
    public UserWorld gameStartup(WorldParameters worldParameters,
                                 TERenderer renderer) {
        char next = getNextInput();
        if (next == 'N') {
            drawSeedFrame("", worldParameters.getWidth(), worldParameters.getHeight());
            long seed = getSeed(worldParameters.getWidth(), worldParameters.getHeight());
            WorldParameters updatedWorldParam =  worldParameters.changeWorldSeed(seed);
            System.out.println("New Game");
            UserWorld s = createWorld(updatedWorldParam, renderer);
            return s;
        }
        if (next == 'L') {
            UserWorld s = loadWorld();
            System.out.println("loaded");
            renderer.renderFrame(s.getWorld());
            System.out.println("rendered");
            UserWorld userWorld = new UserWorld(s.getWorld(),
                    s.getAvatarPos(), s.getKeyPos(), s.getDoorPos());
            return userWorld;
        }
        if (next == 'Q') {
            System.exit(0);
        }
        return null;
    }

    public void drawGameOverFrame(int w, int h) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) w / 2, h - 5, "Game Over!");
        StdDraw.text((double) w / 2, h - 10, "New Game (N)");
        StdDraw.text((double) w / 2, h - 13, "Quit (Q)");
        StdDraw.show();
    }

    public void drawSeedFrame(String text, int w, int h) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) w / 2, h - 15, "Enter Seed, then press S:");
        StdDraw.text((double) w / 2, h - 17, text);
        StdDraw.show();

    }

//    public void drawHUD(TETile tile, TETile[][] world) {
//        switch (tile) {
//            case (Tileset.WALL) :
//
//
//        }
//    }

    public TETile mouseTile(TETile[][] world, Position mousePos) {
        TETile x = world[mousePos.getX()][mousePos.getY()];
        return x;
    }

    /**
     * Returns the user-inputted seed to be used in the Random generator.
     * @param w to be used as the world width
     * @param h to be used as the world height
     * @return the user-inputted seed to be used in the Random generator
     */
    public long getSeed(int w, int h) {
        char next;
        String seed = "";
        while (possibleInput()) {
            next = getNextInput();
            if (next == 'S') {
                break;
            }
            if (Character.isDigit(next)) {
                seed += next;
                StdDraw.clear();
                drawSeedFrame(seed, w, h);
                StdDraw.show();
            } else {
                throw new IllegalArgumentException();
            }
        }
        return Long.parseLong(seed);
    }

    /**
     * Returns an upper-case char that was inputted by the user.
     * @return an upper-case char that was inputted by the user
     */
    public char getNextInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

    public boolean possibleInput() {
        return true;
    }
}
