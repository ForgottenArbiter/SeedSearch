package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.Exordium;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.ArrayList;

@SpirePatch(
        clz= Exordium.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractPlayer.class, ArrayList.class}
)
public class ExordiumPatch {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(NewExpr m) throws CannotCompileException {
                if (m.getClassName().equals("com.megacrit.cardcrawl.scenes.TheBottomScene")) {
                    m.replace("{$_ = $0;}");
                } else if (m.getClassName().equals("com.megacrit.cardcrawl.neow.NeowRoom")) {
                    m.replace("{$_ = $0;}");
                }
            }

            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("randomizeScene")) {
                    m.replace("{}");
                }
            }
        };
    }
}
