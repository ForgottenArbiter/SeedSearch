package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="hasRelic"
)
public class PlayerRelics {
}
