package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class AbstractCardPatch {

    @SpirePatch(
            clz= AbstractCard.class,
            method="initializeDescription"
    )
    public static class InitializeDescriptionPatch {
        public static void Replace(AbstractCard _instance) {
            //Do nothing
        }
    }

}
