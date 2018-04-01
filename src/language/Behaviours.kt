package language

import language.objects.LivingEntity
import language.objects.Player

object Behaviours {

    private val itemBehaviours = arrayOf(
            Behaviour("right_click", Player("player", false)),
            Behaviour("held", Player("player", false)),
            Behaviour("hit_entity", LivingEntity("attacker", false), LivingEntity("target", false))
    )

    private val foodBehaviours = arrayOf(
            Behaviour("eaten", Player("player", false)),
            Behaviour("held", Player("player", false)),
            Behaviour("hit_entity", LivingEntity("attacker", false), LivingEntity("target", false))
    )

    private val toolBehaviours = arrayOf(
            Behaviour("block_broken", Player("player", false)),
            Behaviour("right_click", Player("player", false)),
            Behaviour("held", Player("player", false)),
            Behaviour("hit_entity", LivingEntity("attacker", false), LivingEntity("target", false))
    )

    private val blockBehaviours = arrayOf(
            Behaviour("walked_on", LivingEntity("entity", false)),
            Behaviour("destroyed"),
            Behaviour("right_clicked", Player("player", false)),
            Behaviour("placed", LivingEntity("placer", false))
    )

    private val playerBehaviours = arrayOf(
            Behaviour("update"),
            Behaviour("jump"),
            Behaviour("sleep"),
            Behaviour("hurt", LivingEntity("attacker", false)),
            Behaviour("spawn")
    )

    private val worldBehaviours = arrayOf(
            Behaviour("setup")
    )

    val none: Array<Behaviour> = arrayOf()

    fun getValidBehaviours(type: String): Array<Behaviour> =
        when(type) {
            "item" -> itemBehaviours
            "food" -> foodBehaviours
            "tool" -> toolBehaviours
            "block" -> blockBehaviours
            "player" -> playerBehaviours
            "world" -> worldBehaviours
            else -> none
         }

    fun getValidBehaviourNames(type: String): Array<String> {
        val nameList = mutableListOf<String>()
        getValidBehaviours(type).mapTo(nameList, { it.name })
        return nameList.toTypedArray()
    }

    fun toNameMap(events: Array<Behaviour>): Map<String, Behaviour> {
        val map = hashMapOf<String, Behaviour>()
        events.asSequence().forEach { map[it.name + ":"] = it }
        return map
    }
}