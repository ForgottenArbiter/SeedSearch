# SeedSearch
A mod that searches through Slay the Spire seeds.

## Requirements

* Slay the Spire
* ModTheSpire (https://github.com/kiooeht/ModTheSpire)

Note: Use of other mods, including BaseMod, with SeedSearch is not recommended and will likely fail.

## Setup and Usage

Run the mod once to search the first 100 seeds with some default settings. On the current patch of the game (v1.1), you should find 3 seeds (1, 26, and 30). A file called "spireConfig.json" should have been created. Edit that file to control the behavior of future searches. Note that the game will not launch. All output will be printed to the program's stdout.

## Settings

These are the descriptions of the settings in spireConfig.json. Edit them as you like to control the outcome of the seed search.

Some settings take lists of relics, cards, or events. For these settings, either use the ID (found in the game's code and output of Seed Search) or the name in the current language. Seed Search will warn you if an invalid name or ID is provided. For example, the following two settings for requiredEvents are both valid:

` "requiredEvents": ["FaceTrader", "Beggar"],`  
` "requiredEvents": ["Face Trader", "Old Beggar"],`

### Core search parameters

* **ascensionLevel**: The ascension level used for the search (0 to 20)
* **playerClass**: The class used to search (IRONCLAD, THE_SILENT, or DEFECT)
* **startSeed**: The first seed to search
* **endSeed**: The last seed to search
* **verbose**: Whether to print out detailed information about each seed found
* **exitAfterSearch** Set to true to cause the program to immediately exit after search every seed

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
* **cardsToBuy**: These are the only cards which will be bought, in order of priority.
* **bossRelicsToTake**: These are the only boss relics which will be taken, in order of priority. All others will be skipped.
* **neowChoice**: Which Neow option to choose (0 to 3). Not implemented yet.
* **useShovel**: Whether to dig at rest sites whenever available.
* **speedrunPace**: If set to true, then the secret portal event will not spawn.
* **act4**: If set to true, then the runs will include Act 4. Note that there is no check to ensure that Act 4 can be unlocked with the selected path.
* **alwaysSpawnBottledTornado**: If set to true, then the player is assumed to always have a power in their deck to make Bottled Tornado spawn.

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
* **takePortal**: Whether to take the portal straight to the boss in the Secret Portal event
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
* **requiredAct1Relics**: The relics which must be acquired somewhere in Act 1
* **requiredRelics**: The relics which must be acquired anywhere in the run
* **requiredEvents**: The events which must be encountered somewhere in the run
* **minimumElites**: The minimum number of elites which must be encountered
* **maximumElites**: The maximum number of elites which may be encountered
* **minimumCombats**: The minimum number of combats (event combats, normal combats, elites, and bosses)
* **maximumCombats**: The maximum number of combats

## Caveats

When searching through seeds, many assumptions must be made about your choices and game state. When you run a seed yourself, you may notice diverging behavior, especially later on in the run. This is expected. It can be caused by a large number of factors, including spending extra gold and being low on HP. Currently, many of those assumptions can be controlled through the config file.

## Current Major Restrictions

- Potions are not handled at all
- Default seed filtering is very limited. To do something complicated, you must program it yourself.
- Neow options are not implemented. Right now, the tool assumes a starter relic trade.
- There is no checking to make sure that you can actually make it to Act 4.

