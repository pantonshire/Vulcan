package language

import language.objects.LivingEntity
import language.objects.Player

object Events {

    val itemEvents = arrayOf(
            Event("right_click", Player("player")),
//            Event("update", Player("player")),
            Event("held", Player("player")),
            Event("hit_entity", LivingEntity("attacker"), LivingEntity("target"))
    )

    val foodEvents = arrayOf(
            Event("eaten", Player("player"))
    )

    val toolEvents = arrayOf(
            Event("block_broken", Player("player"))
    )

    val blockEvents = arrayOf(
            Event("walked_on", LivingEntity("entity")),
            Event("destroyed"),
            Event("right_clicked", Player("player")),
            Event("placed")
    )

    val playerEvents = arrayOf(
            Event("update"),
            Event("jump"),
            Event("sleep"),
            Event("hurt", LivingEntity("attacker")),
            Event("spawn")
    )

    val worldEvents = arrayOf(
            Event("setup")
    )

    val none: Array<Event> = arrayOf()

    fun getValidEvents(type: String): Array<Event> =
        when(type) {
            "item" -> itemEvents
            "food" -> foodEvents
            "block" -> blockEvents
            "player" -> playerEvents
            "world" -> worldEvents
            else -> none
         }

    fun getValidEventNames(type: String): Array<String> {
        val nameList = mutableListOf<String>()
        getValidEvents(type).mapTo(nameList, { it.name })
        return nameList.toTypedArray()
    }

    fun toNameMap(events: Array<Event>): Map<String, Event> {
        val map = hashMapOf<String, Event>()
        events.asSequence().forEach { map[it.name + ":"] = it }
        return map
    }
}