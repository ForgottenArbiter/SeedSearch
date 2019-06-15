package seedsearch.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="renderBlackFadeScreen"
)
public class BlackFadeScreenPatch {

    public static void Replace(CardCrawlGame _instance, SpriteBatch sb) {

    }

}
