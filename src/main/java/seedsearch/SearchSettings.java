package seedsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.cards.red.BodySlam;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.JuzuBracelet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SearchSettings {

    private static final String configName = "searchConfig.json";

    // Core search parameters

    public int ascensionLevel = 0;
    public AbstractPlayer.PlayerClass playerClass = AbstractPlayer.PlayerClass.IRONCLAD;
    public long startSeed = 0L;
    public long endSeed = 100L;
    public boolean verbose = true;
    public boolean exitAfterSearch = false;
    public int highestFloor = 55;
    public boolean showBosses = false;


    // Navigation

    public float eliteRoomWeight = 1.2f;
    public float monsterRoomWeight = 1f;
    public float restRoomWeight = 0f;
    public float shopRoomWeight = 0.9f;
    public float eventRoomWeight = 0.9f;
    public float wingBootsThreshold = 1f; // Wing boots charges are used if weight is changed by this amount

    // General decisions

    public ArrayList<String> relicsToBuy = new ArrayList<>(); // Use the ID for cards and relics
    public ArrayList<String> cardsToBuy = new ArrayList<>();
    public ArrayList<String> bossRelicsToTake = new ArrayList<>(); // Give them in priority order to always take a relic
    public int neowChoice = 3; // 3 is the boss relic trade
    public boolean useShovel = false;
    public boolean speedrunPace = true; // Do you reach Act 3 fast enough to skip Secret Portal?
    public boolean act4 = false;
    public boolean alwaysSpawnBottledTornado = true; // Assume you always have a power for Bottled Tornado to spawn
    public boolean alwaysSpawnBottledLightning = true; // Assume you always have a non-basic skill for Bottled Lightning to spawn
    public boolean alwaysSpawnBottledFlame = true; // Assume you always have a non-basic attack for Bottled Flame to spawn
    public boolean ignorePandoraCards = false; // Don't add the cards from Pandora's Box (as if you glitch it)

    // Event decisions

    public boolean takeSerpentGold = false;
    public boolean takeWarpedTongs = false;
    public boolean takeBigFishRelic = false;
    public boolean takeDeadAdventurerFight = false;
    public boolean takeMausoleumRelic = false;
    public boolean takeScrapOozeRelic = true;
    public boolean takeAddictRelic = true; // Always assume you pay, no taking Shame
    public boolean takeMysteriousSphereFight = false;
    public boolean takeRedMaskAct3 = true;
    public boolean takeMushroomFight = true;
    public boolean takeMaskedBanditFight = true;
    public boolean takeGoldenIdolWithoutCurse = true;
    public boolean takeGoldenIdolWithCurse = false;
    public boolean tradeGoldenIdolForBloody = true;
    public boolean takeCursedTome = true;
    public boolean tradeFaces = false;
    public boolean takeMindBloomGold = false; // Mind Bloom choices in order of priority
    public boolean takeMindBloomFight = true;
    public boolean takeMindBloomUpgrade = false;
    public boolean tradeGoldenIdolForMoney = true; // Moai Head event
    public boolean takePortal = false;
    public int numSensoryStoneCards = 1; // Keep it between 1 and 3, please!
    public boolean takeWindingHallsCurse = false;
    public boolean takeWindingHallsMadness = false;
    public boolean takeColosseumFight = false;
    public boolean takeDrugDealerRelic = false;
    public boolean takeDrugDealerTransform = true;
    public boolean takeLibraryCard = false;
    public boolean takeWeMeetAgainRelic = true;

    // Result filters

    public ArrayList<String> requiredAct1Cards = new ArrayList<>();
    public ArrayList<String> bannedAct1Cards = new ArrayList<>();
    public ArrayList<String> requiredAct1Relics = new ArrayList<>();
    public ArrayList<String> requiredRelics = new ArrayList<>();
    public ArrayList<String> requiredEvents = new ArrayList<>();
    public ArrayList<String> requiredCombats = new ArrayList<>();
    public int minimumElites = 0;
    public int maximumElites = 1;
    public int minimumCombats = 0;
    public int maximumCombats = 33;

    public SearchSettings() {
    }

    private void setDefaults() {
        relicsToBuy.add(JuzuBracelet.ID);
        requiredRelics.add(Calipers.ID);
        requiredAct1Cards.add(BodySlam.ID);
    }

    public static SearchSettings loadSettings() {
        try {
            File file = new File(configName);
            if (file.exists()) {
                Gson gson = new Gson();
                return gson.fromJson(new FileReader(file), SearchSettings.class);
            } else {
                SearchSettings settings = new SearchSettings();
                settings.setDefaults();
                settings.saveSettings();
                return settings;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("Could not load search settings: %s", e.getMessage()));
            SearchSettings settings = new SearchSettings();
            settings.setDefaults();
            return settings;
        }
    }

    private void saveSettings() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configName);
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not serialize the search settings.");
        }
    }

    public boolean checkIds() {
        ArrayList<ArrayList<String>> relicLists = new ArrayList<>();
        relicLists.add(relicsToBuy);
        relicLists.add(bossRelicsToTake);
        relicLists.add(requiredAct1Relics);
        relicLists.add(requiredRelics);

        ArrayList<ArrayList<String>> cardLists = new ArrayList<>();
        cardLists.add(cardsToBuy);
        cardLists.add(requiredAct1Cards);
        cardLists.add(bannedAct1Cards);

        ArrayList<ArrayList<String>> eventLists = new ArrayList<>();
        eventLists.add(requiredEvents);

        ArrayList<ArrayList<String>> encounterLists = new ArrayList<>();
        encounterLists.add(requiredCombats);

        boolean mistakesMade = false;

        for (ArrayList<String> relicList : relicLists) {
            ArrayList<String> mistakes = IdChecker.findBadRelicIds(relicList);
            if (mistakes.size() > 0) {
                System.out.println(String.format("WARNING: Bad relic ids/names found: %s", mistakes));
                mistakesMade = true;
            }
        }

        for (ArrayList<String> cardList : cardLists) {
            ArrayList<String> mistakes = IdChecker.findBadCardIds(cardList);
            if (mistakes.size() > 0) {
                System.out.println(String.format("WARNING: Bad card ids/names found: %s", mistakes));
                mistakesMade = true;
            }
        }

        for (ArrayList<String> eventList : eventLists) {
            ArrayList<String> mistakes = IdChecker.findBadEventIds(eventList);
            if (mistakes.size() > 0) {
                System.out.println(String.format("WARNING: Bad event ids/names found: %s", mistakes));
                mistakesMade = true;
            }
        }

        for (ArrayList<String> encounterList : encounterLists) {
            ArrayList<String> mistakes = IdChecker.findBadEncounterIds(encounterList);
            if (mistakes.size() > 0) {
                System.out.println(String.format("WARNING: Bad encounter ids/names found: %s", mistakes));
                mistakesMade = true;
            }
        }

        return mistakesMade;
    }
}
