package seedsearch;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

import static java.lang.System.exit;

public class SeedSearch {

    public static boolean loadingEnabled = true;
    public static SearchSettings settings = SearchSettings.loadSettings();

    public static void search() {
        loadingEnabled = false;
        String[] expectedBaseUnlocks = {"CROW", "AUTOMATON", "SLIME", "CHAMP", "WIZARD", "DONUT", "GUARDIAN", "GHOST", "COLLECTOR", "The Silent", "Defect", "Watcher"};
        UnlockTracker.unlockPref.data.clear();
        for (String key : expectedBaseUnlocks) {
            UnlockTracker.unlockPref.putInteger(key, 2);
        }
        UnlockTracker.resetUnlockProgress(AbstractPlayer.PlayerClass.IRONCLAD);
        UnlockTracker.unlockProgress.putInteger("IRONCLADUnlockLevel", settings.ironcladUnlocks);
        UnlockTracker.resetUnlockProgress(AbstractPlayer.PlayerClass.THE_SILENT);
        UnlockTracker.unlockProgress.putInteger("THE_SILENTUnlockLevel", settings.silentUnlocks);
        UnlockTracker.resetUnlockProgress(AbstractPlayer.PlayerClass.DEFECT);
        UnlockTracker.unlockProgress.putInteger("DEFECTUnlockLevel", settings.defectUnlocks);
        UnlockTracker.resetUnlockProgress(AbstractPlayer.PlayerClass.WATCHER);
        UnlockTracker.unlockProgress.putInteger("WATCHERUnlockLevel", settings.watcherUnlocks);
        UnlockTracker.retroactiveUnlock();
        UnlockTracker.refresh();
        SeedRunner runner = new SeedRunner(settings);
        long seed = SeedHelper.getLong(settings.alphanumericSeed);
        runner.runSeed(seed);
        runner.getSeedResult().printSeedStats(settings);

        if (settings.exitAfterSearch) {
            exit(0);
        } else {
            System.out.println("Search complete. Manually close this program when finished.");
        }
    }

}
