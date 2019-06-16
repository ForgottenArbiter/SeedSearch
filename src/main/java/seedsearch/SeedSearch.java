package seedsearch;

import java.util.ArrayList;

public class SeedSearch {

    public static boolean loadingEnabled = true;
    public static SeedRunner runner;
    public static SearchSettings settings;

    public static void search() {
        long time1 = System.nanoTime();
        ArrayList<Long> foundSeeds = new ArrayList<>();
        for(long seed = settings.startSeed; seed < settings.endSeed; seed++) {
            if (runner.runSeed(seed)) {
                foundSeeds.add(seed);
                if(settings.verbose) {
                    runner.getSeedResult().printSeedStats();
                }
            }
        }
        System.out.println(String.format("%d seeds found: ", foundSeeds.size()));
        System.out.println(foundSeeds);
    }

}
