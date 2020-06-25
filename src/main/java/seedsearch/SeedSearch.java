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
        System.out.println("Starting search...");
        for (long seed = settings.startSeed; seed < settings.endSeed; seed++) {
            if (runner.runSeed(seed, 3, true)) {
                System.out.println("Seed under closer scrutiny: ");
                System.out.println(seed);
//                runner.getSeedResult().printSeedStats();
                if (runner.runSeed(seed, 2) && runner.runSeed(seed, 2, true)
                        && runner.runSeed(seed, 1) && runner.runSeed(seed, 1, true)
                        && runner.runSeed(seed, 0) && runner.runSeed(seed, 0, true)
                        && runner.runSeed(seed, 3)) {
                    foundSeeds.add(seed);
                    if (settings.verbose) {
                        runner.getSeedResult().printSeedStats();
                    }
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
