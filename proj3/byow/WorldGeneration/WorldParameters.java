package byow.WorldGeneration;


public class WorldParameters {
    private int width;
    private int height;
    private long seed;

    public WorldParameters() {
        this.width = 80;
        this.height = 30;
        this.seed = 1234;
    }

//    public WorldParameters(int width, int height, long seed) {
//        this.width = width;
//        this.height = height;
//        this.seed = seed;
//    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getSeed() {
        return seed;
    }

    public WorldParameters changeWorldSeed(long seedChange) {
        WorldParameters worldParameters = new WorldParameters();
        worldParameters.seed = seedChange;
        return worldParameters;
    }
}
