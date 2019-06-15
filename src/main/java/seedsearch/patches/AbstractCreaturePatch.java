package seedsearch.patches;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

@SpirePatch(
        clz= AbstractCreature.class,
        method="loadAnimation"
)
public class AbstractCreaturePatch {

    public static void Replace(AbstractCreature _instance, String atlasUrl, String skeletonUrl, float scale) {

        _instance.state = new AnimationState();

        try {
            Field data = AbstractCreature.class.getDeclaredField("stateData");
            data.setAccessible(true);
            data.set(_instance, mock(AnimationStateData.class));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


}
