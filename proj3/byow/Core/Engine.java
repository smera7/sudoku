package byow.Core;

import byow.TileEngine.*;
import byow.WorldGeneration.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static byow.WorldGeneration.RandomWorld.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static long SEED;
    private static boolean changeFrame = false;
    private static UserWorld mainWorld;
    private static UserWorld secretWorld;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        WorldParameters tempWorldParam = new WorldParameters();
        TERenderer renderer = new TERenderer();
        renderer.initialize(tempWorldParam.getWidth(), tempWorldParam.getHeight());
        KeyboardInteraction k = new KeyboardInteraction();
        mainWorld = k.gameStartup(tempWorldParam, renderer);
        UserWorld world = mainWorld;
        secretWorld = transportWorld(tempWorldParam, renderer);
        while (k.possibleInput()) {
            cursorHover(world);
            char next = k.getNextInput();
            if (next == 'W' || next == 'A' || next == 'S' || next == 'D') {
                if (moveOrNot(world, next)) {
                    Position p = getUpdatedAvatarPos(world, next);
                    if (changeFrame) {
                        if (world == secretWorld) {
                            world = mainWorld;
                        } else {
                            world = secretWorld;
                        }
                        renderer.renderFrame(world.getWorld());
                        changeFrame = !changeFrame;
                    } else {
                        moveAvatar(world, p, renderer);
                    }
                } else {
                    k.drawGameOverFrame(tempWorldParam.getWidth(), tempWorldParam.getHeight());
                    world = k.gameStartup(tempWorldParam, renderer);
                }
            } else if (next == ':') {
                if (k.getNextInput() == 'Q') {
                    quitSaveWorld(world);
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        WorldParameters tempWorldParams = new WorldParameters();
        TERenderer renderer = new TERenderer();
        //renderer.initialize(tempWorldParams.getWidth(), tempWorldParams.getHeight());
        UserWorld world;
        KeyboardInteraction k = new KeyboardInteraction();


        ArrayList<Character> inputList = new ArrayList<>();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            inputList.add(chars[i]);
        }

        char next;
        mainWorld = setUp(tempWorldParams, renderer, inputList);
        world = mainWorld;
        secretWorld = transportWorld(tempWorldParams, renderer);

        int arrayLength = inputList.size();
        if (arrayLength == 0) {
            return world.getWorld();
        }
        int index = 0;
        for (int i = 0; i < arrayLength; i++) {
            next = Character.toUpperCase(inputList.remove(index));
            if (next == 'W' || next == 'A' || next == 'S' || next == 'D') {
                if (moveOrNot(world, next)) {
                    Position p = getUpdatedAvatarPos(world, next);
                    if (changeFrame) {
                        if (world == secretWorld) {
                            world = mainWorld;
                        } else {
                            world = secretWorld;
                        }
                        //renderer.renderFrame(world.getWorld());
                        changeFrame = !changeFrame;
                    } else {
                        moveAvatar(world, p, renderer);
                    }
                } else { // you die, game over
                    //k.drawGameOverFrame(tempWorldParams.getWidth(), tempWorldParams.getHeight());
                    //world = setUp(tempWorldParams, renderer, inputList);
                }
            } else if (next == ':') {
                if (inputList.remove(index) == 'Q') {
                    quitSaveWorld(world);
                    //System.exit(0);
                    break;
                }
            }
        }

        return world.getWorld();
    }

    private void cursorHover(UserWorld userWorld) {
        int xCoord = (int) StdDraw.mouseX();
        int yCoord = (int) StdDraw.mouseY();

        if (userWorld.getWorld()[xCoord][yCoord].equals(Tileset.FLOOR)) {
            ter.renderFrame(userWorld.getWorld());
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, 1, "This is the floor");
        } else if (userWorld.getWorld()[xCoord][yCoord].equals(Tileset.WALL)) {
            ter.renderFrame(userWorld.getWorld());
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, 1, "This is the wall");
        } else if (userWorld.getWorld()[xCoord][yCoord].equals(Tileset.LOCKED_DOOR)) {
            ter.renderFrame(userWorld.getWorld());
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, 1, "This is the locked door");
        } else if (userWorld.getWorld()[xCoord][yCoord].equals(Tileset.UNLOCKED_DOOR)) {
            ter.renderFrame(userWorld.getWorld());
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, 1, "This is the unlocked door");
        } else if (userWorld.getWorld()[xCoord][yCoord].equals(Tileset.FLOWER)) {
            ter.renderFrame(userWorld.getWorld());
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, 1, "This is the key to unlock the locked door");
        }
        StdDraw.show();
    }

    public UserWorld setUp(WorldParameters worldParams,
                           TERenderer renderer, ArrayList<Character> chars) {
        char next = Character.toUpperCase(chars.remove(0));
        if (next == 'N') {
            long seed = getSeed(chars);
            WorldParameters updatedWorldParam = worldParams.changeWorldSeed(seed);
            mainWorld = createWorld(updatedWorldParam, renderer);
        } else if (next == 'L') {
            UserWorld s = loadWorld();
            //renderer.renderFrame(s.getWorld());
            mainWorld = new UserWorld(s.getWorld(),
                    s.getAvatarPos(), s.getKeyPos(), s.getDoorPos());
        } else if (next == 'Q') {
            System.exit(0);
        }
        return mainWorld;
    }

    public long getSeed(ArrayList<Character> chars) {
        String seed = "";
        int index = 0;
        int charsLength = chars.size();
        for (int i = 0; i < charsLength; i++) {
            char next = chars.remove(index);
            if (next == 's' || next == 'S') {
                break;
            } else if (Character.isDigit(next)) {
                seed += next;
            } else {
                throw new IllegalArgumentException();
            }
        }
        return Long.parseLong(seed);
    }

    /**
     * updates Avatar's position based on user input.
     * @param userWorld to be used for world objects
     * @param userMoveInput to be used for Avatar movement
     * @return a UserWorld with an updated Avatar position
     */
    public Position getUpdatedAvatarPos(UserWorld userWorld,
                                        char userMoveInput) {
        Position updatedAvatarPos;
        switch (userMoveInput) {
            case ('W'): {
                updatedAvatarPos = new Position(userWorld.getAvatarPos().getX(),
                        userWorld.getAvatarPos().getY() + 1);
                if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && !userWorld.hasKey()) {
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && userWorld.hasKey()) {
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.UNLOCKED_DOOR) {
                    changeFrame = true;
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                }
                userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY()] = Tileset.FLOOR;
                break;
            }
            case ('A'): {
                updatedAvatarPos = new Position(userWorld.getAvatarPos().getX() - 1,
                        userWorld.getAvatarPos().getY());
                if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && !userWorld.hasKey()) {
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && userWorld.hasKey()) {
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.UNLOCKED_DOOR) {
                    changeFrame = true;
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                }
                userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY()] = Tileset.FLOOR;
                break;
            }
            case ('S'): {
                updatedAvatarPos = new Position(userWorld.getAvatarPos().getX(),
                        userWorld.getAvatarPos().getY() - 1);
                if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && !userWorld.hasKey()) {
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && userWorld.hasKey()) {
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.UNLOCKED_DOOR) {
                    changeFrame = true;
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                }
                userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY()] = Tileset.FLOOR;
                break;
            }
            case ('D'): {
                updatedAvatarPos = new Position(userWorld.getAvatarPos().getX() + 1,
                        userWorld.getAvatarPos().getY());
                if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && !userWorld.hasKey()) {
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.LOCKED_DOOR && userWorld.hasKey()) {
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                } else if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                        == Tileset.UNLOCKED_DOOR) {
                    changeFrame = true;
                    userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                            = Tileset.UNLOCKED_DOOR;
                    return userWorld.getAvatarPos();
                }
                userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY()] = Tileset.FLOOR;
                break;
            } default: {
                return null;
            }
        }
        if (userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                == Tileset.FLOWER) {
            userWorld.getKey();
        }
        userWorld.getWorld()[updatedAvatarPos.getX()][updatedAvatarPos.getY()]
                = Tileset.AVATAR;
        return updatedAvatarPos;
    }

    public UserWorld moveAvatar(UserWorld userWorld, Position updatedAvatarPos,
                                TERenderer renderer) {
        renderer.renderFrame(userWorld.getWorld());
        userWorld.changeAvatarPos(updatedAvatarPos);
        return userWorld;
    }

    /**
     * Returns true if Avatar is able to move to next position from user input
     * @param userWorld to be used for world objects
     * @param userMoveInput to be used for Avatar movement
     * @return true if the Avatar is able to move to the next position from user input
     */
    private boolean moveOrNot(UserWorld userWorld, char userMoveInput) {
        switch (userMoveInput) {
            case ('W'): {
                return userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY() + 1] != Tileset.WALL;
            }
            case ('A'): {
                return userWorld.getWorld()[userWorld.getAvatarPos().getX() - 1]
                        [userWorld.getAvatarPos().getY()] != Tileset.WALL;
            }
            case ('S'): {
                return userWorld.getWorld()[userWorld.getAvatarPos().getX()]
                        [userWorld.getAvatarPos().getY() - 1] != Tileset.WALL;
            }
            case ('D'): {
                return userWorld.getWorld()[userWorld.getAvatarPos().getX() + 1]
                        [userWorld.getAvatarPos().getY()] != Tileset.WALL;
            } default: {
                return false;
            }
        }
    }

    public static UserWorld loadWorld() {
        File worldToLoad = new File("./savedWorld.txt");
        if (worldToLoad.exists()) {
            try {
                FileInputStream fis = new FileInputStream(worldToLoad);
                ObjectInputStream ois = new ObjectInputStream(fis);
                return (UserWorld) ois.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("There are not current saved worlds to load");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Stream initialization error");
                System.exit(0);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        WorldParameters tempWorldParam = new WorldParameters();
        TERenderer renderer = new TERenderer();
        //renderer.initialize(WIDTH, HEIGHT);
        return RandomWorld.createWorld(tempWorldParam, renderer);
    }

    /**
     * Saves the current world object to a text file.
     * @param userWorld to be saved
     * @source https://mkyong.com/java/how-to-read-and-write-java-object-to-a-file/
     */
    private static void quitSaveWorld(UserWorld userWorld) {
        File savedWorld = new File("./savedWorld.txt");
        try {
            if (!savedWorld.exists()) {
                savedWorld.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(savedWorld);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            /* Write object to file. */
            oos.writeObject(userWorld);
        } catch (FileNotFoundException e) {
            System.out.println("There is no file to save the current world");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Stream initialization error");
            System.exit(0);
        }
    }
}
