package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.SaveHelper;

@SpirePatch(
        clz = SaveHelper.class,
        method = "shouldSave"
)
public class SaveHelperPatch {

    public static boolean Replace() {
        return false;
    }
}
