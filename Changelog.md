# Changelog
## Alpha 0.2.2
Released on 14th April 2018
* Added two new statements: `otherwise` and `otherwise if`. These act as "else" and "else if" statements. Syntax:  
```
if condition_a then
    ...
otherwise if condition_b then
    ...
otherwise
    ...
end if
```
* Fixed a bug where "is greater than" would be compiler to < rather than >

## Alpha 0.2.1
Released on 14th April 2018
* Rebuilt the parser from the ground-up in order to tackle some annoying edge cases. The parser is the first layer of the compiler, responsible for working out what kind of line each line is and extracting important information from them
* Added support for smart quotes; smart quotes can now be used to denote strings
* Revised syntax for "repeat" loops using an explicit counter variable: `repeat n times using a counter variable called foo`
* Added the "not" operator, which is a much neater way to get the complement of a boolean value, e.g. `not baa`. However, `baa's complement` should still work

## Alpha 0.2.0
Released on 7th April 2018
* Added the ability to declare and assign variables
* Declaring variables has the syntax `new [type] variable [name] = [value]` (constant can be written instead of variable to make it immutable)
* Assigning variables has the same syntax as setting attributes: `set [name] to [value]`
* Objects now contain fields which can be referenced and used (but not reassigned) within your code using the syntax `object's field`, for example: `player's position` is a vector equal to the player's current position in the world
* Added several new data types: boolean, integer, decimal, string, vector and world (note that most of these do not yet have all of the desired features, such as an easy way to perform arithmetic)
* Added boolean expressions, which are written with very English-y syntax, e.g. `a is equal to b` or `a is less than b and a is greater than c`
* Added if statements, which use the syntax `if condition then ... end if`
* All block behaviours now have access to the world
* Added an attribute class to store the value and data type of the different attributes for items, blocks, etc
* Lines starting with invalid words will now throw an error

## Alpha 0.1.2
Released on 7th April 2018
* Added food
* Food has three behaviours: eaten, held and hit_entity
* Food has all of the same attributes as item, plus a few extras: heal_amount, saturation, meat and eat_time

## Alpha 0.1.1
Released on 24th March 2018
* Fixed a crash in compiled mods caused when no items or no blocks were registered
* Removed the "climbable" attribute from block
* Added new attributes to block: flammable, burn_forever, redstone_signal and gravity
* Added a new generated Java class: VulcanBlockFalling.java, which is used for blocks with gravity

## Alpha 0.1.0
Released on 24th March 2018
* Added a new type of Vulcan file: block!
* Blocks have four behaviours: walked_on, placed, right_clicked and destroyed
* Blocks have 11 different attributes: name, texture, hardness, resistance, unbreakable, fragile, climbable, slipperiness, light, tool and tool_level
* Added a separate creative menu tab for blocks

## Alpha 0.0.5
Released on 23rd March 2018
* Removed indentation from ModItems file to be reimplemented at a later date
* A creative menu tab is now included to contain all modded items

## Alpha 0.0.4
Released on 14th March 2018
* Fixed issue #2: using an apostrophe within a string no longer causes an error
* If ".png" is written at the end of the texture attribute in an item, it will be ignored
* Setting attributes to invalid values will now show errors
* Introduced rules for whether or not a string is considered valid

## Alpha 0.0.3
Released on 14th March 2018
* Added "swing" method for LivingEntity objects to allow them to swing their arms too, like Player objects
* Reimplemented "jump" method for LivingEntity objects using some nasty reflection
* Added an "explode" method for both Player and LivingEntity objects. Fairly self-explanatory. KABOOM!

## Alpha 0.0.2
Released on 13th March 2018  
* Added code comments. A double forward slash // will cause the rest of the line to be ignored when building Java files. Example: `tell player to jump //Causes the player to jump in the air!`
