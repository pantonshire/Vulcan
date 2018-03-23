# Changelog
## Alpha 0.0.5
In development
* Removed indentation from ModItems file to be reimplemented at a later date
* A creative menu tab is now included to contain all modded items

## Alpha 0.0.4
Released on 14th March 2018
* Fixed issue #2: using an apostrophe within a string no longer causes an error
* If ".png" is written at the end of the texture attribute in an item, it will be ignored
* Setting attributes to invalid values will now show errors.
* Introduced rules for whether or not a string is considered valid

## Alpha 0.0.3
Released on 14th March 2018
* Added "swing" method for LivingEntity objects to allow them to swing their arms too, like Player objects
* Reimplemented "jump" method for LivingEntity objects using some nasty reflection
* Added an "explode" method for both Player and LivingEntity objects. Fairly self-explanatory. KABOOM!

## Alpha 0.0.2
Released on 13th March 2018  
* Added code comments. A double forward slash // will cause the rest of the line to be ignored when building Java files. Example: `tell player to jump //Causes the player to jump in the air!`
