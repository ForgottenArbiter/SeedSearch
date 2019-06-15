package seedsearch.patches;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;

import static seedsearch.SeedSearch.loadingEnabled;

public class TextureAtlasPatch {

    @SpirePatch(
            clz=TextureAtlas.class,
            method="findRegion",
            paramtypez = {String.class}
    )
    public static class FindRegionPatch1{
        public static SpireReturn<TextureAtlas.AtlasRegion> Prefix() {
            if(loadingEnabled) {
                return SpireReturn.Continue();
            } else {
                return SpireReturn.Return(null);
            }
        }
    }

    @SpirePatch(
            clz=TextureAtlas.class,
            method="findRegion",
            paramtypez = {String.class, int.class}
    )
    public static class FindRegionPatch2{
        public static SpireReturn<TextureAtlas.AtlasRegion> Prefix() {
            if(loadingEnabled) {
                return SpireReturn.Continue();
            } else {
                return SpireReturn.Return(null);
            }
        }
    }
}
