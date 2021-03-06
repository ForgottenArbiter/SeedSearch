package seedsearch;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

public class SeedResult {

    private ArrayList<Reward> miscRewards;
    private ArrayList<Reward> shopRewards;
    private ArrayList<Reward> cardRewards;
    private ArrayList<NeowReward> neowRewards;
    private ArrayList<String> events;
    private ArrayList<String> bosses;
    private ArrayList<String> monsters;
    private ArrayList<String> mapPath;
    private ArrayList<String> trueMapPath;
    private ArrayList<String> bossRelics;
    private ArrayList<String> relics;
    private ArrayList<String> rawCommonRelics;
    private ArrayList<String> rawUncommonRelics;
    private ArrayList<String> rawRareRelics;
    private ArrayList<String> rawBossRelics;
    private ArrayList<String> rawShopRelics;
    private int numElites;
    private int numCombats;
    private int numRestSites;
    private long seed;

    public SeedResult(long seed) {
        this.seed = seed;
        this.miscRewards = new ArrayList<>();
        this.shopRewards = new ArrayList<>();
        this.cardRewards = new ArrayList<>();
        this.neowRewards = new ArrayList<>();
        this.events = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.mapPath = new ArrayList<>();
        this.trueMapPath = new ArrayList<>();
        this.bossRelics = new ArrayList<>();
        this.relics = new ArrayList<>();
    }

    public void addCardReward(int floor, ArrayList<AbstractCard> cards) {
        Reward reward = Reward.makeCardReward(floor, cards);
        cardRewards.add(reward);
    }

    public void addCardReward(Reward reward) {
        cardRewards.add(reward);
    }

    public void addAllCardRewards(ArrayList<Reward> rewards) {
        cardRewards.addAll(rewards);
    }

    public void addMiscReward(Reward reward) {
        miscRewards.add(reward);
    }

    public void addShopReward(Reward reward) {
        shopRewards.add(reward);
    }

    public void addNeowRewards(ArrayList<NeowReward> neowRewards) {
        this.neowRewards = neowRewards;
    }

    public void registerCombat(String monsterName) {
        numCombats += 1;
        monsters.add(monsterName);
    }

    public void registerEliteCombat(String monsterName) {
        numElites += 1;
        registerCombat(monsterName);
    }

    public void registerBossCombat(String monsterName) {
        bosses.add(monsterName);
        registerCombat(monsterName);
    }

    public void registerEvent(String eventName) {
        events.add(eventName);
    }

    public void countRestSite() {
        numRestSites++;
    }

    public void addBossReward(ArrayList<String> bossRelics) {
        this.bossRelics.addAll(bossRelics);
    }

    public void addToMapPath(String mapSymbol) {
        mapPath.add(mapSymbol);
    }

    public void addToTrueMapPath(String mapSymbol) {
        trueMapPath.add(mapSymbol);
    }

    public void updateRelics() {
        relics = new ArrayList<>();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            relics.add(relic.relicId);
        }
    }

    public void SetCommonRelicPool(ArrayList<String> relics){
        rawCommonRelics = new ArrayList<>(relics);
    }

    public void SetUncommonRelicPool(ArrayList<String> relics){
        rawUncommonRelics = new ArrayList<>(relics);
    }

    public void SetRareRelicPool(ArrayList<String> relics){
        rawRareRelics = new ArrayList<>(relics);
    }

    public void SetBossRelicPool(ArrayList<String> relics){
        rawBossRelics = new ArrayList<>(relics);
    }

    public void SetShopRelicPool(ArrayList<String> relics){
        rawShopRelics = new ArrayList<>(relics);
    }

    public boolean testFinalFilters(SearchSettings settings) {
        if (numCombats > settings.maximumCombats) {
            return false;
        }
        if (numCombats < settings.minimumCombats) {
            return false;
        }
        if (numElites > settings.maximumElites) {
            return false;
        }
        if (numElites < settings.minimumElites) {
            return false;
        }
        if (numRestSites < settings.minimumRestSites) {
            return false;
        }
        if (!events.containsAll(settings.requiredEvents)) {
            return false;
        }
        if (!relics.containsAll(settings.requiredRelics)) {
            return false;
        }
        if (!monsters.containsAll(settings.requiredCombats)) {
            return false;
        }
        ArrayList<String> allPotions = getAllPotionIds();
        for (String potion : settings.requiredPotions) {
            if (allPotions.contains(potion)){
                allPotions.remove(potion);
            } else{
                return false;
            }
        }
        return true;
    }

    public boolean testAct1Filters(SearchSettings settings) {
        if (!relics.containsAll(settings.requiredAct1Relics)) {
            return false;
        }
        ArrayList<String> allCards = getAllCardIds();
        for (String card : settings.bannedAct1Cards) {
            if (allCards.contains(card)) {
                return false;
            }
        }
        for (String card : settings.requiredAct1Cards) {
            if (allCards.contains(card)) {
                allCards.remove(card);
            } else {
                return false;
            }
        }
        ArrayList<String> allPotions = getAllPotionIds();
        for (String potion : settings.requiredAct1Potions) {
            if (allPotions.contains(potion)){
                allPotions.remove(potion);
            } else{
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getAllCardIds() {
        ArrayList<String> allCards = new ArrayList<>();
        for (Reward reward : cardRewards) {
            for (AbstractCard card : reward.cards) {
                allCards.add(card.cardID);
            }
        }
        for (Reward reward : miscRewards) {
            for (AbstractCard card : reward.cards) {
                allCards.add(card.cardID);
            }
        }
        return allCards;
    }

    private ArrayList<String> getAllPotionIds() {
        ArrayList<String> allPotions = new ArrayList<>();
        for (Reward reward : miscRewards) {
            for (AbstractPotion potion : reward.potions){
                allPotions.add(potion.ID);
            }
        }
        return allPotions;
    }

    private static String removeTextFormatting(String text) {
        text = text.replaceAll("~|@(\\S+)~|@", "$1");
        return text.replaceAll("#.|NL", "");
    }

    public void printSeedStats(SearchSettings settings) {
        ArrayList<String> shopRelics = new ArrayList<>();
        ArrayList<String> shopCards = new ArrayList<>();
        ArrayList<String> shopPotions = new ArrayList<>();
        for (Reward shopReward : shopRewards) {
            shopRelics.addAll(shopReward.relics);
            for (AbstractCard card : shopReward.cards) {
                shopCards.add(card.name);
            }
            for (AbstractPotion potion : shopReward.potions)
            {
                shopPotions.add(potion.name);
            }
        }

        System.out.println(MessageFormat.format("Seed: {0} ({1})", SeedHelper.getString(seed), seed));
        if (settings.showNeowOptions) {
            System.out.println("Neow Options:");
            for (NeowReward reward : neowRewards) {
                System.out.println(removeTextFormatting(reward.optionLabel));
            }
        }
        if (settings.showCombats) {
            System.out.println(MessageFormat.format("{0} combats ({1} elite(s)):", numCombats, numElites));
            System.out.println(monsters);
        }
        if (settings.showBosses) {
            System.out.println("Bosses:");
            System.out.println(bosses);
        }
        if (settings.showBossRelics) {
            System.out.println("Boss relics:");
            System.out.println(bossRelics);
        }
        if (settings.showRelics) {
            System.out.println(MessageFormat.format("{0} relics:", relics.size()));
            System.out.println(relics);
        }
        if (settings.showShopRelics) {
            System.out.println("Shop relics:");
            System.out.println(shopRelics);
        }
        if (settings.showShopCards) {
            System.out.println("Shop cards:");
            System.out.println(shopCards);
        }
        if (settings.showShopPotions) {
            System.out.println("Shop potions:");
            System.out.println(shopPotions);
        }
        if (settings.showEvents) {
            System.out.println("Events:");
            System.out.println(events);
        }
        System.out.println("Map path:");
        System.out.println(mapPath);
        System.out.println("True map path:");
        ArrayList<String> combinedMapPath = new ArrayList<>();
        for (int i = 0; i < mapPath.size(); i++) {
            String mapPathItem = mapPath.get(i);
            String trueItem = trueMapPath.get(i);
            if (mapPathItem.equals(trueItem)) {
                combinedMapPath.add(trueItem);
            } else {
                combinedMapPath.add(String.format("%s/%s", mapPathItem, trueItem));
            }
        }
        System.out.println(combinedMapPath);
        if (settings.showCardChoices) {
            System.out.println("Card choices:");
            for (Reward reward : cardRewards) {
                if (reward.cards.size() > 0) {
                    System.out.println(String.format("Floor %d: %s", reward.floor, reward.cards));
                }
            }
        }
        if (settings.showPotions) {
            System.out.println("Potions:");
            for (Reward reward : miscRewards) {
                if (reward.potions.size() > 0) {
                    ArrayList<String> potionNames = new ArrayList<>();
                    for (AbstractPotion potion : reward.potions) {
                        potionNames.add(potion.name);
                    }
                    System.out.println(String.format("Floor %d: %s", reward.floor, potionNames));
                }
            }
        }
        if (settings.showOtherCards) {
            System.out.println("Other cards:");
            for (Reward reward : miscRewards) {
                if (reward.cards.size() > 0) {
                    System.out.println(String.format("Floor %d: %s", reward.floor, reward.cards));
                }
            }
        }
        if (settings.showRawRelicPools) {
            System.out.println("Raw common relic list:");
            System.out.println(rawCommonRelics);
            System.out.println("Raw uncommon relic list:");
            System.out.println(rawUncommonRelics);
            System.out.println("Raw rare relic list:");
            System.out.println(rawRareRelics);
            System.out.println("Raw boss relic list:");
            System.out.println(rawBossRelics);
            System.out.println("Raw shop relic list:");
            System.out.println(rawShopRelics);
        }
        System.out.println("#####################################");
    }
}
