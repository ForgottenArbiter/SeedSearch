package seedsearch;

import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import seedsearch.patches.MonsterHelperPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class IdChecker {

    private static String[] eventIds = {
            AccursedBlacksmith.ID,
            Bonfire.ID,
            FountainOfCurseRemoval.ID,
            Designer.ID,
            Duplicator.ID,
            Lab.ID,
            GremlinMatchGame.ID,
            GoldShrine.ID,
            PurificationShrine.ID,
            Transmogrifier.ID,
            GremlinWheelGame.ID,
            UpgradeShrine.ID,
            FaceTrader.ID,
            NoteForYourself.ID,
            WeMeetAgain.ID,
            WomanInBlue.ID,
            BigFish.ID,
            Cleric.ID,
            DeadAdventurer.ID,
            GoldenWing.ID,
            GoldenIdolEvent.ID,
            GoopPuddle.ID,
            ForgottenAltar.ID,
            ScrapOoze.ID,
            Sssserpent.ID,
            LivingWall.ID,
            Mushrooms.ID,
            Nloth.ID,
            ShiningLight.ID,
            Vampires.ID,
            Ghosts.ID,
            Addict.ID,
            BackToBasics.ID,
            Beggar.ID,
            CursedTome.ID,
            DrugDealer.ID,
            KnowingSkull.ID,
            MaskedBandits.ID,
            Nest.ID,
            TheLibrary.ID,
            TheMausoleum.ID,
            TheJoust.ID,
            Colosseum.ID,
            MysteriousSphere.ID,
            SecretPortal.ID,
            TombRedMask.ID,
            Falling.ID,
            WindingHalls.ID,
            MoaiHead.ID,
            SensoryStone.ID,
            MindBloom.ID
    };

    private static HashMap<String, String> eventMap;
    private static HashMap<String, String> cardMap;
    private static HashMap<String, String> relicMap;
    private static HashMap<String, String> encounterMap;

    static {
        eventMap = new HashMap<>();
        for (String id : eventIds) {
            eventMap.put(EventHelper.getEventName(id), id);
        }

        Set<String> cardIdSet = CardLibrary.cards.keySet();
        cardMap = new HashMap<>();
        for (String id : cardIdSet) {
            cardMap.put(CardLibrary.cards.get(id).name, id);
        }

        relicMap = new HashMap<>();
        ArrayList<ArrayList<AbstractRelic>> relicLists = new ArrayList<>();
        relicLists.add(RelicLibrary.starterList);
        relicLists.add(RelicLibrary.commonList);
        relicLists.add(RelicLibrary.uncommonList);
        relicLists.add(RelicLibrary.rareList);
        relicLists.add(RelicLibrary.bossList);
        relicLists.add(RelicLibrary.specialList);
        relicLists.add(RelicLibrary.shopList);
        for (ArrayList<AbstractRelic> relicList : relicLists) {
            for (AbstractRelic relic : relicList) {
                relicMap.put(relic.name, relic.relicId);
            }
        }

        encounterMap = new HashMap<>();
        MonsterHelper.uploadEnemyData();
        for (String encounter : MonsterHelperPatch.ids) {
            String encounterName = MonsterHelper.getEncounterName(encounter);
            if (!encounterName.equals("")) {
                encounterMap.put(encounterName, encounter);
            } else {
                encounterMap.put(encounter, encounter);
            }
        }
    }

    private static ArrayList<String> findBadIds(HashMap<String, String> map, ArrayList<String> ids) {
        ArrayList<String> mistakes = new ArrayList<>();
        for (int i = 0 ; i < ids.size(); i++) {
            String id = ids.get(i);
            if (!map.containsValue(id)) {
                if (!map.containsKey(id)) {
                    mistakes.add(id);
                } else {
                    ids.set(i, map.get(id));
                }
            }
        }
        return mistakes;
    }

    public static ArrayList<String> findBadEventIds(ArrayList<String> ids) {
        return findBadIds(eventMap, ids);
    }

    public static ArrayList<String> findBadCardIds(ArrayList<String> ids) {
        return findBadIds(cardMap, ids);
    }

    public static ArrayList<String> findBadRelicIds(ArrayList<String> ids) {
        return findBadIds(relicMap, ids);
    }

    public static ArrayList<String> findBadEncounterIds(ArrayList<String> ids) {
        return findBadIds(encounterMap, ids);
    }

}
