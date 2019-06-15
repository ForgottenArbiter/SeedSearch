package seedsearch.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seedsearch.SearchSettings;
import seedsearch.SeedRunner;
import seedsearch.SeedSearch;

import java.util.ArrayList;

import static java.lang.System.exit;
import static org.apache.logging.log4j.Level.OFF;

@SpirePatch(
        clz= CardCrawlGame.class,
        method="create"
)
public class StartSearch {

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

        Class[] noLoggingClasses = {
                AbstractDungeon.class,
                AbstractPlayer.class,
                TheEnding.class,
                UnlockTracker.class,
                RoomTypeAssigner.class,
                CardLibrary.class,
                EventHelper.class,
                ShopScreen.class,
                CardGroup.class,
                CardHelper.class
        };

        for (Class c : noLoggingClasses) {
            Logger logger = LogManager.getLogger(c);
            ((org.apache.logging.log4j.core.Logger)logger).setLevel(OFF);
        }

        LoadImagePatch.defaultTexture = ImageMaster.loadImage("images/npcs/rug/eng.png");
        SeedSearch.settings = SearchSettings.loadSettings();
        SeedSearch.runner = new SeedRunner(SeedSearch.settings);
        SeedSearch.loadingEnabled = false;
        SeedSearch.search();
        exit(0);
    }

}
