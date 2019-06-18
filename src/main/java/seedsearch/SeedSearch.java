package seedsearch;

import java.util.ArrayList;

import static java.lang.System.exit;

public class SeedSearch {

    public static boolean loadingEnabled = true;
    public static SearchSettings settings;

    public static void search() {
        loadingEnabled = false;
        settings = SearchSettings.loadSettings();
        SeedRunner runner = new SeedRunner(settings);
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

        if (SeedSearch.settings.exitAfterSearch) {
            exit(0);
        } else {
            System.out.println("Search complete. Manually close this program when finished.");
        }
    }

}
