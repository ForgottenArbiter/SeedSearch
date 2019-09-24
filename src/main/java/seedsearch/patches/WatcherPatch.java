package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.Watcher;

public class WatcherPatch {

    @SpirePatch(
            clz= Watcher.class,
            method="loadEyeAnimation"
    )
    public static class InitializeDescriptionPatch {
        public static void Replace(Watcher _instance) {
            //Do nothing
        }
    }

}
