package seedsearch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import java.util.ArrayList;

@SpireInitializer
public class SeedSearch {

    public static boolean loadingEnabled = true;
    public static SeedRunner runner;
    public static SearchSettings settings;

    public SeedSearch(){
    }

    public static void initialize(){
        SeedSearch seedSearch = new SeedSearch();
    }

    public static void search() {
        long time1 = System.nanoTime();
        ArrayList<Long> foundSeeds = new ArrayList<>();
        for(long seed = settings.startSeed; seed < settings.endSeed; seed++) {
            if (runner.runSeed(seed)) {
                foundSeeds.add(seed);
                if(settings.verbose) {
                    runner.printSeedStats();
                }
            }
        }
        System.out.println(foundSeeds);
        System.out.println(foundSeeds.size());
        System.out.println((System.nanoTime() - time1));
    }

}
