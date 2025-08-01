# Kotlin Unit Conversion Library

[![](https://jitpack.io/v/jksalcedo/unitconverter-kt.svg)](https://jitpack.io/#jksalcedo/unitconverter-kt)  [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-blue.svg?logo=kotlin)](https://kotlinlang.org/) 

A simple yet robust Kotlin library to convert numerical values between different units. Supports multiple measurement categories including Length, Weight, and Temperature—with special handling for temperature’s non-linear conversions.

---

## Features

- **Categorized Units:** Units are logically grouped into `UnitCategory` enums (e.g., `LENGTH`, `WEIGHT`, `TEMPERATURE`).
- **Fluent Conversion API:** Introduces a new `UnitValue` data class and `Double.toUnit` extension, enabling readable conversions like `10.0.meters.convertTo("feet")`.
- **Accurate Temperature Conversion:** Handles complex temperature conversions (Celsius, Fahrenheit, Kelvin) with specific affine (addition/subtraction) logic, not just multiplication.
- **Result Object:** Returns a `ConversionResult` data class, providing the converted value, a success flag, and an optional message for failed conversions.
- **Case-Insensitive Unit Matching:** Unit names are handled case-insensitively for user convenience.

---

## API
| Function / Class                      | Description                                                                 | Parameters                                      | Returns                 |
|---------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------|-------------------------|
| **`UnitCategory`**                    | Enum representing supported unit conversion categories and their conversion factors. | —                                               | —                       |
| - `LENGTH`                            | Defines conversion factors for length units (meters, feet, yards, inches).  | —                                               | —                       |
| - `WEIGHT`                            | Defines conversion factors for weight units (kilograms, pounds, grams, ounces). | —                                               | —                       |
| - `TEMPERATURE`                       | Defines conversion factors for temperature units (celsius, fahrenheit, kelvin). | —                                               | —                       |
| - `getFactor(fromUnit: String, toUnit: String): Double?` | Retrieves the conversion factor between two units within the category. | `fromUnit: String`, `toUnit: String`            | `Double?`               |
| **`UnitValue`**                       | Data class to hold a value with its unit for fluent conversions.            | `value: Double`, `unit: String`, `category: UnitCategory` | —                   |
| - `convertTo(toUnit: String): ConversionResult` | Converts the value to the specified unit. | `toUnit: String`                                | `ConversionResult`      |
| **`ConversionResult`**                | Data class to hold conversion results.                                     | `value: Double`, `success: Boolean`, `message: String? = null` | —                   |
| **`convert(value: Double, fromUnit: String, toUnit: String, category: UnitCategory): ConversionResult`** | Converts a value from one unit to another within a category. | `value: Double`, `fromUnit: String`, `toUnit: String`, `category: UnitCategory` | `ConversionResult` |
| **`toUnit(unit: String, category: UnitCategory): UnitValue`** | Extension function to start a fluent unit conversion from `Double`. | `unit: String`, `category: UnitCategory`         | `UnitValue`             |
| **`Double.meters`**                   | Extension property to initiate a length conversion from meters.             | —                                               | `UnitValue`             |
| **`Double.feet`**                     | Extension property to initiate a length conversion from feet.               | —                                               | `UnitValue`             |
| **`Double.kilograms`**                | Extension property to initiate a weight conversion from kilograms.          | —                                               | `UnitValue`             |
| **`Double.pounds`**                   | Extension property to initiate a weight conversion from pounds.             | —                                               | `UnitValue`             |
| **`Double.celsius`**                  | Extension property to initiate a temperature conversion from celsius.       | —                                               | `UnitValue`             |
| **`Double.fahrenheit`**               | Extension property to initiate a temperature conversion from fahrenheit.    | —                                               | `UnitValue`             |

## Installation

Add the JitPack repository to your project’s root `settings.gradle.kts` (or `settings.gradle`):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_NON_DECLARED_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Add this line
    }
}
```

Then, add the unit-converter dependency to your module’s build.gradle.kts (or build.gradle):
```groovy
// app/build.gradle.kts (or your JVM project's build.gradle.kts)
dependencies {
    implementation("com.github.jksalcedo:unitconverter-kt:1.0.0")
}
```

## Usage

All conversion functions return a ConversionResult object. Always check result.success before using result.value.

**1. Using the Fluent Double.toUnit().convertTo() API**

This is the recommended way for highly readable conversions.
```kotlin
import com.jksalcedo.unitconverter.UnitCategory
import com.jksalcedo.unitconverter.meters
import com.jksalcedo.unitconverter.kilograms
import com.jksalcedo.unitconverter.celsius
import com.jksalcedo.unitconverter.fahrenheit
import com.jksalcedo.unitconverter.toUnit

fun main() {
    // Length Conversion
    val metersToFeet = 10.0.meters.convertTo("feet")
    if (metersToFeet.success) {
        println("10 meters = ${metersToFeet.value} feet") // Output: 10 meters = 32.8084 feet
    }

    // Weight Conversion
    val poundsToKg = 50.0.pounds.convertTo("kilograms")
    if (poundsToKg.success) {
        println("50 pounds = ${poundsToKg.value} kilograms") // Output: 50 pounds = 22.6796 kilograms
    }

    // Temperature Conversion (Celsius to Fahrenheit)
    val cToF = 25.0.celsius.convertTo("fahrenheit")
    if (cToF.success) {
        println("25 Celsius = ${cToF.value} Fahrenheit") // Output: 25 Celsius = 77.0 Fahrenheit
    }

    // Temperature Conversion (Fahrenheit to Celsius)
    val fToC = 68.0.fahrenheit.convertTo("celsius")
    if (fToC.success) {
        println("68 Fahrenheit = ${fToC.value} Celsius") // Output: 68 Fahrenheit = 20.0 Celsius
    }

    // Temperature Conversion (Kelvin to Fahrenheit using generic toUnit)
    val kToF = 300.0.toUnit("kelvin", UnitCategory.TEMPERATURE).convertTo("fahrenheit")
    if (kToF.success) {
        println("300 Kelvin = ${kToF.value} Fahrenheit") // Output: 300 Kelvin = 80.33 Fahrenheit
    }

    // Invalid Unit within a category
    val invalidUnit = 10.0.meters.convertTo("notAUnit")
    if (!invalidUnit.success) {
        println("Error: ${invalidUnit.message}") // Output: Error: Invalid unit conversion: meters to notAUnit in LENGTH
    }
}
```

**2. Using the standalone convert function**

This provides the underlying logic if you prefer not to use the fluent API.
Kotlin

```kotlin
import com.jksalcedo.unitconverter.convert
import com.jksalcedo.unitconverter.UnitCategory

fun main() {
    // Length Conversion
    val yardsToMeters = convert(5.0, "yards", "meters", UnitCategory.LENGTH)
    if (yardsToMeters.success) {
        println("5 yards = ${yardsToMeters.value} meters") // Output: 5 yards = 4.572 meters
    }

    // Weight Conversion
    val kgToOunces = convert(1.0, "kilograms", "ounces", UnitCategory.WEIGHT)
    if (kgToOunces.success) {
        println("1 kilogram = ${kgToOunces.value} ounces") // Output: 1 kilogram = 35.274 ounces
    }

    // Invalid Conversion (category mismatch)
    val invalidConversion = convert(10.0, "meters", "kilograms", UnitCategory.LENGTH)
    if (!invalidConversion.success) {
        println("Error: ${invalidConversion.message}") // Output: Error: Invalid unit conversion: meters to kilograms in LENGTH
    }
}
```

## Limitations

- **Specific Categories Only:** Currently supports only LENGTH, WEIGHT, and TEMPERATURE.
- **Hardcoded Units:** Units within each category are predefined in the UnitCategory enum. Adding new units requires modifying the enum.
- **JVM-Specific:** Built for the JVM. Not directly compatible with Kotlin/Native or Kotlin/JS.
- **Floating-Point Precision:** Uses Double. Standard floating-point limitations apply for very large/small numbers.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Make your changes and add tests if needed.
4. Open a pull request with a clear description of your changes.

Please follow the existing code style and conventions.
