package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;

@SpirePatch(
        clz= EventHelper.class,
        method="getEvent"
)
public class EventHelperPatch {

    public static String eventName = "";

    public static void Prefix(String key) {
        eventName = key;
    }
}
