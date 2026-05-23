import com.google.gson.JsonParser
import com.google.gson.JsonArray

/**
 * Parsuje strukturę JSON z koordynatami geograficznymi dostarczanymi przez NASA EONET API.
 * NASA EONET API zwraca współrzędne w formacie [Długość geograficzna, Szerokość geograficzna].
 * @param coords Obiekt JsonArray reprezentujący surowe współrzędne pobrane z API.
 * @return Para (Szerokość, Długość) gotowa do użycia w Google Maps.
 */
fun parseLatLng(coords: JsonArray): Pair<Double, Double>? {
    try {
        // Przypadek 1: Tablica zawiera dokładnie dwa elementy liczbowe (np. [-95.91, 47.02])
        if(coords.size() == 2 && coords[0].isJsonPrimitive) 
        {
            // EONET przechowuje współrzędne jako [Longitude, Latitude].
            // Dla Google Maps i standardowej pary zwracamy [Latitude, Longitude], stąd zamiana kolejności: coords[1], coords[0].
            return Pair(coords[1].asDouble, coords[0].asDouble)
        } 
        else
        {
            //Przypadek 2: Tablica jest zagnieżdżona.
            //Schodzimy w dół hierarchii zagnieżdżonych tablic, pobierając zawsze pierwszy element.
            var current = coords
            while (current.size() > 0 && current[0].isJsonArray) 
            {
                current = current[0].asJsonArray
            }
            //Gdy dotrzemy do najgłębszego poziomu, sprawdzamy czy mamy przynajmniej 2 elementy liczbowe
            if (current.size() >= 2 && current[0].isJsonPrimitive) 
            {

                return Pair(current[1].asDouble, current[0].asDouble)
            }
        }
    } catch (e: Exception) 
    {
        e.printStackTrace()
    }
    return null
}

/* 
 * Główna funkcja uruchomieniowa (punkt startowy testu).
 */
fun main() 
{
    //Przykładowy ciąg JSON reprezentujący punkt geograficzny (współrzędne w formacie EONET: [lng, lat])
    val json = """[-95.912189, 47.020535]"""
    
    //Parsowanie ciągu znaków do obiektu tablicy JSON
    val array = JsonParser.parseString(json).asJsonArray
    
    //Wywołanie parsowania
    println("Parsed: " + parseLatLng(array))
}

