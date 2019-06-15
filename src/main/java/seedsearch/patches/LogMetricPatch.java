package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;

import java.util.List;

@SpirePatch(
        clz= AbstractEvent.class,
        method="logMetric",
        paramtypez = {String.class, String.class, List.class, List.class, List.class, List.class, List.class,
                List.class, List.class, int.class, int.class, int.class, int.class, int.class, int.class}
)
public class LogMetricPatch {
    public void Replace(String eventName, String playerChoice, List<String> cardsObtained, List<String> cardsRemoved,
                        List<String> cardsTransformed, List<String> cardsUpgraded, List<String> relicsObtained,
                        List<String> potionsObtained, List<String> relicsLost, int damageTaken, int damageHealed,
                        int hpLoss, int hpGain, int goldGain, int goldLoss) {

    }
}
