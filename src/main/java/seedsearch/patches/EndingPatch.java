package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import java.util.ArrayList;

@SpirePatch(
        clz= TheEnding.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractPlayer.class, ArrayList.class}
)
public class EndingPatch {

    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(NewExpr m) throws CannotCompileException {
                if (m.getClassName().equals("com.megacrit.cardcrawl.scenes.TheEndingScene")) {
                    m.replace("{$_ = $0;}");
                }
            }
        };
    }

}
