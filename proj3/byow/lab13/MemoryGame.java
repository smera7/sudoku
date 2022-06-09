package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Please enter a seed");
//            return;
//        }

        long seed = Long.parseLong("1234");
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = rand.nextInt(CHARACTERS.length);
            char nextChar = CHARACTERS[index];
            result.append(nextChar);
        }
        return result.toString();
    }

    public void drawFrame(String s, String task) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear();
        Font fontA = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(fontA);
        StdDraw.text((double) width / 2, (double) height - 2, task);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text((double) width / 2, (double) height / 2, s);
        StdDraw.show();
        StdDraw.pause(1000);
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        char[] chars = letters.toCharArray();
        StdDraw.clear();
        StdDraw.show();
        for (int i = 0; i < chars.length; i++) {
            String current = String.valueOf(chars[i]);
            drawFrame(current, "Watch!");
            //StdDraw.text((double) width / 2, (double) height / 2, current);
            //StdDraw.show();
            //StdDraw.pause(1000);
            StdDraw.clear();
            StdDraw.show();
            StdDraw.pause(500);

        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        int count = 0;
        StringBuilder result = new StringBuilder();
        while (count < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char next = StdDraw.nextKeyTyped();
                result.append(next);
                StdDraw.clear();
                drawFrame(result.toString(), "Type!");
                StdDraw.show();
                count++;
            }
        }
        return result.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        int round = 1;

        //TODO: Establish Engine loop
        while (true) {
            drawFrame("Round: " + round, "");
            String s = generateRandomString(round);
            flashSequence(s);
            String input = solicitNCharsInput(s.length());
            if (!input.equals(s)) {
                drawFrame("Game Over! You made it to round: " + round, "");
                break;
            }
            round++;
        }
    }

}
