# Vulcan
An experimental tool for Minecraft modding, intended for teaching basic programming concepts to beginners.  
The general idea is that the user codes their mod in the Vulcan language, which I have attempted to make as easy to understand for new programmers as possible, then that code is converted into Java so that it can be compiled into a working Minecraft mod. For now, the compilation of the Java code is manual, but at some point in the future it may be done automatically.

# The language
The purpose of the Vulcan language is to be easy to understand for new programmers rather than being concise or neat. The syntax attempts to mimic the structure of an Engligh sentence; the goal is that every line of code should make sense to somebody who has never programmed before. For example, the current syntax for calling the method "jump" in object "player" would be:  
`tell player to jump`  
rather than the more traditional `player.jump()`. The syntax for passing arguments into a method varies based on what the method is, as this helps the code to look like English. For example, calling the method "burn" in the object "player" with the argument "5" would look like:  
`tell player to burn for 5 seconds`.  
The programmer will have no way to create new methods, so the only methods that they will be able to call are in-built ones.

# Planned features


# Downloads
[Alpha version 0.0.1](https://www.dropbox.com/s/6kws97t78ps6fmn/vulcan-alpha-0.0.1.jar?dl=0 "Alpha 0.0.1")
