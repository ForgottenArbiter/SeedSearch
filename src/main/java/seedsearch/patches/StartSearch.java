package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.DisplayOption;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;
import seedsearch.SeedSearch;

import java.util.ArrayList;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="create"
)
public class StartSearch {

    public static void Prefix(CardCrawlGame _instance) {
        Settings.displayOptions = new ArrayList<>();
        Settings.displayOptions.add(new DisplayOption(0,0));
    }

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn Insert(CardCrawlGame _instance) {
        LoadImagePatch.defaultTexture = ImageMaster.loadImage("images/npcs/rug/eng.png");
        SeedSearch.search();
        return SpireReturn.Return(null);
    }

    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(NewExpr m) throws CannotCompileException {
                if (m.getClassName().equals("com.badlogic.gdx.graphics.g2d.SpriteBatch")
                        || m.getClassName().equals("com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch")) {
                    m.replace("{$_ = $0;}");
                }
            }
        };
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "tips");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
        }
    }
}
