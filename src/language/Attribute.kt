package language

import builder.Builder
import utils.VulcanUtils

abstract class Attribute<out T: Any>(private val type: DataType, builder: Builder, name: String, defaultValue: T) {

    private var value: T = defaultValue

    init {
       registerAttribute(builder, name)
    }

    private fun registerAttribute(builder: Builder, name: String) {
        builder.attributes[name] = this
    }

    fun get(): T = value

    fun set(newValue: String) {
        value = parse(newValue)
    }

    abstract fun parse(newValue: String): T

    protected fun typeError(value: String): String = "$value is not a valid ${type.typeName}"
}

class BooleanAttribute(builder: Builder, name: String, defaultValue: Boolean) : Attribute<Boolean>(DataType.BOOLEAN, builder, name, defaultValue) {
    override fun parse(newValue: String): Boolean {
        if(newValue == "true" || newValue == "false") {
            return newValue == "true"
        }

        throw IllegalArgumentException(typeError(newValue))
    }
}

class IntegerAttribute(builder: Builder, name: String, defaultValue: Int) : Attribute<Int>(DataType.INTEGER, builder, name, defaultValue) {
    override fun parse(newValue: String): Int {
        try {
            return newValue.toInt()
        } catch(exception: NumberFormatException) {
            throw IllegalArgumentException(typeError(newValue))
        }
    }
}

class FloatAttribute(builder: Builder, name: String, defaultValue: Double) : Attribute<Double>(DataType.FLOAT, builder, name, defaultValue) {
    override fun parse(newValue: String): Double {
        try {
            return newValue.toDouble()
        } catch(exception: NumberFormatException) {
            throw IllegalArgumentException(typeError(newValue))
        }
    }
}

class StringAttribute(builder: Builder, name: String, defaultValue: String) : Attribute<String>(DataType.STRING, builder, name, defaultValue) {
    override fun parse(newValue: String): String {
        if(VulcanUtils.isValidInputString(newValue)) {
            return VulcanUtils.sanitiseInputString(newValue)
        } else {
            throw IllegalArgumentException(typeError(newValue))
        }
    }
}
