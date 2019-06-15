package seedsearch;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Reward {

    public int floor;
    public ArrayList<AbstractCard> cards;
    public ArrayList<String> relics;

    public Reward(int floor, ArrayList<AbstractCard> cards, ArrayList<String> relics) {
        this.floor = floor;
        this.cards = cards;
        this.relics = relics;
    }

    public Reward(int floor) {
        this(floor, new ArrayList<AbstractCard>(), new ArrayList<String>());
    }

    public static Reward makeCardReward(int floor, ArrayList<AbstractCard> cards) {
        return new Reward(floor, cards, new ArrayList<>());
    }

    public static Reward makeRelicReward(int floor, ArrayList<String> relics) {
        return new Reward(floor, new ArrayList<>(), relics);
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

    public boolean isEmpty() {
        return cards.isEmpty() && relics.isEmpty();
    }
}
