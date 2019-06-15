package seedsearch;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import seedsearch.patches.EventHelperPatch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;


public class SeedRunner {

    public static ArrayList<AbstractRelic> combatRelics;
    public static ArrayList<Reward> combatCardRewards;
    public static int combatGold = 0;

    private AbstractPlayer player;
    private int currentAct;
    private int actFloor;
    private int bootsCharges = 0;

    private ArrayList<Reward> rewards;
    private ArrayList<Reward> shopRewards;
    private ArrayList<String> events;
    private ArrayList<String> bosses;
    private ArrayList<String> monsters;
    private ArrayList<String> mapPath;
    private ArrayList<String> bossRelics;
    private int numElites;
    private int numCombats;

    private SearchSettings settings;
    private long currentSeed;

    public SeedRunner(SearchSettings settings) {
        this.settings = settings;
        AbstractDungeon.fadeColor = Settings.SHADOW_COLOR;
        CharacterManager characterManager = new CharacterManager();
        characterManager.setChosenCharacter(settings.playerClass);
        currentSeed = settings.startSeed;
        AbstractDungeon.ascensionLevel = settings.ascensionLevel;
    }

    private void setSeed(long seed) {
        Settings.seed = seed;
        currentSeed = seed;
        AbstractDungeon.generateSeeds();
        player = AbstractDungeon.player;
        AbstractDungeon.reset();
        resetCharacter();

        currentAct = 0;
        actFloor = 0;
        rewards = new ArrayList<>();
        events = new ArrayList<>();
        bosses = new ArrayList<>();
        monsters = new ArrayList<>();
        mapPath = new ArrayList<>();
        shopRewards = new ArrayList<>();
        bossRelics = new ArrayList<>();
        numElites = 0;
        numCombats = 0;
    }

    private void resetCharacter() {
        player.relics = new ArrayList<>();
        player.potions = new ArrayList<>();
        player.masterDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        CharSelectInfo info = player.getLoadout();
        player.maxHealth = info.maxHp;
        player.gold = info.gold;
    }

    private boolean runSeed() {

        if (!settings.speedrunPace) {
            CardCrawlGame.playtime = 900F;
        } else {
            CardCrawlGame.playtime = 0F;
        }

        AbstractDungeon exordium = new Exordium(player, new ArrayList<String>());
        ArrayList<MapRoomNode> exordiumPath = findMapPath(AbstractDungeon.map);
        tradeStarterRelic();
        runPath(exordium, exordiumPath);
        getBossRewards();

        if (!testAct1Filters()) {
            return false;
        }

        currentAct += 1;
        actFloor = 0;
        AbstractDungeon city = new TheCity(player, AbstractDungeon.specialOneTimeEventList);
        ArrayList<MapRoomNode> cityPath = findMapPath(AbstractDungeon.map);
        runPath(city, cityPath);
        getBossRewards();


        currentAct += 1;
        actFloor = 0;
        AbstractDungeon beyond = new TheBeyond(player, AbstractDungeon.specialOneTimeEventList);
        ArrayList<MapRoomNode> beyondPath = findMapPath(AbstractDungeon.map);
        runPath(beyond, beyondPath);
        getBossRewards();

        if (settings.act4) {
            currentAct += 1;
            actFloor = 0;
            AbstractDungeon end = new TheEnding(player, AbstractDungeon.specialOneTimeEventList);
            ArrayList<MapRoomNode> endPath = new ArrayList<>();
            endPath.add(AbstractDungeon.map.get(0).get(3));
            endPath.add(AbstractDungeon.map.get(1).get(3));
            endPath.add(AbstractDungeon.map.get(2).get(3));
            runPath(end, endPath);
            getBossRewards();
        }

        return testFinalFilters();
    }

    public boolean runSeed(long seed) {
        setSeed(seed);
        return runSeed();
    }

    private boolean testFinalFilters() {
        if(numCombats > settings.maximumCombats) {
            return false;
        }
        if(numCombats < settings.minimumCombats) {
            return false;
        }
        if(numElites > settings.maximumElites) {
            return false;
        }
        if(numElites < settings.minimumElites) {
            return false;
        }
        if (!events.containsAll(settings.requiredEvents)) {
            return false;
        }
        ArrayList<String> relicStrings = getAllRelicIds();
        if (!relicStrings.containsAll(settings.requiredRelics)) {
            return false;
        }
        return true;
    }

    private boolean testAct1Filters() {
        ArrayList<String> relicStrings = getAllRelicIds();
        if (!relicStrings.containsAll(settings.requiredAct1Relics)) {
            return false;
        }
        ArrayList<String> allCards = getAllCardIds();
        if (!allCards.containsAll(settings.requiredAct1Cards)) {
            return false;
        }
        return true;
    }

    private ArrayList<String> getAllRelicIds() {
        ArrayList<String> relicStrings = new ArrayList<>();
        for (AbstractRelic relic : player.relics) {
            relicStrings.add(relic.relicId);
        }
        return relicStrings;
    }

    private ArrayList<String> getAllCardIds() {
        ArrayList<String> allCards = new ArrayList<>();
        for (Reward reward : rewards) {
            for (AbstractCard card : reward.cards) {
                allCards.add(card.cardID);
            }
        }
        for (AbstractCard card : player.masterDeck.group) {
            allCards.add(card.cardID);
        }
        return allCards;
    }

    private void tradeStarterRelic() {
        String bossRelic = AbstractDungeon.returnEndRandomRelicKey(AbstractRelic.RelicTier.BOSS);
        Reward neowRewards = new Reward(0);
        awardRelic(bossRelic, neowRewards);
        rewards.add(neowRewards);
    }

    private void awardRelic(String relic, Reward reward) {
        reward.addRelic(relic);
        AbstractRelic realRelic = RelicLibrary.getRelic(relic);
        doRelicPickupLogic(realRelic, reward);
    }

    private void awardRelic(AbstractRelic relic, Reward reward) {
        String relicKey = relic.relicId;
        reward.addRelic(relicKey);
        doRelicPickupLogic(relic, reward);
    }

    private void doRelicPickupLogic(AbstractRelic relic, Reward reward) {
        this.player.relics.add(relic);
        String relicKey = relic.relicId;
        switch(relicKey) {
            case TinyHouse.ID:
                //TODO: Handle Tiny House
                break;
            case WingBoots.ID:
                this.bootsCharges = 3;
                break;
            case CallingBell.ID:
                for(int i = 0; i < 3; i++) {
                    AbstractCard newCard = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
                    reward.addCard(newCard);
                    player.masterDeck.addToBottom(newCard);
                }
                String relic1 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.COMMON);
                String relic2 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.UNCOMMON);
                String relic3 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.RARE);
                awardRelic(relic1, reward);
                awardRelic(relic2, reward);
                awardRelic(relic3, reward);
                break;
            case PandorasBox.ID:
                int count = 0;
                for(AbstractCard card : player.masterDeck.group) {
                    if ((card.cardID.equals("Strike_R")) || (card.cardID.equals("Strike_G")) || (card.cardID.equals("Strike_B")) ||
                            (card.cardID.equals("Defend_R")) || (card.cardID.equals("Defend_G")) || (card.cardID.equals("Defend_B"))) {
                        count += 1;
                    }
                }
                for(int i = 0; i < count; i++) {
                    AbstractCard newCard = AbstractDungeon.returnTrulyRandomCard().makeCopy();
                    reward.addCard(newCard);
                    player.masterDeck.addToBottom(newCard);
                }
                break;
            case Necronomicon.ID:
                AbstractCard curse = new Necronomicurse();
                reward.addCard(curse);
                player.masterDeck.addToBottom(curse);
                break;
        }
    }

    private ArrayList<MapRoomNode> findBootsPath(ArrayList<ArrayList<MapRoomNode>> map) {
        float[][] weights = new float[15][7];
        float[][][] pathWeights = new float[15][7][4];
        ArrayList<ArrayList<ArrayList<MapRoomNode>>> parents = new ArrayList<>();
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 7; j++) {
                weights[i][j] = getRoomScore(map.get(i).get(j).room);
                for(int k = 0; k < 4; k++) {
                    if (i == 0) {
                        pathWeights[i][j][k] = weights[i][j];
                    } else {
                        pathWeights[i][j][k] = 100000f;
                    }
                }
            }
        }
        for(int floor = 0; floor < 14; floor++) {
            ArrayList<ArrayList<MapRoomNode>> floorParents = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {
                ArrayList<MapRoomNode> floorSubList = new ArrayList<MapRoomNode>(7);
                for(int j = 0; j < 7; j++) {
                    floorSubList.add(null);
                }
                floorParents.add(floorSubList);
            }
            for(int x = 0; x < 7; x++) {
                MapRoomNode node = map.get(floor).get(x);
                if(node.room == null) {
                    continue;
                }
                ArrayList<MapEdge> edges = node.getEdges();
                ArrayList<Integer> nextXs = new ArrayList<>(0);
                for (MapEdge edge : edges) {
                    int targetX = edge.dstX;
                    nextXs.add(targetX);
                }
                for(int nx = 0; nx < 7; nx++) {
                    for(int wing_uses = 0; wing_uses <= bootsCharges ; wing_uses++) {
                        if (nextXs.contains(nx)) {
                            float testWeight = weights[floor+1][nx] + pathWeights[floor][x][wing_uses];
                            if(testWeight < pathWeights[floor+1][nx][wing_uses]) {
                                pathWeights[floor+1][nx][wing_uses] = testWeight;
                                floorParents.get(wing_uses).set(nx, node);
                            }
                        } else if (wing_uses > 0) {
                            float testWeight = weights[floor+1][nx] + pathWeights[floor][x][wing_uses-1];
                            if(testWeight < pathWeights[floor+1][nx][wing_uses]) {
                                pathWeights[floor+1][nx][wing_uses] = testWeight;
                                floorParents.get(wing_uses).set(nx, node);
                            }
                        }
                    }
                }
            }
            parents.add(floorParents);
        }
        int[] best_top = {0,0,0,0};
        float[] best_score = {100000f, 100000f, 100000f, 100000f};
        for(int uses = 0; uses < 4; uses++) {
            for (int x = 0; x < 7; x++) {
                if (pathWeights[14][x][uses] < best_score[uses]) {
                    best_score[uses] = pathWeights[14][x][uses];
                    best_top[uses] = x;
                }
            }
        }
        int best_uses = 0;
        if(best_score[0] - best_score[1] >= settings.wingBootsThreshold) {
            best_uses = 1;
        }
        if(best_score[1] - best_score[2] >= settings.wingBootsThreshold) {
            best_uses = 2;
        }
        if(best_score[2] - best_score[3] >= settings.wingBootsThreshold) {
            best_uses = 3;
        }
        int cur_uses = best_uses;
        ArrayList<MapRoomNode> path = new ArrayList<MapRoomNode>(15);
        int next_x = best_top[cur_uses];
        path.add(map.get(14).get(best_top[cur_uses]));
        for(int y = 14; y > 0; y--) {
            MapRoomNode parent = parents.get(y-1).get(cur_uses).get(next_x);
            boolean isConnected = false;
            for(MapEdge edge : parent.getEdges()) {
                if(edge.dstX == next_x) {
                    isConnected = true;
                    break;
                }
            }
            if(!isConnected) {
                cur_uses -= 1;
            }
            path.add(0, parent);
            next_x = parent.x;
        }
        if(best_uses != 0) {
            this.bootsCharges -= best_uses;
        }
        return path;
    }

    private ArrayList<MapRoomNode> findMapPath(ArrayList<ArrayList<MapRoomNode>> map) {
        if(bootsCharges > 0) {
            return findBootsPath(map);
        }
        float[][] weights = new float[15][7];
        float[][] pathWeights = new float[15][7];
        ArrayList<ArrayList<MapRoomNode>> parents = new ArrayList<>();
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 7; j++) {
                weights[i][j] = getRoomScore(map.get(i).get(j).room);
                if(i == 0) {
                    pathWeights[i][j] = weights[i][j];
                } else {
                    pathWeights[i][j] = 100000f;
                }
            }
        }
        for(int floor = 0; floor < 14; floor++) {
            ArrayList<MapRoomNode> floorParents = new ArrayList<MapRoomNode>(7);
            for(int i = 0; i < 7; i++) {
                floorParents.add(null);
            }
            for(int x = 0; x < 7; x++) {
                MapRoomNode node = map.get(floor).get(x);
                if(node.room == null) {
                    continue;
                }
                ArrayList<MapEdge> edges = node.getEdges();
                for (MapEdge edge : edges) {
                    int targetX = edge.dstX;
                    float testWeight = weights[floor+1][targetX] + pathWeights[floor][x];
                    if (testWeight < pathWeights[floor+1][targetX]) {
                        pathWeights[floor+1][targetX] = testWeight;
                        floorParents.set(targetX, node);
                    }
                }
            }
            parents.add(floorParents);
        }
        int best_top = 0;
        float best_score = 100000f;
        for(int x = 0; x < 7; x++) {
            if(pathWeights[14][x] < best_score) {
                best_score = pathWeights[14][x];
                best_top = x;
            }
        }
        ArrayList<MapRoomNode> path = new ArrayList<MapRoomNode>(15);
        int next_x = best_top;
        path.add(map.get(14).get(best_top));
        for(int y = 14; y > 0; y--) {
            MapRoomNode parent = parents.get(y-1).get(next_x);
            path.add(0, parent);
            next_x = parent.x;
        }
        return path;
    }

    private float getRoomScore(AbstractRoom room) {
        if (room instanceof TreasureRoom) {
            return 0f;
        } else if (room instanceof MonsterRoomElite) {
            return settings.eliteRoomWeight;
        } else if (room instanceof MonsterRoom) {
            return settings.monsterRoomWeight;
        } else if (room instanceof RestRoom) {
            return settings.restRoomWeight;
        } else if (room instanceof ShopRoom) {
            return settings.shopRoomWeight;
        } else if (room instanceof EventRoom) {
            return settings.eventRoomWeight;
        } else {
            return 0f;
        }
    }

    public enum RoomType {
        EVENT, ELITE, MONSTER, SHOP, TREASURE, REST
    }

    private void runPath(AbstractDungeon dungeon, ArrayList<MapRoomNode> path) {
        int offset = currentAct * 17;
        for(actFloor = 1; actFloor < path.size(); actFloor++) {
            AbstractDungeon.floorNum += 1;
            AbstractDungeon.miscRng = new Random(currentSeed + (long)AbstractDungeon.floorNum);
            MapRoomNode node = path.get(actFloor - 1);
            RoomType result;
            if(node.room instanceof EventRoom) {
                result = RoomType.EVENT;
                mapPath.add("?");
            } else if(node.room instanceof MonsterRoomElite) {
                result = RoomType.ELITE;
                mapPath.add("E");
            } else if(node.room instanceof MonsterRoom) {
                result = RoomType.MONSTER;
                mapPath.add("M");
            } else if(node.room instanceof ShopRoom) {
                result = RoomType.SHOP;
                mapPath.add("S");
            } else if(node.room instanceof TreasureRoom) {
                result = RoomType.TREASURE;
                mapPath.add("T");
            } else {
                result = RoomType.REST;
                mapPath.add("R");
            }
            if(result == RoomType.EVENT) {
                EventHelper.RoomResult eventRoll = EventHelper.roll();
                switch (eventRoll) {
                    case ELITE:
                        result = RoomType.ELITE;
                        break;
                    case MONSTER:
                        result = RoomType.MONSTER;
                        break;
                    case SHOP:
                        result = RoomType.SHOP;
                        break;
                    case TREASURE:
                        result = RoomType.TREASURE;
                        break;
                }
            }
            AbstractDungeon.currMapNode = node;
            switch(result) {
                case EVENT:
                    Random eventRngDuplicate = new Random(Settings.seed, AbstractDungeon.eventRng.counter);
                    AbstractEvent event = AbstractDungeon.generateEvent(eventRngDuplicate);
                    String eventKey = EventHelperPatch.eventName;
                    Reward eventReward = getEventReward(event, eventKey, AbstractDungeon.floorNum);
                    events.add(eventKey);
                    if (!eventReward.isEmpty()) {
                        rewards.add(eventReward);
                    }
                    break;
                case MONSTER:
                    String monster = AbstractDungeon.monsterList.remove(0);
                    monsters.add(monster);
                    rewards.add(Reward.makeCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards()));
                    int gold = AbstractDungeon.treasureRng.random(10, 20);
                    addGoldReward(gold);
                    numCombats += 1;
                    break;
                case ELITE:
                    numElites += 1;
                    String elite= AbstractDungeon.eliteMonsterList.remove(0);
                    monsters.add(elite);
                    AbstractRelic.RelicTier tier = AbstractDungeon.returnRandomRelicTier();
                    String relic = AbstractDungeon.returnRandomRelicKey(tier);
                    Reward relicReward = new Reward(AbstractDungeon.floorNum);
                    awardRelic(relic, relicReward);
                    if(player.hasRelic(BlackStar.ID)) {
                        AbstractRelic starRelic = AbstractDungeon.returnRandomNonCampfireRelic(tier);
                        awardRelic(starRelic, relicReward);
                    }
                    rewards.add(relicReward);
                    rewards.add(Reward.makeCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards()));
                    gold = AbstractDungeon.treasureRng.random(25, 35);
                    addGoldReward(gold);
                    numCombats += 1;
                    break;
                case SHOP:
                    Merchant merchant = new Merchant();
                    Reward shopReward = getShopReward(AbstractDungeon.floorNum);
                    shopRewards.add(shopReward);
                    break;
                case TREASURE:
                    AbstractChest chest = AbstractDungeon.getRandomChest();
                    chest.open(false);
                    addGoldReward(combatGold);
                    Reward treasureRelicReward = new Reward(AbstractDungeon.floorNum);
                    for(AbstractRelic treasureRelic : combatRelics) {
                        awardRelic(treasureRelic, treasureRelicReward);
                    }
                    rewards.addAll(combatCardRewards);
                    rewards.add(treasureRelicReward);
                    break;
                case REST:
                    if (settings.useShovel && player.hasRelic(Shovel.ID)) {
                        Reward digReward = new Reward(AbstractDungeon.floorNum);
                        AbstractRelic.RelicTier digTier = AbstractDungeon.returnRandomRelicTier();
                        awardRelic(AbstractDungeon.returnRandomRelic(digTier), digReward);
                    }
                    break;
            }
        }
        mapPath.add("R");
        mapPath.add("BOSS");
    }

    private Reward getShopReward(int floor) {
        Reward shopReward = new Reward(floor);
        ShopScreen screen = AbstractDungeon.shopScreen;
        try {
            Field coloredCardsField = ShopScreen.class.getDeclaredField("coloredCards");
            Field colorlessCardsField = ShopScreen.class.getDeclaredField("colorlessCards");
            Field relicsField = ShopScreen.class.getDeclaredField("relics");
            Field potionsField = ShopScreen.class.getDeclaredField("potions");
            coloredCardsField.setAccessible(true);
            colorlessCardsField.setAccessible(true);
            relicsField.setAccessible(true);
            potionsField.setAccessible(true);
            ArrayList<AbstractCard> coloredCards = (ArrayList<AbstractCard>) coloredCardsField.get(screen);
            ArrayList<AbstractCard> colorlessCards = (ArrayList<AbstractCard>) colorlessCardsField.get(screen);
            ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) relicsField.get(screen);
            ArrayList<StorePotion> potions = (ArrayList<StorePotion>) potionsField.get(screen);
            for (AbstractCard card : coloredCards) {
                shopReward.addCard(card);
            }
            for (AbstractCard card : colorlessCards) {
                shopReward.addCard(card);
            }
            for (StoreRelic relic : relics) {
                if (settings.relicsToBuy.contains(relic.relic.relicId) && relic.price <= player.gold) {
                    awardRelic(relic.relic, shopReward);
                    addGoldReward(-relic.price);
                } else {
                    shopReward.addRelic(relic.relic.relicId);
                }
            }
            for (AbstractCard card : shopReward.cards) {
                if (settings.cardsToBuy.contains(card.cardID) && card.price <= player.gold) {
                    player.masterDeck.addToBottom(card);
                    addGoldReward(-card.price);
                }
            }
            for (StorePotion potion : potions) {
                //Potions not implemented yet in seedsearch
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return shopReward;
    }

    private Reward getEventReward(AbstractEvent event, String eventKey, int floor) {
        Random miscRng = AbstractDungeon.miscRng;
        Reward reward = new Reward(floor);
        switch(eventKey) {
            case GoopPuddle.ID:
                addGoldReward(75);
                break;
            case Sssserpent.ID:
                if(settings.takeSerpentGold) {
                    int goldgain = AbstractDungeon.ascensionLevel >=15 ? 150 : 175;
                    addGoldReward(goldgain);
                    player.masterDeck.addToBottom(new Doubt());
                }
                break;
            case AccursedBlacksmith.ID:
                if(settings.takeWarpedTongs) {
                    reward.addRelic(WarpedTongs.ID);
                    player.masterDeck.addToBottom(new Pain());
                }
                break;
            case BigFish.ID:
                if (settings.takeBigFishRelic) {
                    AbstractRelic.RelicTier fishTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(fishTier), reward);
                    player.masterDeck.addToBottom(new Regret());
                }
                break;
            case DeadAdventurer.ID:
                if (settings.takeDeadAdventurerFight) {
                    DeadAdventurer deadAdventurer = (DeadAdventurer)event;

                    try {
                        Method getMonster = DeadAdventurer.class.getDeclaredMethod("getMonster");
                        getMonster.setAccessible(true);
                        String monster = (String)getMonster.invoke(deadAdventurer);

                        int encounterChance = AbstractDungeon.ascensionLevel >= 15 ? 35 : 25;
                        for(int i = 1; i <= 3; i++) {
                            if (miscRng.random(0, 99) < encounterChance) {
                                addGoldReward(miscRng.random(25, 35));
                                reward.addCards(AbstractDungeon.getRewardCards());
                                monsters.add(monster);
                            } else {
                                encounterChance += 25;
                            }
                        }
                        addGoldReward(30);
                        AbstractRelic.RelicTier adventurerTier = AbstractDungeon.returnRandomRelicTier();
                        awardRelic(AbstractDungeon.returnRandomScreenlessRelic(adventurerTier), reward);

                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TheMausoleum.ID:
                if (settings.takeMausoleumRelic) {
                    AbstractRelic.RelicTier mausoleumTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(mausoleumTier), reward);
                    if (miscRng.randomBoolean() || AbstractDungeon.ascensionLevel >= 15) {
                        player.masterDeck.addToBottom(new Writhe());
                    }
                }
                break;
            case ScrapOoze.ID:
                if (settings.takeScrapOozeRelic) {
                    AbstractRelic.RelicTier scrapOozeTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(scrapOozeTier), reward);
                }
                break;
            case Addict.ID:
                if (settings.takeAddictRelic) {
                    AbstractRelic.RelicTier addictTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(addictTier), reward);
                }
                break;
            case MysteriousSphere.ID:
                if (settings.takeMysteriousSphereFight) {
                    addGoldReward(miscRng.random(45, 55));
                    monsters.add("2 Orb Walkers");
                    AbstractRelic.RelicTier mysteriousSphereTier = AbstractRelic.RelicTier.RARE;
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(mysteriousSphereTier), reward);
                    reward.addCards(AbstractDungeon.getRewardCards());
                }
                break;
            case TombRedMask.ID:
                if(!player.hasRelic(RedMask.ID) && settings.takeRedMaskAct3) {
                    awardRelic(RedMask.ID, reward);
                    addGoldReward(-player.gold);
                } else {
                    addGoldReward(222);
                }
                break;
            case Mushrooms.ID:
                if(settings.takeMushroomFight) {
                    monsters.add("The Mushroom Lair");
                    addGoldReward(miscRng.random(25, 35));
                    awardRelic(OddMushroom.ID, reward);
                    reward.addCards(AbstractDungeon.getRewardCards());
                } else {
                    player.masterDeck.addToBottom(new Parasite());
                }
                break;
            case MaskedBandits.ID:
                if (settings.takeMaskedBanditFight) {
                    monsters.add("Masked Bandits");
                    addGoldReward(miscRng.random(25, 35));
                    awardRelic(RedMask.ID, reward);
                    reward.addCards(AbstractDungeon.getRewardCards());
                } else {
                    addGoldReward(-player.gold);
                }
                break;
            case GoldenIdol.ID:
                if (settings.takeGoldenIdolWithCurse) {
                    awardRelic(GoldenIdol.ID, reward);
                    player.masterDeck.addToBottom(new Injury());
                } else if (settings.takeGoldenIdolWithoutCurse) {
                    awardRelic(GoldenIdol.ID, reward);
                }
                break;
            case ForgottenAltar.ID:
                if(player.hasRelic(GoldenIdol.ID) && settings.tradeGoldenIdolForBloody) {
                    awardRelic(BloodyIdol.ID, reward);
                    player.loseRelic(GoldenIdol.ID);
                }
                break;
            case Bonfire.ID:
                if(player.isCursed()) {
                    awardRelic(SpiritPoop.ID, reward);
                }
                break;
            case CursedTome.ID:
                if (settings.takeCursedTome) {
                    int roll = miscRng.random(2);
                    switch (roll) {
                        case 0:
                            awardRelic(Necronomicon.ID, reward);
                            break;
                        case 1:
                            awardRelic(Enchiridion.ID, reward);
                            break;
                        case 2:
                            awardRelic(NilrysCodex.ID, reward);
                            break;
                    }
                }
                break;
            case FaceTrader.ID:
                if (settings.tradeFaces) {
                    try {
                        Method hotkeyCheck = FaceTrader.class.getDeclaredMethod("getRandomFace");
                        hotkeyCheck.setAccessible(true);
                        AbstractRelic mask = (AbstractRelic) hotkeyCheck.invoke((FaceTrader) event);
                        awardRelic(mask, reward);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MindBloom.ID:
                if (settings.takeMindBloomGold && AbstractDungeon.floorNum <= 40) {
                    addGoldReward(999);
                    player.masterDeck.addToBottom(new Normality());
                    player.masterDeck.addToBottom(new Normality());
                } else if (settings.takeMindBloomFight) {
                    addGoldReward(100);
                    ArrayList<String> encounters = new ArrayList<>();
                    encounters.add("The Guardian");
                    encounters.add("Hexaghost");
                    encounters.add("Slime Boss");
                    Collections.shuffle(encounters, new java.util.Random(miscRng.randomLong()));
                    monsters.add(encounters.get(0));
                    AbstractRelic bloomRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE);
                    awardRelic(bloomRelic, reward);
                    reward.addCards(AbstractDungeon.getRewardCards());
                } else if (settings.takeMindBloomUpgrade) {
                    awardRelic(MarkOfTheBloom.ID, reward);
                } // Not really worrying about the other case
                break;
            case SecretPortal.ID:
                if (settings.takePortal) {
                    // TODO: Portal is not currently supported
                }
                break;
            case MoaiHead.ID:
                if (settings.tradeGoldenIdolForMoney && player.hasRelic(GoldenIdol.ID)) {
                    player.loseRelic(GoldenIdol.ID);
                    addGoldReward(333);
                }
                break;
            case Colosseum.ID:
                monsters.add("Colosseum Slavers");
                AbstractDungeon.treasureRng.random(10, 20); //Unused rng roll
                if (settings.takeColosseumFight) {
                    monsters.add("Colosseum Nobs");
                    AbstractRelic rareRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE);
                    AbstractRelic uncommonRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.UNCOMMON);
                    awardRelic(rareRelic, reward);
                    awardRelic(uncommonRelic, reward);
                    addGoldReward(100);
                    reward.addCards(AbstractDungeon.getRewardCards());
                }
                break;
            case TheLibrary.ID:
                if (settings.takeLibraryCard) {
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (int i = 0; i < 20; i++) {
                        AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                        if (!group.contains(card)) {
                            group.addToBottom(card);
                        } else {
                            i--;
                        }
                    }
                    reward.addCards(group.group);
                }
                break;
            case DrugDealer.ID:
                if (settings.takeDrugDealerRelic) {
                    awardRelic(MutagenicStrength.ID, reward);
                } else if (settings.takeDrugDealerTransform) {
                    // Assume two strikes? Note that the cards you choose do matter here.
                    for (int i = 0; i < 2; i++) {
                        AbstractDungeon.transformCard(new Strike_Red(), false, miscRng);
                        reward.addCard(AbstractDungeon.getTransformedCard());
                    }
                } else {
                    reward.addCard(new JAX());
                }
                break;
            case SensoryStone.ID:
                for (int i = 0; i < settings.numSensoryStoneCards; i++) {
                    reward.addCards(AbstractDungeon.getColorlessRewardCards());
                }
                break;
            case WeMeetAgain.ID:
                if (settings.takeWeMeetAgainRelic) {
                    AbstractRelic.RelicTier weMeetAgainTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(weMeetAgainTier), reward);
                }
                break;
            case WindingHalls.ID:
                if (settings.takeWindingHallsCurse) {
                    player.masterDeck.addToBottom(new Writhe());
                } else if (settings.takeWindingHallsMadness) {
                    for(int i = 0; i < 2; i++) {
                        reward.addCard(new Madness());
                        player.masterDeck.addToBottom(new Madness());
                    }
                }
                break;
            case GremlinMatchGame.ID:
                try {
                    Field eventCards = GremlinMatchGame.class.getDeclaredField("cards");
                    eventCards.setAccessible(true);
                    CardGroup gremlinCards = (CardGroup)eventCards.get(event);
                    reward.addCards(gremlinCards.group);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case GremlinWheelGame.ID:
                try {
                    Method buttonMethod = GremlinWheelGame.class.getDeclaredMethod("buttonEffect", int.class);
                    buttonMethod.setAccessible(true);
                    buttonMethod.invoke(event, 0);

                    Method preResultMethod = GremlinWheelGame.class.getDeclaredMethod("preApplyResult");
                    preResultMethod.setAccessible(true);
                    preResultMethod.invoke(event);

                    Method resultMethod = GremlinWheelGame.class.getDeclaredMethod("applyResult");
                    resultMethod.setAccessible(true);
                    resultMethod.invoke(event);

                    for (AbstractRelic relic : combatRelics) {
                        awardRelic(relic, reward);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        return reward;
    }

    public void getBossRewards() {
        bosses.add(AbstractDungeon.bossKey);
        if (AbstractDungeon.ascensionLevel == 20 && currentAct == 2) {
            bosses.add(AbstractDungeon.bossList.get(1));
        }
        if (currentAct < 2) {
            AbstractDungeon.floorNum = currentAct * 17 + 16;
            AbstractDungeon.currMapNode = new MapRoomNode(-1, 15);
            AbstractDungeon.currMapNode.room = new MonsterRoomBoss();
            AbstractDungeon.miscRng = new Random(currentSeed + (long)AbstractDungeon.floorNum);
            Reward cardReward = new Reward(AbstractDungeon.floorNum);
            cardReward.addCards(AbstractDungeon.getRewardCards());
            addGoldReward(AbstractDungeon.miscRng.random(-5, 5));

            AbstractDungeon.currMapNode.room = new TreasureRoomBoss();
            AbstractDungeon.floorNum += 1;
            AbstractDungeon.miscRng = new Random(currentSeed + (long)AbstractDungeon.floorNum);
            BossChest bossChest = new BossChest();
            Reward bossRelicReward = new Reward(AbstractDungeon.floorNum);
            for (AbstractRelic relic : bossChest.relics) {
                bossRelics.add(relic.relicId);
            }
            for (String relic : settings.bossRelicsToTake) {
                if (bossRelicReward.relics.contains(relic)) {
                    doRelicPickupLogic(RelicLibrary.getRelic(relic), bossRelicReward);
                }
            }

            rewards.add(cardReward);
            rewards.add(bossRelicReward);

        }
    }

    private void addGoldReward(int amount) {
        if (amount > 0) {
            player.gainGold(amount);
        } else {
            player.loseGold(-amount);
        }
    }

    public void printSeedStats() {
        ArrayList<String> shopRelics = new ArrayList<>();
        for (Reward shopReward : shopRewards) {
            shopRelics.addAll(shopReward.relics);
        }

        System.out.println(MessageFormat.format("Seed: {0} ({1})", SeedHelper.getString(currentSeed), currentSeed));
        System.out.println(MessageFormat.format("{0} combats", numCombats));
        System.out.println(MessageFormat.format("{0} Elites", numElites));
        System.out.println(MessageFormat.format("{0} relics:", player.relics.size()));
        System.out.println(player.relics);
        System.out.println("Shop relics:");
        System.out.println(shopRelics);
        System.out.println("Boss relics:");
        System.out.println(bossRelics);
        System.out.println("Events:");
        System.out.println(events);
        System.out.println("Monsters (not boss):");
        System.out.println(monsters);
        System.out.println("Bosses:");
        System.out.println(bosses);
        System.out.println("Map path:");
        System.out.println(mapPath);
        System.out.println("Card choices:");
        for (Reward reward : rewards) {
            if (reward.cards.size() > 0) {
                System.out.println(String.format("Floor %d: %s", reward.floor, reward.cards));
            }
        }
        System.out.println("Mandatory cards:");
        System.out.println(player.masterDeck.group);
        System.out.println("#####################################");
    }
}
