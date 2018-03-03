package language

object Events {

    val itemEvents = arrayOf(
            Event("right_click", "player"),
            Event("update", "player"),
            Event("held", "player")
    )

    val foodEvents = arrayOf(
            Event("eaten", "player")
    )

    val blockEvents = arrayOf(
            Event("walked_on", "entity"),
            Event("destroyed"),
            Event("right_clicked", "player"),
            Event("placed")
    )

    val playerEvents = arrayOf(
            Event("update"),
            Event("jump"),
            Event("sleep"),
            Event("hurt", "attacker"),
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