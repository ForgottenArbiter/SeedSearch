package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.BottledFlame;
import seedsearch.SeedSearch;

@SpirePatch(
        clz= BottledFlame.class,
        method="canSpawn"
)
public class BottledFlamePatch {
    public static SpireReturn<Boolean> Prefix() {
        if (SeedSearch.settings.alwaysSpawnBottledFlame) {
            return SpireReturn.Return(true);
        } else {
            return SpireReturn.Continue();
        }
    }
}
