package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz= ShowCardAndObtainEffect.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCard.class, float.class, float.class, boolean.class}
)
public class ShowCardAndObtainEffectPatch {

    public static ArrayList<AbstractCard> obtainedCards = new ArrayList<>();

    public static void resetCards() {
        obtainedCards = new ArrayList<>();
    }

    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(ShowCardAndObtainEffect _instance, AbstractCard card, float x, float y, boolean convergeCards) {
        if (!_instance.isDone) {
            obtainedCards.add(card);
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(UnlockTracker.class, "markCardAsSeen");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }
}
