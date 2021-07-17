package seedsearch;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

import static java.lang.System.exit;

public class SeedSearch {

    public static boolean loadingEnabled = true;
    public static SearchSettings settings = SearchSettings.loadSettings();

    private static void unlockBosses(String[] bosslist, int unlockLevel) {
        for (int i = 0; i < unlockLevel; i++) {
            if (i >= 3) {
                break;
            }
            UnlockTracker.unlockPref.putInteger(bosslist[i], 2);
            UnlockTracker.bossSeenPref.putInteger(bosslist[i], 1);
        }
    }

    public static void search() {
        loadingEnabled = false;
        String[] expectedBaseUnlocks = {"The Silent", "Defect", "Watcher"};
        String[] firstBossUnlocks = {"GUARDIAN", "GHOST", "SLIME"};
        String[] secondBossUnlocks = {"CHAMP", "AUTOMATON", "COLLECTOR"};
        String[] thirdBossUnlocks = {"CROW", "DONUT", "WIZARD"};
        UnlockTracker.unlockPref.data.clear();
        UnlockTracker.bossSeenPref.data.clear();
        for (String key : expectedBaseUnlocks) {
            UnlockTracker.unlockPref.putInteger(key, 2);
        }
        unlockBosses(firstBossUnlocks, settings.firstBoss);
        unlockBosses(secondBossUnlocks, settings.secondBoss);
        unlockBosses(thirdBossUnlocks, settings.thirdBoss);
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
