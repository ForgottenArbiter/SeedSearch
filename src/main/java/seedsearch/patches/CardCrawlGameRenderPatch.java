package seedsearch.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import seedsearch.SeedSearch;
import seedsearch.SeedSearchScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="render"
)
public class CardCrawlGameRenderPatch {

    /*@SpireInsertPatch(
            locator=Locator.class,
            localvars={"sb"}
    )
    public static void Insert(CardCrawlGame _instance, SpriteBatch sb) {
        SeedSearch.screen.render(sb);
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher matcher = new Matcher.MethodCallMatcher(DrawMaster.class, "draw");
            int[] locations = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher);
            for(int i = 0; i < locations.length; i++) {
                locations[i] -= 1;
            }
            return locations;
        }
    }*/

    public static void Replace(CardCrawlGame _instance) {
    }

    /*public static void Replace(CardCrawlGame _instance) {
        try {
            _instance.update();



            SpriteBatch sb;
            OrthographicCamera camera;
            Field sbField = CardCrawlGame.class.getDeclaredField("sb");
            sbField.setAccessible(true);
            sb = (SpriteBatch)sbField.get(_instance);
            Field cameraField = CardCrawlGame.class.getDeclaredField("camera");
            cameraField.setAccessible(true);
            camera = (OrthographicCamera)cameraField.get(_instance);

            sb.setProjectionMatrix(camera.combined);
            CardCrawlGame.psb.setProjectionMatrix(camera.combined);
            Gdx.gl.glClear(16384);
            sb.begin();
            SeedSearch.screen.render(sb);
            TipHelper.render(sb);
            GameCursor.hidden = false;
            Settings.isControllerMode = false;
            CardCrawlGame.cursor.render(sb);
            sb.end();

        } catch(Exception e) {
            throw new RuntimeException("Unrecoverable error in update.");
        }
    }*/
}
