# Contents
* [About](https://github.com/Pantonshire/Vulcan/blob/master/README.md#about)
* [The language](https://github.com/Pantonshire/Vulcan/blob/master/README.md#the-language)
* [Planned features](https://github.com/Pantonshire/Vulcan/blob/master/README.md#planned-features)
* [Downloads](https://github.com/Pantonshire/Vulcan/blob/master/README.md#downloads)
   * [Recommended](https://github.com/Pantonshire/Vulcan/blob/master/README.md#recommended)
   * [Latest](https://github.com/Pantonshire/Vulcan/blob/master/README.md#latest)
   * [Older](https://github.com/Pantonshire/Vulcan/blob/master/README.md#older)

# About
Vulcan is an experimental tool for Minecraft modding, intended for teaching basic programming concepts to beginners.  
The general idea is that the user codes their mod in the Vulcan language, which I have attempted to make as easy to understand for new programmers as possible, then that code is converted into Java so that it can be compiled into a working Minecraft mod. For now, the compilation of the Java code is manual, but at some point in the future it may be done automatically.

# The language
The purpose of the Vulcan language is to be easy to understand for new programmers rather than being concise or neat. The syntax attempts to mimic the structure of an Engligh sentence; the goal is that every line of code should make sense to somebody who has never programmed before. For example, the current syntax for calling the method "jump" in object "player" would be:  
`tell player to jump`  
rather than the more traditional `player.jump()`. The syntax for passing arguments into a method varies based on what the method is, as this helps the code to look like English. For example, calling the method "burn" in the object "player" with the argument "5" would look like:  
`tell player to burn for 5 seconds`.  
The programmer will have no way to create new methods, so the only methods that they will be able to call are in-built ones.

# Planned features
* If statements `if condition then ... end`
* Else and else if `if condition then ... otherwise if condition ... otherwise ... end`
* While loops `while condition do ... end`
* For loops `repeat x times ... end`
* "player" type for changing player attributes and recieving player-based events
* "world" type for doing worldgen stuff
* "block" type for adding custom blocks into the game
* An event handler to recieve Forge events
* A way to add crafting recipes
* Ability to reference items and blocks by name, item and block object types
* Method to add potion effects to entities
* Types for item subclasses: "food" and "tool"
* A "throwable" type to make throwable items that spawn projectiles when used
* "biome" type for adding new biomes into the overworld
* "dimension" type for adding new dimensions (don't expect this any time soon!)
* "fluid" type for adding liquids, automatically create bucket
* Possible integration with existing mods?
* Ability for objects to reference themselves with "this" or "self", or similar
* A better GUI for building the mod
* An IDE for Vulcan?  

# Downloads
## Recommended
[Vulcan Alpha 0.0.2](https://www.dropbox.com/s/o6tn2rlp44eo6pu/vulcan-alpha-0.0.2.jar?dl=0 "Alpha 0.0.2")  

## Latest
[Vulcan Alpha 0.0.2](https://www.dropbox.com/s/o6tn2rlp44eo6pu/vulcan-alpha-0.0.2.jar?dl=0 "Alpha 0.0.2")  

## Older
[Vulcan Alpha 0.0.1](https://www.dropbox.com/s/6kws97t78ps6fmn/vulcan-alpha-0.0.1.jar?dl=0 "Alpha 0.0.1")
