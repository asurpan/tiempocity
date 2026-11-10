package com.example.tiempocity.data.model

import androidx.annotation.Keep

@Keep
data class City(
    val name: String,
    val province: String,
    val latitude: Double,
    val longitude: Double,
    val postalCode: String = "",
    val neighborhood: String = "",
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = if (neighborhood.isNotBlank()) "$name ($neighborhood)" else if (postalCode.isNotBlank()) "$name (C.P. $postalCode)" else name

    companion object {
        val SEVILLA = City("Sevilla", "Andalucía", 37.3891, -5.9845, "41001", "Centro / Casco Antiguo", true)
        val LA_ALGABA = City("La Algaba", "Sevilla", 37.4622, -6.0125, "41980")

        val SEVILLA_MUNICIPALITIES = listOf(
            SEVILLA,
            LA_ALGABA,
            City("Sevilla", "Andalucía", 37.3820, -5.9750, "41005", "Nervión / Buhaira"),
            City("Sevilla", "Andalucía", 37.3850, -6.0020, "41010", "Triana / Los Remedios"),
            City("Sevilla", "Andalucía", 37.4080, -5.9860, "41009", "Macarena / San Gil"),
            City("Sevilla", "Andalucía", 37.3610, -5.9810, "41013", "Sur / Bellavista / Heliópolis"),
            City("Sevilla", "Andalucía", 37.4110, -5.9980, "41092", "Isla de la Cartuja"),
            City("Sevilla", "Andalucía", 37.3830, -5.9420, "41020", "Este / Sevilla Este"),
            City("La Rinconada", "Sevilla", 37.4870, -5.9490, "41309"),
            City("Camas", "Sevilla", 37.4020, -6.0330, "41900"),
            City("San Juan de Aznalfarache", "Sevilla", 37.3610, -6.0260, "41920"),
            City("Tomares", "Sevilla - Aljarafe", 37.3760, -6.0450, "41920"),
            City("Mairena del Aljarafe", "Sevilla - Aljarafe", 37.3440, -6.0650, "41927"),
            City("Bormujos", "Sevilla - Aljarafe", 37.3730, -6.0710, "41930"),
            City("Gines", "Sevilla - Aljarafe", 37.3870, -6.0780, "41960"),
            City("Castilleja de la Cuesta", "Sevilla", 37.3870, -6.0530, "41950"),
            City("Santiponce", "Sevilla", 37.4370, -6.0410, "41970"),
            City("Dos Hermanas", "Sevilla", 37.2830, -5.9230, "41701"),
            City("Alcalá de Guadaíra", "Sevilla", 37.3330, -5.8500, "41500"),
            City("Coria del Río", "Sevilla", 37.2870, -6.0540, "41100"),
            City("Utrera", "Sevilla", 37.1800, -5.7800, "41710"),
            City("Écija", "Sevilla", 37.5410, -5.0800, "41400"),
            City("Carmona", "Sevilla", 37.4710, -5.6420, "41410"),
            City("Morón de la Frontera", "Sevilla", 37.1210, -5.4540, "41530"),
            City("Lebrija", "Sevilla", 36.9200, -6.0770, "41740"),
            City("Los Palacios y Villafranca", "Sevilla", 37.1610, -5.9240, "41720"),
            City("Osuna", "Sevilla", 37.2370, -5.1030, "41640"),
            City("Lora del Río", "Sevilla", 37.6570, -5.5340, "41440")
        )

        val SPANISH_CITIES = SEVILLA_MUNICIPALITIES + listOf(
            City("Madrid", "Comunidad de Madrid", 40.4168, -3.7038, "28001", "Centro"),
            City("Barcelona", "Cataluña", 41.3851, 2.1734, "08001", "Eixample"),
            City("Valencia", "Comunidad Valenciana", 39.4699, -0.3763, "46001"),
            City("Málaga", "Andalucía", 36.7213, -4.4214, "29001"),
            City("Bilbao", "País Vasco", 43.2630, -2.9350, "48001"),
            City("Zaragoza", "Aragón", 41.6488, -0.8891, "50001"),
            City("Granada", "Andalucía", 37.1773, -3.5986, "18001"),
            City("Córdoba", "Andalucía", 37.8882, -4.7794, "14001"),
            City("Alicante", "Comunidad Valenciana", 38.3452, -0.4810, "03001"),
            City("Vigo", "Galicia", 42.2406, -8.7207, "36201"),
            City("A Coruña", "Galicia", 43.3623, -8.4115, "15001"),
            City("Palma de Mallorca", "Islas Baleares", 39.5696, 2.6502, "07001"),
            City("Las Palmas de Gran Canaria", "Canarias", 28.1235, -15.4363, "35001"),
            City("Santa Cruz de Tenerife", "Canarias", 28.4636, -16.2518, "38001"),
            City("Valladolid", "Castilla y León", 41.6523, -4.7245, "47001"),
            City("Murcia", "Región de Murcia", 37.9922, -1.1307, "30001"),
            City("Oviedo", "Asturias", 43.3614, -5.8593, "33001"),
            City("San Sebastián", "País Vasco", 43.3183, -1.9812, "20001"),
            City("Santander", "Cantabria", 43.4623, -3.8099, "39001"),
            City("Pamplona", "Navarra", 42.8125, -1.6458, "31001"),
            City("Cádiz", "Andalucía", 36.5271, -6.2886, "11001"),
            City("Huelva", "Andalucía", 37.2614, -6.9447, "21001"),
            City("Jaén", "Andalucía", 37.7796, -3.7849, "23001"),
            City("Almería", "Andalucía", 36.8340, -2.4637, "04001")
        )
    }
}
