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
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.potions.AbstractPotion;
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
import seedsearch.patches.AbstractRoomPatch;
import seedsearch.patches.CardRewardScreenPatch;
import seedsearch.patches.EventHelperPatch;
import seedsearch.patches.ShowCardAndObtainEffectPatch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.helpers.MonsterHelper.*;


public class SeedRunner {

    public static ArrayList<AbstractRelic> combatRelics = new ArrayList<>();
    public static ArrayList<Reward> combatCardRewards = new ArrayList<>();
    public static ArrayList<AbstractPotion> combatPotions = new ArrayList<>();
    public static int combatGold = 0;

    private AbstractPlayer player;
    private int currentAct;
    private int actFloor;
    private int bootsCharges = 0;

    private SeedResult seedResult;

    private SearchSettings settings;
    private long currentSeed;

    public SeedRunner(SearchSettings settings) {
        this.settings = settings;
        AbstractDungeon.fadeColor = Settings.SHADOW_COLOR;
        CharacterManager characterManager = new CharacterManager();
        CardCrawlGame.characterManager = characterManager;
        characterManager.setChosenCharacter(settings.playerClass);
        currentSeed = settings.startSeed;
        AbstractDungeon.ascensionLevel = settings.ascensionLevel;
        Settings.seedSet = true;
        this.settings.checkIds();
    }

    private void setSeed(long seed) {
        Settings.seed = seed;
        currentSeed = seed;
        AbstractDungeon.generateSeeds();
        player = AbstractDungeon.player;
        AbstractDungeon.reset();
        resetCharacter();
        clearCombatRewards();

        currentAct = 0;
        actFloor = 0;
        this.bootsCharges = 0;
        seedResult = new SeedResult(currentSeed);
    }

    private void resetCharacter() {
        player.relics = new ArrayList<>();
        try {
            Method starterRelicsMethod = AbstractPlayer.class.getDeclaredMethod("initializeStarterRelics", AbstractPlayer.PlayerClass.class);
            starterRelicsMethod.setAccessible(true);
            starterRelicsMethod.invoke(player, settings.playerClass);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Reflection error when initializing player relics");
        }
        player.potions = new ArrayList<>();
        player.masterDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        CharSelectInfo info = player.getLoadout();
        player.maxHealth = info.maxHp;
        player.gold = info.gold;

        // Remove the MockMusic tracks that would otherwise pile up
        CardCrawlGame.music.dispose();
        CardCrawlGame.music.update();
    }

    private boolean runSeed() {
        if (!settings.speedrunPace) {
            CardCrawlGame.playtime = 900F;
        } else {
            CardCrawlGame.playtime = 0F;
        }

        AbstractDungeon exordium = new Exordium(player, new ArrayList<>());
        ArrayList<MapRoomNode> exordiumPath = findMapPath(AbstractDungeon.map);

        ArrayList<NeowReward> neowRewards = getNeowRewards();
        seedResult.addNeowRewards(neowRewards);
        if (settings.neowChoice < 0 || settings.neowChoice > 3) {
            throw new RuntimeException("The 'neowChoice' setting must be between 0 and 3.");
        }
        claimNeowReward(neowRewards.get(settings.neowChoice));

        runPath(exordiumPath);
        getBossRewards();

        seedResult.updateRelics();
        if (!seedResult.testAct1Filters(settings)) {
            return false;
        }

        currentAct += 1;
        AbstractDungeon city = new TheCity(player, AbstractDungeon.specialOneTimeEventList);
        ArrayList<MapRoomNode> cityPath = findMapPath(AbstractDungeon.map);
        runPath(cityPath);
        getBossRewards();

        currentAct += 1;
        AbstractDungeon beyond = new TheBeyond(player, AbstractDungeon.specialOneTimeEventList);
        ArrayList<MapRoomNode> beyondPath = findMapPath(AbstractDungeon.map);
        runPath(beyondPath);
        getBossRewards();

        if (settings.act4) {
            currentAct += 1;
            AbstractDungeon end = new TheEnding(player, AbstractDungeon.specialOneTimeEventList);
            AbstractDungeon.floorNum += 1;
            ArrayList<MapRoomNode> endPath = new ArrayList<>();
            endPath.add(AbstractDungeon.map.get(0).get(3));
            endPath.add(AbstractDungeon.map.get(1).get(3));
            endPath.add(AbstractDungeon.map.get(2).get(3));
            runPath(endPath);
            getBossRewards();
        }

        seedResult.updateRelics();
        return seedResult.testFinalFilters(settings);
    }

    public boolean runSeed(long seed) {
        setSeed(seed);
        return runSeed();
    }

    public static void clearCombatRewards() {
        combatGold = 0;
        combatRelics.clear();
        combatCardRewards.clear();
        combatPotions.clear();
    }

    private ArrayList<NeowReward> getNeowRewards() {
        NeowEvent.rng = new Random(Settings.seed);
        ArrayList<NeowReward> rewards = new ArrayList<>();
        rewards.add(new NeowReward(0));
        rewards.add(new NeowReward(1));
        rewards.add(new NeowReward(2));
        rewards.add(new NeowReward(3));
        return rewards;
    }

    private void claimNeowReward(NeowReward neowOption) {
        Reward reward = new Reward(0);
        AbstractDungeon.getCurrMapNode().room = new EmptyRoom();
        AbstractRoomPatch.obtainedRelic = null;
        CardRewardScreenPatch.rewardCards = null;
        ShowCardAndObtainEffectPatch.resetCards();
        neowOption.activate();
        if (AbstractRoomPatch.obtainedRelic != null) {
            awardRelic(AbstractRoomPatch.obtainedRelic, reward);
        }
        if (CardRewardScreenPatch.rewardCards != null) {
            seedResult.addCardReward(0, CardRewardScreenPatch.rewardCards);
        }
        if (ShowCardAndObtainEffectPatch.obtainedCards.size() > 0) {
            for (AbstractCard card : ShowCardAndObtainEffectPatch.obtainedCards) {
                addInvoluntaryCardReward(card, reward);
            }
        }
        if (combatPotions.size() > 0) {
            reward.addPotions(combatPotions);
        }
        if (neowOption.type == NeowReward.NeowRewardType.TRANSFORM_CARD) {
            // We're assuming we remove the second card in the deck here to avoid Ascender's Bane
            AbstractCard removedCard = player.masterDeck.group.get(1);
            AbstractDungeon.transformCard(removedCard, false, NeowEvent.rng);
            player.masterDeck.removeCard(removedCard);
            addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
        }
        if (neowOption.type == NeowReward.NeowRewardType.TRANSFORM_TWO_CARDS) {
            AbstractCard removedCard = player.masterDeck.group.get(1);
            AbstractDungeon.transformCard(removedCard, false, NeowEvent.rng);
            player.masterDeck.removeCard(removedCard);
            addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
            removedCard = player.masterDeck.group.get(1);
            AbstractDungeon.transformCard(removedCard, false, NeowEvent.rng);
            player.masterDeck.removeCard(removedCard);
            addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
        }
        seedResult.addMiscReward(reward);
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
        switch (relicKey) {
            case TinyHouse.ID:
                seedResult.addCardReward(reward.floor, AbstractDungeon.getRewardCards());
                addGoldReward(50);
                break;
            case WingBoots.ID:
                this.bootsCharges = 3;
                break;
            case CallingBell.ID:
                addInvoluntaryCardReward(new CurseOfTheBell(), reward);
                String relic1 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.COMMON);
                String relic2 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.UNCOMMON);
                String relic3 = AbstractDungeon.returnRandomRelicKey(AbstractRelic.RelicTier.RARE);
                awardRelic(relic1, reward);
                awardRelic(relic2, reward);
                awardRelic(relic3, reward);
                break;
            case PandorasBox.ID:
                int count = 0;
                ArrayList<AbstractCard> strikesAndDefends = new ArrayList<>();
                for (AbstractCard card : player.masterDeck.group) {
                    if ((card.cardID.equals("Strike_R")) || (card.cardID.equals("Strike_G")) ||
                            (card.cardID.equals("Strike_B")) || (card.cardID.equals("Strike_P")) ||
                            (card.cardID.equals("Defend_R")) || (card.cardID.equals("Defend_G")) ||
                            (card.cardID.equals("Defend_B")) || (card.cardID.equals("Defend_P"))) {
                        count += 1;
                        strikesAndDefends.add(card);
                    }
                }
                for (AbstractCard card : strikesAndDefends) {
                    player.masterDeck.group.remove(card);
                }
                for (int i = 0; i < count; i++) {
                    AbstractCard newCard = AbstractDungeon.returnTrulyRandomCard().makeCopy();
                    if (!settings.ignorePandoraCards) {
                        addInvoluntaryCardReward(newCard, reward);
                    }
                }
                break;
            case Astrolabe.ID:
                AbstractCard removedCard = player.masterDeck.group.get(1);
                AbstractDungeon.transformCard(removedCard, true, AbstractDungeon.miscRng);
                player.masterDeck.removeCard(removedCard);
                addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
                removedCard = player.masterDeck.group.get(1);
                AbstractDungeon.transformCard(removedCard, true, AbstractDungeon.miscRng);
                player.masterDeck.removeCard(removedCard);
                addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
                removedCard = player.masterDeck.group.get(1);
                AbstractDungeon.transformCard(removedCard, true, AbstractDungeon.miscRng);
                player.masterDeck.removeCard(removedCard);
                addInvoluntaryCardReward(AbstractDungeon.getTransformedCard(), reward);
                break;
            case Necronomicon.ID:
                AbstractCard curse = new Necronomicurse();
                addInvoluntaryCardReward(curse, reward);
                break;
        }
    }

    private ArrayList<MapRoomNode> findBootsPath(ArrayList<ArrayList<MapRoomNode>> map) {
        // I apologize for this monstrosity

        float[][] weights = new float[15][7];
        float[][][] pathWeights = new float[15][7][4];
        ArrayList<ArrayList<ArrayList<MapRoomNode>>> parents = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 7; j++) {
                weights[i][j] = getRoomScore(map.get(i).get(j).room);
                for (int k = 0; k < 4; k++) {
                    if (i == 0) {
                        pathWeights[i][j][k] = weights[i][j];
                    } else {
                        pathWeights[i][j][k] = 100000f;
                    }
                }
            }
        }
        for (int floor = 0; floor < 14; floor++) {
            ArrayList<ArrayList<MapRoomNode>> floorParents = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                ArrayList<MapRoomNode> floorSubList = new ArrayList<>(7);
                for (int j = 0; j < 7; j++) {
                    floorSubList.add(null);
                }
                floorParents.add(floorSubList);
            }
            for (int x = 0; x < 7; x++) {
                MapRoomNode node = map.get(floor).get(x);
                if (node.room == null) {
                    continue;
                }
                ArrayList<MapEdge> edges = node.getEdges();
                ArrayList<Integer> nextXs = new ArrayList<>(0);
                for (MapEdge edge : edges) {
                    int targetX = edge.dstX;
                    nextXs.add(targetX);
                }
                for (int nx = 0; nx < 7; nx++) {
                    for (int wing_uses = 0; wing_uses <= bootsCharges; wing_uses++) {
                        if (nextXs.contains(nx)) {
                            float testWeight = weights[floor + 1][nx] + pathWeights[floor][x][wing_uses];
                            if (testWeight < pathWeights[floor + 1][nx][wing_uses]) {
                                pathWeights[floor + 1][nx][wing_uses] = testWeight;
                                floorParents.get(wing_uses).set(nx, node);
                            }
                        } else if (wing_uses > 0) {
                            float testWeight = weights[floor + 1][nx] + pathWeights[floor][x][wing_uses - 1];
                            if (testWeight < pathWeights[floor + 1][nx][wing_uses]) {
                                pathWeights[floor + 1][nx][wing_uses] = testWeight;
                                floorParents.get(wing_uses).set(nx, node);
                            }
                        }
                    }
                }
            }
            parents.add(floorParents);
        }
        int[] best_top = {0, 0, 0, 0};
        float[] best_score = {100000f, 100000f, 100000f, 100000f};
        for (int uses = 0; uses < 4; uses++) {
            for (int x = 0; x < 7; x++) {
                if (pathWeights[14][x][uses] < best_score[uses]) {
                    best_score[uses] = pathWeights[14][x][uses];
                    best_top[uses] = x;
                }
            }
        }
        int best_uses = 0;
        if (best_score[0] - best_score[1] >= settings.wingBootsThreshold) {
            best_uses = 1;
        }
        if (best_score[1] - best_score[2] >= settings.wingBootsThreshold) {
            best_uses = 2;
        }
        if (best_score[2] - best_score[3] >= settings.wingBootsThreshold) {
            best_uses = 3;
        }
        int cur_uses = best_uses;
        ArrayList<MapRoomNode> path = new ArrayList<>(15);
        int next_x = best_top[cur_uses];
        path.add(map.get(14).get(best_top[cur_uses]));
        for (int y = 14; y > 0; y--) {
            MapRoomNode parent = parents.get(y - 1).get(cur_uses).get(next_x);
            boolean isConnected = false;
            for (MapEdge edge : parent.getEdges()) {
                if (edge.dstX == next_x) {
                    isConnected = true;
                    break;
                }
            }
            if (!isConnected) {
                cur_uses -= 1;
            }
            path.add(0, parent);
            next_x = parent.x;
        }
        if (best_uses != 0) {
            this.bootsCharges -= best_uses;
        }
        return path;
    }

    private ArrayList<MapRoomNode> findMapPath(ArrayList<ArrayList<MapRoomNode>> map) {
        if (bootsCharges > 0) {
            return findBootsPath(map);
        }
        float[][] weights = new float[15][7];
        float[][] pathWeights = new float[15][7];
        ArrayList<ArrayList<MapRoomNode>> parents = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 7; j++) {
                weights[i][j] = getRoomScore(map.get(i).get(j).room);
                if (i == 0) {
                    pathWeights[i][j] = weights[i][j];
                } else {
                    pathWeights[i][j] = 100000f;
                }
            }
        }
        for (int floor = 0; floor < 14; floor++) {
            ArrayList<MapRoomNode> floorParents = new ArrayList<>(7);
            for (int i = 0; i < 7; i++) {
                floorParents.add(null);
            }
            for (int x = 0; x < 7; x++) {
                MapRoomNode node = map.get(floor).get(x);
                if (node.room == null) {
                    continue;
                }
                ArrayList<MapEdge> edges = node.getEdges();
                for (MapEdge edge : edges) {
                    int targetX = edge.dstX;
                    float testWeight = weights[floor + 1][targetX] + pathWeights[floor][x];
                    if (testWeight < pathWeights[floor + 1][targetX]) {
                        pathWeights[floor + 1][targetX] = testWeight;
                        floorParents.set(targetX, node);
                    }
                }
            }
            parents.add(floorParents);
        }
        int best_top = 0;
        float best_score = 100000f;
        for (int x = 0; x < 7; x++) {
            if (pathWeights[14][x] < best_score) {
                best_score = pathWeights[14][x];
                best_top = x;
            }
        }
        ArrayList<MapRoomNode> path = new ArrayList<MapRoomNode>(15);
        int next_x = best_top;
        path.add(map.get(14).get(best_top));
        for (int y = 14; y > 0; y--) {
            MapRoomNode parent = parents.get(y - 1).get(next_x);
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

    private void runPath(ArrayList<MapRoomNode> path) {
        for (actFloor = 0; actFloor < path.size(); actFloor++) {
            AbstractDungeon.floorNum += 1;
            if (AbstractDungeon.floorNum > settings.highestFloor) {
                return;
            }
            AbstractDungeon.miscRng = new Random(currentSeed + (long) AbstractDungeon.floorNum);
            MapRoomNode node = path.get(actFloor);
            RoomType result;
            if (node.room instanceof EventRoom) {
                result = RoomType.EVENT;
                seedResult.addToMapPath("?");
            } else if (node.room instanceof MonsterRoomElite) {
                result = RoomType.ELITE;
                seedResult.addToMapPath("E");
            } else if (node.room instanceof MonsterRoom) {
                result = RoomType.MONSTER;
                seedResult.addToMapPath("M");
            } else if (node.room instanceof ShopRoom) {
                result = RoomType.SHOP;
                seedResult.addToMapPath("S");
            } else if (node.room instanceof TreasureRoom) {
                result = RoomType.TREASURE;
                seedResult.addToMapPath("T");
            } else {
                result = RoomType.REST;
                seedResult.addToMapPath("R");
            }
            if (result == RoomType.EVENT) {
                EventHelper.RoomResult eventRoll = EventHelper.roll();
                switch (eventRoll) {
                    case ELITE:
                        result = RoomType.ELITE;
                        node.room = new MonsterRoomElite();
                        break;
                    case MONSTER:
                        result = RoomType.MONSTER;
                        node.room = new MonsterRoom();
                        break;
                    case SHOP:
                        result = RoomType.SHOP;
                        node.room = new ShopRoom();
                        break;
                    case TREASURE:
                        result = RoomType.TREASURE;
                        node.room = new TreasureRoom();
                        break;
                }
            }
            AbstractDungeon.currMapNode = node;
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            }
            clearCombatRewards();
            switch (result) {
                case EVENT:
                    seedResult.addToTrueMapPath("?");
                    Random eventRngDuplicate = new Random(Settings.seed, AbstractDungeon.eventRng.counter);
                    AbstractEvent event = AbstractDungeon.generateEvent(eventRngDuplicate);
                    String eventKey = EventHelperPatch.eventName;
                    Reward eventReward = getEventReward(event, eventKey, AbstractDungeon.floorNum);
                    seedResult.registerEvent(eventKey);
                    if (!eventReward.isEmpty()) {
                        seedResult.addMiscReward(eventReward);
                    }
                    break;
                case MONSTER:
                    seedResult.addToTrueMapPath("M");
                    String monster = AbstractDungeon.monsterList.remove(0);
                    seedResult.registerCombat(monster);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                    int gold = AbstractDungeon.treasureRng.random(10, 20);
                    addGoldReward(gold);
                    AbstractPotion monsterPotion = getPotionReward();
                    if (monsterPotion != null) {
                        Reward monsterPotionReward = new Reward(AbstractDungeon.floorNum);
                        monsterPotionReward.addPotion(monsterPotion);
                        seedResult.addMiscReward(monsterPotionReward);
                    }
                    break;
                case ELITE:
                    seedResult.addToTrueMapPath("E");
                    String elite = AbstractDungeon.eliteMonsterList.remove(0);
                    seedResult.registerEliteCombat(elite);
                    AbstractRelic.RelicTier tier = AbstractDungeon.returnRandomRelicTier();
                    String relic = AbstractDungeon.returnRandomRelicKey(tier);
                    Reward relicReward = new Reward(AbstractDungeon.floorNum);
                    awardRelic(relic, relicReward);
                    if (player.hasRelic(BlackStar.ID)) {
                        AbstractRelic starRelic = AbstractDungeon.returnRandomNonCampfireRelic(tier);
                        awardRelic(starRelic, relicReward);
                    }
                    seedResult.addMiscReward(relicReward);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                    gold = AbstractDungeon.treasureRng.random(25, 35);
                    addGoldReward(gold);
                    AbstractPotion elitePotion = getPotionReward();
                    if (elitePotion != null) {
                        Reward monsterPotionReward = new Reward(AbstractDungeon.floorNum);
                        monsterPotionReward.addPotion(elitePotion);
                        seedResult.addMiscReward(monsterPotionReward);
                    }
                    break;
                case SHOP:
                    seedResult.addToTrueMapPath("S");
                    Merchant merchant = new Merchant();
                    Reward shopReward = getShopReward(AbstractDungeon.floorNum);
                    seedResult.addShopReward(shopReward);
                    break;
                case TREASURE:
                    seedResult.addToTrueMapPath("T");
                    AbstractChest chest = AbstractDungeon.getRandomChest();
                    ShowCardAndObtainEffectPatch.resetCards();
                    chest.open(false);
                    addGoldReward(combatGold);
                    Reward treasureRelicReward = new Reward(AbstractDungeon.floorNum);
                    for (AbstractRelic treasureRelic : combatRelics) {
                        awardRelic(treasureRelic, treasureRelicReward);
                    }
                    for (AbstractCard card : ShowCardAndObtainEffectPatch.obtainedCards) {
                        addInvoluntaryCardReward(card, treasureRelicReward);
                    }
                    seedResult.addAllCardRewards(combatCardRewards);
                    seedResult.addMiscReward(treasureRelicReward);
                    break;
                case REST:
                    seedResult.addToTrueMapPath("R");
                    if (settings.useShovel && player.hasRelic(Shovel.ID)) {
                        Reward digReward = new Reward(AbstractDungeon.floorNum);
                        AbstractRelic.RelicTier digTier = AbstractDungeon.returnRandomRelicTier();
                        awardRelic(AbstractDungeon.returnRandomRelic(digTier), digReward);
                    }
                    break;
            }
        }
        seedResult.addToMapPath("BOSS");
        seedResult.addToTrueMapPath("BOSS");
    }


    @SuppressWarnings("unchecked")
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
                shopReward.addPotion(potion.potion);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return shopReward;
    }

    private Reward getEventReward(AbstractEvent event, String eventKey, int floor) {
        clearCombatRewards();
        ShowCardAndObtainEffectPatch.resetCards();
        Random miscRng = AbstractDungeon.miscRng;
        Reward reward = new Reward(floor);
        switch (eventKey) {
            case GoopPuddle.ID:
                addGoldReward(75);
                break;
            case Sssserpent.ID:
                if (settings.takeSerpentGold) {
                    int goldgain = AbstractDungeon.ascensionLevel >= 15 ? 150 : 175;
                    addGoldReward(goldgain);
                    addInvoluntaryCardReward(new Doubt(), reward);
                }
                break;
            case AccursedBlacksmith.ID:
                if (settings.takeWarpedTongs) {
                    reward.addRelic(WarpedTongs.ID);
                    addInvoluntaryCardReward(new Pain(), reward);
                }
                break;
            case BigFish.ID:
                if (settings.takeBigFishRelic) {
                    AbstractRelic.RelicTier fishTier = AbstractDungeon.returnRandomRelicTier();
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(fishTier), reward);
                    addInvoluntaryCardReward(new Regret(), reward);
                }
                break;
            case DeadAdventurer.ID:
                if (settings.takeDeadAdventurerFight) {
                    DeadAdventurer deadAdventurer = (DeadAdventurer) event;

                    try {
                        Method getMonster = DeadAdventurer.class.getDeclaredMethod("getMonster");
                        getMonster.setAccessible(true);
                        String monster = (String) getMonster.invoke(deadAdventurer);

                        int encounterChance = AbstractDungeon.ascensionLevel >= 15 ? 35 : 25;
                        for (int i = 1; i <= 3; i++) {
                            if (miscRng.random(0, 99) < encounterChance) {
                                addGoldReward(miscRng.random(25, 35));
                                seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                                seedResult.registerCombat(monster);
                                break;
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
                        addInvoluntaryCardReward(new Writhe(), reward);
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
                    seedResult.registerCombat("2 Orb Walkers");
                    AbstractRelic.RelicTier mysteriousSphereTier = AbstractRelic.RelicTier.RARE;
                    awardRelic(AbstractDungeon.returnRandomScreenlessRelic(mysteriousSphereTier), reward);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                }
                break;
            case TombRedMask.ID:
                if (!player.hasRelic(RedMask.ID) && settings.takeRedMaskAct3) {
                    awardRelic(RedMask.ID, reward);
                    addGoldReward(-player.gold);
                } else {
                    addGoldReward(222);
                }
                break;
            case Mushrooms.ID:
                if (settings.takeMushroomFight) {
                    seedResult.registerCombat(MUSHROOMS_EVENT_ENC);
                    addGoldReward(miscRng.random(25, 35));
                    awardRelic(OddMushroom.ID, reward);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                } else {
                    addInvoluntaryCardReward(new Parasite(), reward);
                }
                break;
            case MaskedBandits.ID:
                if (settings.takeMaskedBanditFight) {
                    seedResult.registerCombat(MASKED_BANDITS_ENC);
                    addGoldReward(miscRng.random(25, 35));
                    awardRelic(RedMask.ID, reward);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
                } else {
                    addGoldReward(-player.gold);
                }
                break;
            case GoldenIdol.ID:
                if (settings.takeGoldenIdolWithCurse) {
                    awardRelic(GoldenIdol.ID, reward);
                    addInvoluntaryCardReward(new Injury(), reward);
                } else if (settings.takeGoldenIdolWithoutCurse) {
                    awardRelic(GoldenIdol.ID, reward);
                }
                break;
            case ForgottenAltar.ID:
                if (player.hasRelic(GoldenIdol.ID) && settings.tradeGoldenIdolForBloody) {
                    awardRelic(BloodyIdol.ID, reward);
                    loseRelic(GoldenIdol.ID);
                }
                break;
            case Bonfire.ID:
                if (player.isCursed()) {
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
                        AbstractRelic mask = (AbstractRelic) hotkeyCheck.invoke(event);
                        awardRelic(mask, reward);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MindBloom.ID:
                if (settings.takeMindBloomGold && AbstractDungeon.floorNum <= 40) {
                    addGoldReward(999);
                    addInvoluntaryCardReward(new Normality(), reward);
                    addInvoluntaryCardReward(new Normality(), reward);
                } else if (settings.takeMindBloomFight) {
                    addGoldReward(100);
                    ArrayList<String> encounters = new ArrayList<>();
                    encounters.add(GUARDIAN_ENC);
                    encounters.add(HEXAGHOST_ENC);
                    encounters.add(SLIME_BOSS_ENC);
                    Collections.shuffle(encounters, new java.util.Random(miscRng.randomLong()));
                    seedResult.registerCombat(encounters.get(0));
                    AbstractRelic bloomRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE);
                    awardRelic(bloomRelic, reward);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
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
                    loseRelic(GoldenIdol.ID);
                    addGoldReward(333);
                }
                break;
            case Colosseum.ID:
                seedResult.registerCombat(COLOSSEUM_SLAVER_ENC);
                AbstractDungeon.treasureRng.random(10, 20); //Unused rng roll
                if (settings.takeColosseumFight) {
                    seedResult.registerCombat(COLOSSEUM_NOB_ENC);
                    AbstractRelic rareRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE);
                    AbstractRelic uncommonRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.UNCOMMON);
                    awardRelic(rareRelic, reward);
                    awardRelic(uncommonRelic, reward);
                    addGoldReward(100);
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getRewardCards());
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
                    seedResult.addCardReward(AbstractDungeon.floorNum, AbstractDungeon.getColorlessRewardCards());
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
                    addInvoluntaryCardReward(new Writhe(), reward);
                } else if (settings.takeWindingHallsMadness) {
                    for (int i = 0; i < 2; i++) {
                        addInvoluntaryCardReward(new Madness(), reward);
                    }
                }
                break;
            case GremlinMatchGame.ID:
                try {
                    Field eventCards = GremlinMatchGame.class.getDeclaredField("cards");
                    eventCards.setAccessible(true);
                    CardGroup gremlinCards = (CardGroup) eventCards.get(event);
                    ArrayList<AbstractCard> matchCards = new ArrayList<>();
                    ArrayList<String> pairs = new ArrayList<>();
                    for (AbstractCard card : gremlinCards.group) {
                        if (pairs.contains(card.cardID)) {
                            pairs.remove(card.cardID);
                        } else {
                            pairs.add(card.cardID);
                            matchCards.add(card);
                        }
                    }
                    reward.addCards(matchCards);
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
                break;
            case Lab.ID:
                ArrayList<AbstractPotion> potions = new ArrayList<>();
                potions.add(PotionHelper.getRandomPotion());
                potions.add(PotionHelper.getRandomPotion());
                if (AbstractDungeon.ascensionLevel < 15) {
                    potions.add(PotionHelper.getRandomPotion());
                }
                reward.addPotions(potions);
                break;
            case WomanInBlue.ID:
                ArrayList<AbstractPotion> womanPotions = new ArrayList<>();
                womanPotions.add(PotionHelper.getRandomPotion());
                womanPotions.add(PotionHelper.getRandomPotion());
                womanPotions.add(PotionHelper.getRandomPotion());
                reward.addPotions(womanPotions);
                break;
        }
        if (ShowCardAndObtainEffectPatch.obtainedCards.size() > 0) {
            for (AbstractCard card : ShowCardAndObtainEffectPatch.obtainedCards) {
                addInvoluntaryCardReward(card, reward);
            }
        }
        return reward;
    }

    private void getBossRewards() {
        AbstractDungeon.floorNum += 1;
        if (AbstractDungeon.floorNum > settings.highestFloor) {
            if (settings.showBosses){
                seedResult.registerBossCombat(AbstractDungeon.bossKey);
                if (AbstractDungeon.ascensionLevel == 20 && currentAct == 2) {
                    seedResult.registerBossCombat(AbstractDungeon.bossList.get(1));
                }

                if (currentAct < 2) {
                    BossChest bossChest = new BossChest();
                    ArrayList<String> bossRelicStrings = new ArrayList<>();
                    for (AbstractRelic relic : bossChest.relics) {
                        bossRelicStrings.add(relic.relicId);
                    }
                    seedResult.addBossReward(bossRelicStrings);
                    Reward bossRelicReward = new Reward(AbstractDungeon.floorNum);
                    seedResult.addMiscReward(bossRelicReward);
                }
            }
            return;
        }
        seedResult.registerBossCombat(AbstractDungeon.bossKey);
        AbstractDungeon.currMapNode = new MapRoomNode(-1, 15);
        AbstractDungeon.currMapNode.room = new MonsterRoomBoss();
        AbstractDungeon.currMapNode.room.phase = AbstractRoom.RoomPhase.COMPLETE;
        if (AbstractDungeon.ascensionLevel == 20 && currentAct == 2) {
            seedResult.registerBossCombat(AbstractDungeon.bossList.get(1));
            AbstractDungeon.floorNum += 1;
            if (AbstractDungeon.floorNum > settings.highestFloor) {
                return;
            }
        }
        if (currentAct < 2) {
            AbstractDungeon.miscRng = new Random(currentSeed + (long) AbstractDungeon.floorNum);

            Reward cardReward = new Reward(AbstractDungeon.floorNum);
            cardReward.addCards(AbstractDungeon.getRewardCards());
            int gold = 100 + AbstractDungeon.miscRng.random(-5, 5);
            if (AbstractDungeon.ascensionLevel >= 13) {
                gold = (int) (gold * 0.75);
            }
            addGoldReward(gold);
            AbstractPotion potion = getPotionReward();
            if (potion != null) {
                Reward potionReward = new Reward(AbstractDungeon.floorNum);
                potionReward.addPotion(potion);
                seedResult.addMiscReward(potionReward);
            }

            AbstractDungeon.currMapNode.room = new TreasureRoomBoss();
            AbstractDungeon.floorNum += 1;
            if (AbstractDungeon.floorNum > settings.highestFloor) {
                return;
            }
            AbstractDungeon.miscRng = new Random(currentSeed + (long) AbstractDungeon.floorNum);

            BossChest bossChest = new BossChest();
            ArrayList<String> bossRelicStrings = new ArrayList<>();
            for (AbstractRelic relic : bossChest.relics) {
                bossRelicStrings.add(relic.relicId);
            }
            seedResult.addBossReward(bossRelicStrings);
            Reward bossRelicReward = new Reward(AbstractDungeon.floorNum);
            for (String relic : settings.bossRelicsToTake) {
                if (bossRelicStrings.contains(relic)) {
                    doRelicPickupLogic(RelicLibrary.getRelic(relic), bossRelicReward);
                    break;
                }
            }

            seedResult.addCardReward(cardReward);
            seedResult.addMiscReward(bossRelicReward);
        }
    }

    private AbstractPotion getPotionReward() {
        int chance = 40;
        chance = chance + AbstractRoom.blizzardPotionMod;

        if (AbstractDungeon.player.hasRelic(WhiteBeast.ID)) {
            chance = 100;
        }

        if (AbstractDungeon.potionRng.random(0, 99) >= chance && !Settings.isDebug) {
            AbstractRoom.blizzardPotionMod += 10;
            return null;
        } else {
            AbstractRoom.blizzardPotionMod -= 10;
            return AbstractDungeon.returnRandomPotion();
        }
    }

    private void addGoldReward(int amount) {
        if (amount > 0) {
            player.gainGold(amount);
        } else {
            player.loseGold(-amount);
        }
    }

    public SeedResult getSeedResult() {
        return seedResult;
    }

    private void addInvoluntaryCardReward(AbstractCard card, Reward reward) {
        reward.cards.add(card);
        AbstractDungeon.player.masterDeck.addToBottom(card);
    }

    private void loseRelic(String relicID) {
        player.loseRelic(relicID);
    }

}
