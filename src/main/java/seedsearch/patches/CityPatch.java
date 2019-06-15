package seedsearch.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz= TheCity.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractPlayer.class, ArrayList.class}
)
public class CityPatch {

    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(NewExpr m) throws CannotCompileException {
                if (m.getClassName().equals("com.megacrit.cardcrawl.scenes.TheCityScene")) {
                    m.replace("{$_ = $0;}");
                }
            }
        };
    }

}
