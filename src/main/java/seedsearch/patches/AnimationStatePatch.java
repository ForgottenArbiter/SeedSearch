package seedsearch.patches;

import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        clz= AnimationState.class,
        method="setAnimation",
        paramtypez = {int.class, String.class, boolean.class}
)
public class AnimationStatePatch {

    public static AnimationState.TrackEntry Replace(AnimationState _instance, int arg1, String arg2, boolean arg3) {
        return new AnimationState.TrackEntry();
    }

}
