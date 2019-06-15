package seedsearch.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.map.RoomTypeAssigner;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.DisplayOption;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seedsearch.SearchSettings;
import seedsearch.SeedRunner;
import seedsearch.SeedSearch;
import seedsearch.SeedSearchScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static java.lang.System.exit;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="create"
)
public class StartSearch {


/*    public static void Postfix(CardCrawlGame _instance) {
        CardCrawlGame.mainMenuScreen = new MainMenuScreen();
        try {
            Field targetField = CardCrawlGame.class.getDeclaredField("displayCursor");
            targetField.setAccessible(true);
            targetField.set(_instance, true);
        } catch (Exception e) {
            throw new RuntimeException("Reflection error.");
        }
        SeedSearch.screen = new SeedSearchScreen();
        System.out.println(Settings.WIDTH);
        System.out.println(Settings.HEIGHT);
        System.out.println(Settings.VERT_LETTERBOX_AMT);
        System.out.println(Settings.HORIZ_LETTERBOX_AMT);
        Logger abstractDungeonLogger = LogManager.getLogger(AbstractDungeon.class);
        Logger unlockTrackerLogger = LogManager.getLogger(UnlockTracker.class);
        Logger roomTypeLogger = LogManager.getLogger(RoomTypeAssigner.class);
        Logger cardLibraryLogger = LogManager.getLogger(CardLibrary.class);
        Logger eventHelperLogger = LogManager.getLogger(EventHelper.class);
        Logger shopScreenLogger = LogManager.getLogger(ShopScreen.class);
        Logger cardGroupLogger = LogManager.getLogger(CardGroup.class);
        Logger cardHelperLogger = LogManager.getLogger(CardHelper.class);
        ((org.apache.logging.log4j.core.Logger)abstractDungeonLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)unlockTrackerLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)roomTypeLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardLibraryLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)eventHelperLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)shopScreenLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardGroupLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardHelperLogger).setLevel(Level.OFF);
        LoadImagePatch.defaultTexture = ImageMaster.loadImage("images/npcs/rug/eng.png");
        SeedSearch.settings = new SearchSettings();
        SeedSearch.runner = new SeedRunner(SeedSearch.settings);
        SeedSearch.loadingEnabled = false;
        SeedSearch.search();
    }*/



    public static void Replace(CardCrawlGame _instance) {

        CardCrawlGame.publisherIntegration = new SteamIntegration();

        CardCrawlGame.saveSlotPref = SaveHelper.getPrefs("STSSaveSlots");
        CardCrawlGame.saveSlot = CardCrawlGame.saveSlotPref.getInteger("DEFAULT_SLOT", 0);
        CardCrawlGame.playerPref = SaveHelper.getPrefs("STSPlayer");
        CardCrawlGame.playerName = CardCrawlGame.saveSlotPref.getString(SaveHelper.slotName("PROFILE_NAME", CardCrawlGame.saveSlot), "");
        if (CardCrawlGame.playerName.equals("")) {
            CardCrawlGame.playerName = CardCrawlGame.playerPref.getString("name", "");
        }
        CardCrawlGame.alias = CardCrawlGame.playerPref.getString("alias", "");
        Settings.initialize(false);
        Settings.displayOptions = new ArrayList<>();
        Settings.displayOptions.add(new DisplayOption(0,0));
        CardCrawlGame.languagePack = new LocalizedStrings();
        CardCrawlGame.cardPopup = new SingleCardViewPopup();
        CardCrawlGame.relicPopup = new SingleRelicViewPopup();

        CardCrawlGame.music = new MusicMaster();
        CardCrawlGame.sound = new SoundMaster();

        AbstractCreature.initialize();
        AbstractCard.initialize();
        GameDictionary.initialize();
        ImageMaster.initialize();
        AbstractPower.initialize();
        FontHelper.initialize();
        AbstractCard.initializeDynamicFrameWidths();
        UnlockTracker.initialize();
        CardLibrary.initialize();
        RelicLibrary.initialize();
        InputHelper.initialize();
        TipTracker.initialize();
        ModHelper.initialize();
        ShaderHelper.initializeShaders();
        UnlockTracker.retroactiveUnlock();

        //CardCrawlGame.characterManager = new CharacterManager();

        //CardCrawlGame.mainMenuScreen = new MainMenuScreen();
        Logger abstractDungeonLogger = LogManager.getLogger(AbstractDungeon.class);
        Logger abstractPlayerLogger = LogManager.getLogger(AbstractPlayer.class);
        Logger theEndingLogger = LogManager.getLogger(TheEnding.class);
        Logger unlockTrackerLogger = LogManager.getLogger(UnlockTracker.class);
        Logger roomTypeLogger = LogManager.getLogger(RoomTypeAssigner.class);
        Logger cardLibraryLogger = LogManager.getLogger(CardLibrary.class);
        Logger eventHelperLogger = LogManager.getLogger(EventHelper.class);
        Logger shopScreenLogger = LogManager.getLogger(ShopScreen.class);
        Logger cardGroupLogger = LogManager.getLogger(CardGroup.class);
        Logger cardHelperLogger = LogManager.getLogger(CardHelper.class);
        ((org.apache.logging.log4j.core.Logger)abstractDungeonLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)abstractPlayerLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)theEndingLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)unlockTrackerLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)roomTypeLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardLibraryLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)eventHelperLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)shopScreenLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardGroupLogger).setLevel(Level.OFF);
        ((org.apache.logging.log4j.core.Logger)cardHelperLogger).setLevel(Level.OFF);
        LoadImagePatch.defaultTexture = ImageMaster.loadImage("images/npcs/rug/eng.png");
        SeedSearch.settings = SearchSettings.loadSettings();
        SeedSearch.runner = new SeedRunner(SeedSearch.settings);
        SeedSearch.loadingEnabled = false;
        SeedSearch.search();
        exit(0);
    }

}
