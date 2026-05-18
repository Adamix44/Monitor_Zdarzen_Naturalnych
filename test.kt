import com.google.gson.JsonParser
import com.google.gson.JsonArray

fun parseLatLng(coords: JsonArray): Pair<Double, Double>? {
    try {
        if (coords.size() == 2 && coords[0].isJsonPrimitive) {
            return Pair(coords[1].asDouble, coords[0].asDouble)
        } else {
            var current = coords
            while (current.size() > 0 && current[0].isJsonArray) {
                current = current[0].asJsonArray
            }
            if (current.size() >= 2 && current[0].isJsonPrimitive) {
                return Pair(current[1].asDouble, current[0].asDouble)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun main() {
    val json = """[-95.912189, 47.020535]"""
    val array = JsonParser.parseString(json).asJsonArray
    println("Parsed: " + parseLatLng(array))
}
