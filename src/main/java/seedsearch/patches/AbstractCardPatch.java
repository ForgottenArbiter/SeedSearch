package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class AbstractCardPatch {

    @SpirePatch(
            clz= AbstractCard.class,
            method="initializeDescription"
    )
    public static class InitializeDescriptionPatch {
        public static SpireReturn Prefix(AbstractCard _instance) {
            return SpireReturn.Return(null);
        }
    }

}
