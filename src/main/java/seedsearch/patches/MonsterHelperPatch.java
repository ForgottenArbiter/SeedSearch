package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.EnemyData;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.metrics.BotDataUploader;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=MonsterHelper.class,
        method="uploadEnemyData"
)
public class MonsterHelperPatch {

    public static ArrayList<String> ids;

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"data"}
    )
    public static SpireReturn Insert(ArrayList<EnemyData> data) {
        ids = new ArrayList<>();
        for (EnemyData enemy : data) {
            ids.add(enemy.name);
        }
        return SpireReturn.Return(null);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(BotDataUploader.class, "uploadDataAsync");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }

}
