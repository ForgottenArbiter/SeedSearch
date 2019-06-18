package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.screens.DisplayOption;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seedsearch.SeedSearch;

import java.util.ArrayList;

import static org.apache.logging.log4j.Level.OFF;

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
        Class[] noLoggingClasses = {
                AbstractDungeon.class,
                AbstractPlayer.class,
                TheEnding.class,
                UnlockTracker.class,
                RoomTypeAssigner.class,
                CardLibrary.class,
                EventHelper.class,
                ShopScreen.class,
                CardGroup.class,
                CardHelper.class
        };

        for (Class c : noLoggingClasses) {
            Logger logger = LogManager.getLogger(c);
            ((org.apache.logging.log4j.core.Logger)logger).setLevel(OFF);
        }

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
