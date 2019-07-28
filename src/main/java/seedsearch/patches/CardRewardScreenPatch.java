package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import seedsearch.Reward;
import seedsearch.SeedRunner;

import java.util.ArrayList;

public class CardRewardScreenPatch {

    public static ArrayList<AbstractCard> rewardCards;

    @SpirePatch(
            clz= CardRewardScreen.class,
            method="open"
    )
    public static class Patch {
        public static void Replace(CardRewardScreen _instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            rewardCards = cards;
        }
    }
}
