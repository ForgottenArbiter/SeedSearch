package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.BottledTornado;
import seedsearch.SeedSearch;

@SpirePatch(
        clz= BottledTornado.class,
        method="canSpawn"
)
public class BottledTornadoPatch {
    public static SpireReturn<Boolean> Prefix() {
        if (SeedSearch.settings.alwaysSpawnBottledTornado) {
            return SpireReturn.Return(true);
        } else {
            return SpireReturn.Continue();
        }
    }
}
