package com.jksalcedo.unitconverter

/**
 * Enum representing supported unit conversion categories and their conversion factors.
 */
enum class UnitCategory(val factorMap: Map<String, Double>) {
    LENGTH(mapOf(
        "meters" to 1.0,
        "feet" to 3.28084,
        "yards" to 1.09361,
        "inches" to 39.3701
    )),
    WEIGHT(mapOf(
        "kilograms" to 1.0,
        "pounds" to 2.20462,
        "grams" to 1000.0,
        "ounces" to 35.274
    )),
    TEMPERATURE(mapOf(
        "celsius" to 1.0,
        "fahrenheit" to 1.8, // Conversion handled separately
        "kelvin" to 1.0
    ));

    fun getFactor(fromUnit: String, toUnit: String): Double? {
        return if (this == TEMPERATURE) {
            when (Pair(fromUnit, toUnit)) {
                "celsius" to "fahrenheit" -> 1.8
                "fahrenheit" to "celsius" -> 1 / 1.8
                "celsius" to "kelvin" -> 1.0
                "kelvin" to "celsius" -> -273.15
                "fahrenheit" to "kelvin" -> { 1.0 / 1.8 }
                "kelvin" to "fahrenheit" -> { 1.8 }
                else -> null
            }
        } else {
            val baseFactor = factorMap[fromUnit] ?: return null
            val targetFactor = factorMap[toUnit] ?: return null
            targetFactor / baseFactor
        }
    }
}

/**
 * Data class to hold a value with its unit for fluent conversions.
 * @param value The numerical value.
 * @param unit The current unit.
 * @param category The unit category.
 */
data class UnitValue(val value: Double, val unit: String, val category: UnitCategory) {
    fun convertTo(toUnit: String): ConversionResult {
        return convert(value, unit, toUnit, category)
    }
}

/**
 * Data class to hold conversion results.
 * @param value The converted value.
 * @param success Whether the conversion was successful.
 * @param message Optional error or info message.
 */
data class ConversionResult(val value: Double, val success: Boolean, val message: String? = null)

/**
 * Converts a value from one unit to another within a category.
 * @param value The value to convert.
 * @param fromUnit The source unit.
 * @param toUnit The target unit.
 * @param category The unit category.
 * @return A ConversionResult containing the converted value or error details.
 */
fun convert(value: Double, fromUnit: String, toUnit: String, category: UnitCategory): ConversionResult {
    val factor = category.getFactor(fromUnit.lowercase(), toUnit.lowercase())
    return if (factor != null) {
        val baseValue = when {
            category == UnitCategory.TEMPERATURE && fromUnit == "celsius" && toUnit == "fahrenheit" -> value * 1.8 + 32
            category == UnitCategory.TEMPERATURE && fromUnit == "fahrenheit" && toUnit == "celsius" -> (value - 32) / 1.8
            category == UnitCategory.TEMPERATURE && fromUnit == "celsius" && toUnit == "kelvin" -> value + 273.15
            category == UnitCategory.TEMPERATURE && fromUnit == "kelvin" && toUnit == "celsius" -> value - 273.15
            category == UnitCategory.TEMPERATURE && fromUnit == "fahrenheit" && toUnit == "kelvin" -> (value - 32) * (5.0 / 9.0) + 273.15
            category == UnitCategory.TEMPERATURE && fromUnit == "kelvin" && toUnit == "fahrenheit" -> (value - 273.15) * 1.8 + 32
            else -> value * factor
        }
        ConversionResult(baseValue, true)
    } else {
        ConversionResult(0.0, false, "Invalid unit conversion: $fromUnit to $toUnit in ${category.name}")
    }
}

/**
 * Extension function to start a fluent unit conversion from Double.
 */
fun Double.toUnit(unit: String, category: UnitCategory): UnitValue {
    return UnitValue(this, unit.lowercase(), category)
}

/**
 * Extension properties for common units to initiate conversions.
 */
val Double.meters get() = toUnit("meters", UnitCategory.LENGTH)
val Double.feet get() = toUnit("feet", UnitCategory.LENGTH)
val Double.kilograms get() = toUnit("kilograms", UnitCategory.WEIGHT)
val Double.pounds get() = toUnit("pounds", UnitCategory.WEIGHT)
val Double.celsius get() = toUnit("celsius", UnitCategory.TEMPERATURE)
val Double.fahrenheit get() = toUnit("fahrenheit", UnitCategory.TEMPERATURE)