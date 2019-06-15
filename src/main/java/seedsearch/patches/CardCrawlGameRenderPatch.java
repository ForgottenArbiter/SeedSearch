package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="render"
)
public class CardCrawlGameRenderPatch {
    public static void Replace(CardCrawlGame _instance) {
        // Do nothing
    }
}
