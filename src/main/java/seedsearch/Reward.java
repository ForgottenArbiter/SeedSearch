package seedsearch;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public class Reward {

    public int floor;
    public ArrayList<AbstractCard> cards;
    public ArrayList<String> relics;
    public ArrayList<AbstractPotion> potions;

    public Reward(int floor, ArrayList<AbstractCard> cards, ArrayList<String> relics, ArrayList<AbstractPotion> potions) {
        this.floor = floor;
        this.cards = cards;
        this.relics = relics;
        this.potions = potions;
    }

    public Reward(int floor) {
        this(floor, new ArrayList<AbstractCard>(), new ArrayList<String>(), new ArrayList<>());
    }

    public static Reward makeCardReward(int floor, ArrayList<AbstractCard> cards) {
        return new Reward(floor, cards, new ArrayList<>(), new ArrayList<>());
    }

    public static Reward makeRelicReward(int floor, ArrayList<String> relics) {
        return new Reward(floor, new ArrayList<>(), relics, new ArrayList<>());
    }

    public static Reward makePotionReward(int floor, ArrayList<AbstractPotion> potions) {
        return new Reward(floor, new ArrayList<>(), new ArrayList<>(), potions);
    }

    public void addCard(AbstractCard card) {
        this.cards.add(card);
    }

    public void addCards(ArrayList<AbstractCard> cards) {
        this.cards.addAll(cards);
    }

    public void addRelic(String relic) {
        this.relics.add(relic);
    }

    public void addRelics(ArrayList<String> relics) {
        this.relics.addAll(relics);
    }

    public void addPotion(AbstractPotion potion) {
        this.potions.add(potion);
    }

    public void addPotions(ArrayList<AbstractPotion> potions) {
        this.potions.addAll(potions);
    }

    public boolean isEmpty() {
        return cards.isEmpty() && relics.isEmpty() && potions.isEmpty();
    }
}
