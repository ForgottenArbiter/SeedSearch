package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz= Exordium.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractPlayer.class, ArrayList.class}
)
/*public class ExordiumPatch {

    @SpireInsertPatch(
            rloc=2
    )
    public static SpireReturn Insert(Exordium _instance, AbstractPlayer arg1, ArrayList arg2) {
        System.out.println("Hi");
        return SpireReturn.Return(null);
    }
}*/

public class ExordiumPatch {

    public static SpireReturn Prefix(Exordium _instance) {
        try {
            Method initRelicMethod = Exordium.class.getSuperclass().getDeclaredMethod("initializeRelicList");
            initRelicMethod.setAccessible(true);
            initRelicMethod.invoke(_instance);
            _instance.initializeSpecialOneTimeEventList();
            Method initLevelMethod = Exordium.class.getSuperclass().getDeclaredMethod("initializeLevelSpecificChances");
            initLevelMethod.setAccessible(true);
            initLevelMethod.invoke(_instance);
            AbstractDungeon.mapRng = new Random(Settings.seed + (long) AbstractDungeon.actNum);
            Method mapMethod = Exordium.class.getSuperclass().getDeclaredMethod("generateMap");
            mapMethod.setAccessible(true);
            mapMethod.invoke(null);
            AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
            AbstractDungeon.currMapNode.room = new EmptyRoom();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return SpireReturn.Return(null);
    }
}
