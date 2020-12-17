package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.BottledLightning;
import seedsearch.SeedSearch;

@SpirePatch(
        clz= BottledLightning.class,
        method="canSpawn"
)
public class BottledLightningPatch {
    public static SpireReturn<Boolean> Prefix() {
        if (SeedSearch.settings.alwaysSpawnBottledLightning) {
            return SpireReturn.Return(true);
        } else {
            return SpireReturn.Continue();
        }
    }
}
