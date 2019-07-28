package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class AbstractRoomPatch {

    public static AbstractRelic obtainedRelic;

    @SpirePatch(
            clz=AbstractRoom.class,
            method="spawnRelicAndObtain"
    )
    public static class Patch {
        public static void Replace(AbstractRoom _instance, float x, float y, AbstractRelic relic) {
            AbstractRoomPatch.obtainedRelic = relic;
        }
    }

}
