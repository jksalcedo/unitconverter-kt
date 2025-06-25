import com.jksalcedo.unitconverter.celsius
import com.jksalcedo.unitconverter.meters
import kotlin.math.roundToInt
import com.jksalcedo.unitconverter.UnitCategory

fun main() {
    val result = 5.0.meters.convertTo("com.jksalcedo.feet")
    if (result.success) println("5 com.jksalcedo.meters = ${result.value.roundToInt()} com.jksalcedo.feet") // ~16 feet

    val tempResult = 25.0.celsius.convertTo("com.jksalcedo.fahrenheit")
    if (tempResult.success) println("25°C = ${tempResult.value.roundToInt()}°F") // ~77°F
}