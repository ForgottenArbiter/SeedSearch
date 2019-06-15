package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import seedsearch.Reward;
import seedsearch.SeedRunner;

import java.util.ArrayList;

public class CombatRewardScreenPatch {

    @SpirePatch(
            clz= CombatRewardScreen.class,
            method="open",
            paramtypez = {}
    )
    public static class Patch1 {
        public static void Replace(CombatRewardScreen _instance) {
        extractRewards(_instance);
    }
    }

    @SpirePatch(
            clz= CombatRewardScreen.class,
            method="open",
            paramtypez = {String.class}
    )
    public static class Patch2 {
        public static void Replace(CombatRewardScreen _instance, String _arg) {
            extractRewards(_instance);
        }
    }

    @SpirePatch(
            clz= CombatRewardScreen.class,
            method="openCombat",
            paramtypez = {String.class, boolean.class}
    )
    public static class Patch3 {
        public static void Replace(CombatRewardScreen _instance, String _arg1, boolean _arg2) {
            extractRewards(_instance);
        }
    }

    @SpirePatch(
            clz= CombatRewardScreen.class,
            method="openCombat",
            paramtypez = {String.class}
    )
    public static class Patch4 {
        public static void Replace(CombatRewardScreen _instance, String _arg) {
            extractRewards(_instance);
        }
    }

    public static void extractRewards(CombatRewardScreen _instance) {
        SeedRunner.combatGold = 0;
        SeedRunner.combatRelics = new ArrayList<>();
        SeedRunner.combatCardRewards = new ArrayList<>();
        _instance.setupItemReward();
        for (RewardItem item : _instance.rewards) {
            if (item.type == RewardItem.RewardType.RELIC) {
                SeedRunner.combatRelics.add(item.relic);
            } else if (item.type == RewardItem.RewardType.CARD) {
                SeedRunner.combatCardRewards.add(Reward.makeCardReward(AbstractDungeon.floorNum, item.cards));
            } else if (item.type == RewardItem.RewardType.GOLD) {
                SeedRunner.combatGold = item.goldAmt;
            }
        }
    }

}
