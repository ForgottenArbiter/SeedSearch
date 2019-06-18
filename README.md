# SeedSearch
A mod that searches through Slay the Spire seeds.

## Requirements

* Slay the Spire
* ModTheSpire (https://github.com/kiooeht/ModTheSpire)

Note: Use of other mods, including BaseMod, with SeedSearch is not recommended.

## Setup and Usage

Run the mod once to search the first 100 seeds with some default settings. On the current patch of the game (v1.0), you should find 3 seeds (1, 26, and 30). A file called "spireConfig.json" should have been created. Edit that file to control the behavior of future searches. Note that the game will not launch. All output will be printed to the program's stdout.

To see some comments about the settings, see the corresponding source file: https://github.com/ForgottenArbiter/SeedSearch/blob/master/src/main/java/seedsearch/SearchSettings.java

## Caveats

When searching through seeds, many assumptions must be made about your choices and game state. When you run a seed yourself, you may notice diverging behavior, especially later on in the run. This is expected. It can be caused by a large number of factors, including spending extra gold and being low on HP. Currently, many of those assumptions can be controlled through the config file.

## Current Major Restrictions

- Potions are not handled at all
- Default seed filtering is very limited. To do something complicated, you must program it yourself.
- Neow options are not implemented. Right now, the tool assumes a starter relic trade.
- There is no checking to make sure that you can actually make it to Act 4.

## 