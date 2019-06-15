package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AnimatedNpc;

@SpirePatch(
        clz=AnimatedNpc.class,
        method=SpirePatch.CONSTRUCTOR
)
public class AnimatedNpcPatch {
    public static SpireReturn Prefix(AnimatedNpc _instance) {
        return SpireReturn.Return(null);
    }
}
