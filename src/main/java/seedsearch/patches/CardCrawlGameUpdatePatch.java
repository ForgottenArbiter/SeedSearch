package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import seedsearch.SeedSearch;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="update"
)
public class CardCrawlGameUpdatePatch {

    @SpireInsertPatch(
            locator=Locator.class
    )
    public static SpireReturn Insert(CardCrawlGame _instance) {
        InputHelper.updateFirst();
        SeedSearch.screen.update();
        //ReflectionHacks.setPrivate(_instance, CardCrawlGame.class, "mode", CardCrawlGame.GameMode.DUNGEON_TRANSITION);
        try {
            Field targetField = CardCrawlGame.class.getDeclaredField("mode");
            targetField.setAccessible(true);
            targetField.set(_instance, CardCrawlGame.GameMode.DUNGEON_TRANSITION);
        } catch (Exception e) {
            throw new RuntimeException("Reflection error.");
        }
        InputHelper.updateLast();
        return SpireReturn.Return(null);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(InputHelper.class, "updateFirst");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
