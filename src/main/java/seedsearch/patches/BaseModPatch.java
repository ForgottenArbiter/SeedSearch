package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls="basemod.BaseMod",
        method="setupAnimationGfx",
        optional=true
)
public class BaseModPatch {

    public static void Replace() {
        // Do nothing
    }

}
