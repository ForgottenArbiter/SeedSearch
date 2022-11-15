# SeedSearch
A mod that searches through Slay the Spire seeds.

## Requirements

* Slay the Spire
* ModTheSpire (https://github.com/kiooeht/ModTheSpire)

Note: Because it runs a headless version of Slay the Spire and has no access to graphics, SeedSearch is not designed for compatibility with all other mods, and has no mod requirements. Also, any mod can change the outcome of seeds, so you should not use any mods, including BaseMod, with SeedSearch when searching for seeds to use in unmodded gameplay. Still, some character mods, such as Thorton and Jorbs Mod, have been found to work (or at least not crash/hang) with SeedSearch.

## Setup and Usage

To install the mod, download seedsearch.jar from the [Releases](https://github.com/ForgottenArbiter/SeedSearch/releases) page, or compile it yourself. Create a folder named "mods" in your Slay the Spire installation directory if it does not yet exist, and put seedsearch.jar in that folder. When you run the mod launcher, SeedSearch should now show up.

Run the mod once to search the first 100 seeds with some default settings. On the current patch of the game (v2.2), you should find one seed (54). A file called "searchConfig.json" should have been created in your current working directory. Edit that file to control the behavior of future searches. Note that the game will not launch. All output will be printed to the program's stdout.

## Settings

These are the descriptions of the settings in searchConfig.json. Edit them as you like to control the outcome of the seed search.

Some settings take lists of relics, cards, or events. For these settings, either use the ID (found in the game's code and output of Seed Search) or the name in the game's currently selected language. Seed Search will warn you if an invalid name or ID is provided. For example, the following two settings for requiredEvents are both valid:

` "requiredEvents": ["FaceTrader", "Beggar"],`  
` "requiredEvents": ["Face Trader", "Old Beggar"],`

### Core search parameters

* **ascensionLevel**: The ascension level used for the search (0 to 20)
* **playerClass**: The class used to search (IRONCLAD, THE_SILENT, DEFECT, or WATCHER)
* **startSeed**: The first seed to search
* **endSeed**: The last seed to search
* **verbose**: Whether to print out detailed information about each seed found
* **exitAfterSearch** Set to true to cause the program to immediately exit after search every seed
* **highestFloor** How many floors into the seed you want to search
* **ironcladUnlocks** How many unlocks are available for the Ironclad (0 to 5)
* **silentUnlocks** How many unlocks are available for the Silent (0 to 5)
* **defectUnlocks** How many unlocks are available for the Defect (0 to 5)
* **watcherUnlocks** How many unlocks are available for the Watcher (0 to 5)
* **firstBoss** How many act 1 bosses have been seen (0 to 3)
* **secondBoss** How many act 2 bosses have been seen (0 to 3)
* **thirdBoss** How many act 3 bosses have been seen (0 to 3)

### Navigation

These room weights are used to determine which path is taken through each map. The path with the lowest weight, obtained by adding the weights for each individual node on the path, is selected.

* **eliteRoomWeight**
* **monsterRoomWeight**
* **restRoomWeight**
* **shopRoomWeight**
* **eventRoomWeight**
* **wingBootsThreshold**: One or more charges of Wing Boots are used if they reduce the weight of the path by at least this much.

### General decisions

* **relicsToBuy**: These are the only relics which will be bought, in order of priority.
* **potionsToBuy**: These are the only potions which will be bought, in order of priority.
* **cardsToBuy**: These are the only cards which will be bought, in order of priority.
* **bossRelicsToTake**: These are the only boss relics which will be taken, in order of priority. All others will be skipped.
* **neowChoice**: Which Neow option to choose (0 to 3). 0 is the first option and 3 is the last (boss relic trade).
* **forceNeowLament**: Limits the Neow options to max HP and Neow's Lament.
* **useShovel**: Whether to dig at rest sites whenever available.
* **speedrunPace**: If set to true, then the secret portal event will not spawn.
* **act4**: If set to true, then the runs will include Act 4. Note that there is no check to ensure that Act 4 can be unlocked with the selected path.
* **alwaysSpawnBottledTornado**: If set to true, then the player is assumed to always have a power in their deck to make Bottled Tornado spawn.
* **alwaysSpawnBottledLightning**: Same as above, but for non-basic skills
* **alwaysSpawnBottledFlame**: Same as above, but for non-basic attacks

### Event decisions

All of these control which actions are taken at various events in the game.

* **takeSerpentGold**: Whether to take the gold for the curse in the Sssserpent event.
* **takeWarpedTongs**: Whether to take the Warped Tongs in the Accursed Blacksmith event.
* **takeBigFishRelic**: Whether to take the relic for the curse in the Big Fish event.
* **takeDeadAdventurerFight**: If set to true, always start a combat in the Dead Adventurer event if possible. Otherwise, always skip the event.
* **takeMausoleumRelic**: Whether to take the relic for the (poosible) curse in the Mausoleum event.
* **takeScrapOozeRelic**: Whether to dig for the relic in the Scrp Ooze event.
* **takeAddictRelic**: Whether to buy the relic in the Addict event.
* **takeMysteriousSphereFight**: Whether to take the fight in the Mysterious Sphere event.
* **takeRedMaskAct3**: Whether to trade all of your gold for the Red Mask in Act 3 if available.
* **takeMushroomFight**: Whether to fight the mushrooms in the Mushroom event.
* **takeMaskedBanditFight**: Whether to fight the Masked Bandits (and keep all your gold) in Act 2.
* **takeGoldenIdolWithoutCurse**: Whether to take the Golden Idol in the Golden Idol event (without gaining a curse)
* **takeGoldenIdolWithCurse**: Whether to take the Golden Idol in the Golden Idol event, gaining a curse
* **tradeGoldenIdolForBloody**: Whether to trade the Golden Idol for Bloody Idol in the Forgotten Altar event.
* **takeCursedTome**: Whether to take a book in the Cursed Tome event.
* **tradeFaces**: Whether to trade faces in the Face Trader event.
* **takeMindBloomGold**: Whether to take the gold in the Mind Bloom event, if available (these 3 options are in order of priority)
* **takeMindBloomFight**: Whether to take the fight in the Mind Bloom event, if the gold was not taken
* **takeMindBloomUpgrade**: Whether to take the upgrades in the Mind Bloom event, if the other options were not yet taken
* **tradeGoldenIdolForMoney**: Whether to trade the Golden Idol for gold in the Moai Head event, if available
* **takePortal**: Whether to take the portal straight to the boss in the Secret Portal event (not implemented yet)
* **numSensoryStoneCards**: How many cards to take in the Sensory Stone event (1 to 3)
* **takeWindingHallsCurse**: Whether to take the curse in Winding Halls
* **takeWindingHallsMadness**: Whether to take the Madness cards in Winding Halls
* **takeColosseumFight**: Whether to take the Slaver + Nob fight in the Colosseum
* **takeDrugDealerRelic**: Whether to take the Mutagenic Strength relic from the Drug Dealer event
* **takeDrugDealerTransform**: Whether to transform 2 cards in the Drug Dealer event
* **takeLibraryCard**: Whether to take a card from the Library event
* **takeWeMeetAgainRelic**: Whether to trade something in for a relic in the We Meet Again event.

### Result filters

These options control the criteria for deciding which seeds are selected as valid results.

* **requiredAct1Cards**: The cards which must be present somewhere in Act 1
* **bannedAct1Cards**: The cards which must not be present somewhere in Act 1
* **requiredAct1Relics**: The relics which must be acquired somewhere in Act 1
* **requiredAct1Potions**: The potions which must be acquired somewhere in Act 1
* **requiredRelics**: The relics which must be acquired anywhere in the run
* **requiredPotions**: The potions which must be acquired anywhere in the run
* **requiredEvents**: The events which must be encountered somewhere in the run
* **requiredCombats**: The combats which must be encountered somewhere in the run (e.g. "The Champ")
* **minimumElites**: The minimum number of elites which must be encountered
* **maximumElites**: The maximum number of elites which may be encountered
* **minimumCombats**: The minimum number of combats (event combats, normal combats, elites, and bosses)
* **maximumCombats**: The maximum number of combats
* **minimumRestSites**: The minimum number of rest sites which must be encountered

### Output filters

These options control the information which is shown to the user when the program is executed.

* **showNeowOptions**: Shows the neow options that are available
* **showCombats**: Shows the monsters and elite monsters that are fought in order
* **showBosses**: Shows the bosses that are fought
* **showRelics**: Shows the relics obtained
* **showShopRelics**: Shows the relics available in the shops
* **showShopCards**: Shows the cards available in the shops
* **showShopPotions**: Shows the potions available in the shops
* **showBossRelics**: Shows the potential boss relics
* **showEvents**: Shows the names of the events
* **showCardChoices**: Shows the cards that the player can choose from rewards
* **showPotions**: Shows the potions obtained from combats and events
* **showOtherCards**: Shows cards that the player obtains from events and relics
* **showRawRelicPools**: Shows the complete list of all relics in the seed in the order they're obtained

## Caveats

When searching through seeds, many assumptions must be made about your choices and game state. When you run a seed yourself, you may notice diverging behavior, especially later on in the run. This is expected. It can be caused by a large number of factors, including spending extra gold and being low on HP. Currently, many of those assumptions can be controlled through the config file.

## Current Major Restrictions

- Default seed filtering is very limited. To do something complicated, you must program it yourself.
- There is no checking to make sure that you can actually make it to Act 4.

