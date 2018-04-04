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
                    VulcanInteger   ("health",          "$javaName.getHealth()"                 ),
                    VulcanInteger   ("max_health",      "$javaName.getMaxHealth()"              ),
                    VulcanInteger   ("age",             "$javaName.ticksExisted"                ),
                    VulcanInteger   ("dimension",       "$javaName.dimension"                   ),
                    VulcanDecimal   ("x_position",      "$javaName.posX"                        ),
                    VulcanDecimal   ("y_position",      "$javaName.posY"                        ),
                    VulcanDecimal   ("z_position",      "$javaName.posZ"                        ),
                    VulcanDecimal   ("x_motion",        "$javaName.motionX"                     ),
                    VulcanDecimal   ("y_motion",        "$javaName.motionY"                     ),
                    VulcanDecimal   ("z_motion",        "$javaName.motionZ"                     ),
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
                    VulcanInteger   ("health",          "$javaName.getHealth()"                 ),
                    VulcanInteger   ("max_health",      "$javaName.getMaxHealth()"              ),
                    VulcanInteger   ("age",             "$javaName.ticksExisted"                ),
                    VulcanInteger   ("dimension",       "$javaName.dimension"                   ),
                    VulcanDecimal   ("x_position",      "$javaName.posX"                        ),
                    VulcanDecimal   ("y_position",      "$javaName.posY"                        ),
                    VulcanDecimal   ("z_position",      "$javaName.posZ"                        ),
                    VulcanDecimal   ("x_motion",        "$javaName.motionX"                     ),
                    VulcanDecimal   ("y_motion",        "$javaName.motionY"                     ),
                    VulcanDecimal   ("z_motion",        "$javaName.motionZ"                     ),
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
                    VulcanInteger   ("time",            "((int)$javaName.getWorldTime())"           ),
                    VulcanString    ("name",            "$javaName.getWorldInfo().getWorldName()"   ),
                    VulcanVector3   ("spawn",           "$javaName.getSpawnPoint()"                 )
            )

            else -> arrayOf()
        }
    }
}