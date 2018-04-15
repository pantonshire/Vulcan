package language.objects

import language.DataType.*

object FieldManager {

    /** Returns all of the accessible fields for the specified object as an array. */
    fun getFields(vObject: VulcanObject): Array<VulcanObject> {

        val javaName = vObject.java

        return when (vObject.type) {

            BOOLEAN -> arrayOf(
                    VulcanBoolean   ("complement",          "(!$javaName)"                      )
            )

            INTEGER -> arrayOf()

            FLOAT   -> arrayOf(
                    VulcanInteger   ("integer_part",        "((int)$javaName)"                  ),
                    VulcanDecimal   ("decimal_part",        "($javaName - (int)$javaName)"      )
            )

            VECTOR3 -> arrayOf(
                    VulcanDecimal   ("x",                   "$javaName.getX()"                  ),
                    VulcanDecimal   ("y",                   "$javaName.getY()"                  ),
                    VulcanDecimal   ("z",                   "$javaName.getZ()"                  )
            )

            STRING  -> arrayOf(
                    VulcanInteger   ("length",              "$javaName.length()"                ),
                    VulcanString    ("lower_case",          "$javaName.toLowerCase()"           ),
                    VulcanString    ("upper_case",          "$javaName.toUpperCase()"           )
            )

            ENTITY  -> arrayOf(
                    VulcanBoolean   ("airborne",        "$javaName.isAirBorne"                  ),
                    VulcanBoolean   ("child",           "$javaName.isChild()"                   ),
                    VulcanBoolean   ("undead",          "$javaName.isEntityUndead()"            ),
                    VulcanBoolean   ("boss",            "(!$javaName.isNonBoss())"              ),
                    VulcanBoolean   ("burning",         "$javaName.isBurning()"                 ),
                    VulcanBoolean   ("sneaking",        "$javaName.isSneaking()"                ),
                    VulcanBoolean   ("sprinting",       "$javaName.isSprinting()"               ),
                    VulcanBoolean   ("swimming",        "$javaName.isInWater()"                 ),
                    VulcanBoolean   ("wet",             "$javaName.isWet()"                     ),
                    VulcanBoolean   ("flammable",       "(!$javaName.isImmuneToFire())"         ),
                    VulcanInteger   ("health",          "(int)($javaName.getHealth())",         cas = "$javaName.setHealth(VALUE);",    mutable = true      ),
                    VulcanInteger   ("max_health",      "(int)($javaName.getMaxHealth())"       ),
                    VulcanInteger   ("age",             "$javaName.ticksExisted"                ),
                    VulcanInteger   ("dimension",       "$javaName.dimension"                   ),
                    VulcanDecimal   ("x_position",      "$javaName.posX",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("y_position",      "$javaName.posY",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("z_position",      "$javaName.posZ",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("x_motion",        "$javaName.motionX",                    cas = null,                             mutable = true      ),
                    VulcanDecimal   ("y_motion",        "$javaName.motionY",                    cas = null,                             mutable = true      ),
                    VulcanDecimal   ("z_motion",        "$javaName.motionZ",                    cas = null,                             mutable = true      ),
                    VulcanVector3   ("position",        "$javaName.getPosition()"               ),
                    VulcanString    ("name",            "$javaName.getName()"                   ),
                    World           ("world",           "$javaName.world"                       )
            )

            PLAYER  -> arrayOf(
                    VulcanBoolean   ("in_creative_mode","$javaName.capabilities.isCreativeMode" ),
                    VulcanBoolean   ("flying",          "$javaName.capabilities.isFlying"       ),
                    VulcanBoolean   ("airborne",        "$javaName.isAirBorne"                  ),
                    VulcanBoolean   ("burning",         "$javaName.isBurning()"                 ),
                    VulcanBoolean   ("sneaking",        "$javaName.isSneaking()"                ),
                    VulcanBoolean   ("sprinting",       "$javaName.isSprinting()"               ),
                    VulcanBoolean   ("swimming",        "$javaName.isInWater()"                 ),
                    VulcanBoolean   ("wet",             "$javaName.isWet()"                     ),
                    VulcanBoolean   ("flammable",       "(!$javaName.isImmuneToFire())"         ),
                    VulcanInteger   ("level",           "$javaName.experienceLevel"             ),
                    VulcanInteger   ("health",          "(int)($javaName.getHealth())",         cas = "$javaName.setHealth(VALUE);",    mutable = true      ),
                    VulcanInteger   ("max_health",      "(int)($javaName.getMaxHealth())"       ),
                    VulcanInteger   ("age",             "$javaName.ticksExisted"                ),
                    VulcanInteger   ("dimension",       "$javaName.dimension"                   ),
                    VulcanDecimal   ("x_position",      "$javaName.posX",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("y_position",      "$javaName.posY",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("z_position",      "$javaName.posZ",                       cas = null,                             mutable = true      ),
                    VulcanDecimal   ("x_motion",        "$javaName.motionX",                    cas = null,                             mutable = true      ),
                    VulcanDecimal   ("y_motion",        "$javaName.motionY",                    cas = null,                             mutable = true      ),
                    VulcanDecimal   ("z_motion",        "$javaName.motionZ",                    cas = null,                             mutable = true      ),
                    VulcanVector3   ("position",        "$javaName.getPosition()"               ),
                    VulcanString    ("name",            "$javaName.getName()"                   ),
                    World           ("world",           "$javaName.world"                       )
            )

            WORLD   -> arrayOf(
                    VulcanBoolean   ("remote",          "$javaName.isRemote"                        ),
                    VulcanBoolean   ("boiling",         "$javaName.provider.doesWaterVaporize()"    ),
                    VulcanBoolean   ("raining",         "$javaName.isRaining()"                     ),
                    VulcanBoolean   ("thundering",      "$javaName.isThundering()"                  ),
                    VulcanBoolean   ("daytime",         "$javaName.isDaytime()"                     ),
                    VulcanInteger   ("build_limit",     "$javaName.getHeight()"                     ),
                    VulcanInteger   ("sea_level",       "$javaName.getSeaLevel()"                   ),
                    VulcanInteger   ("time",            "((int)$javaName.getWorldTime())",          cas = "$javaName.setWorldTime((VALUE)%23999);",     mutable = true  ),
                    VulcanString    ("name",            "$javaName.getWorldInfo().getWorldName()"   ),
                    VulcanVector3   ("spawn",           "$javaName.getSpawnPoint()",                cas = "$javaName.setSpawnPoint(VALUE);",            mutable = true  )
            )

            else -> arrayOf()
        }
    }
}