package seedsearch.patches;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static seedsearch.SeedSearch.loadingEnabled;

public class LoadImagePatch {

    public static Texture defaultTexture;

    @SpirePatch(
            clz= ImageMaster.class,
            method="loadImage",
            paramtypez = {String.class}
    )
    public static class Patch1 {
        public static SpireReturn<Texture> Prefix() {
            if(loadingEnabled) {
                return SpireReturn.Continue();
            } else {
                return SpireReturn.Return(defaultTexture);
            }
        }
    }

    @SpirePatch(
            clz= ImageMaster.class,
            method="loadImage",
            paramtypez = {String.class, boolean.class}
    )
    public static class Patch2 {
        public static SpireReturn<Texture> Prefix() {
            if(loadingEnabled) {
                return SpireReturn.Continue();
            } else {
                return SpireReturn.Return(defaultTexture);
            }
        }
    }
}
