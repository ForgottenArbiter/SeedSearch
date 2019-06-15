package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.TheCity;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

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
