package com.pawside.app.data

/**
 * The pet "moods" shown on the widget, each mapped to a live device state:
 *  - [BATTERY_GOOD]  battery is healthy
 *  - [MEMORY_HIGH]   memory pressure is high
 *  - [CHARGING]      the device is charging
 */
enum class DogState(
    val key: String,
    val displayLabel: String,
    val widgetLabel: String,
    val description: String,
) {
    BATTERY_GOOD("battery_good", "元気いっぱい", "バッテリー良好", "バッテリー良好：走り回る"),
    MEMORY_HIGH("memory_high", "ちょっと眠そう", "メモリ高負荷", "メモリ高負荷：座って休む"),
    CHARGING("charging", "しあわせ夢の中", "充電中", "充電中：すやすや眠る"),
    ;

    companion object {
        fun fromKey(key: String): DogState? = entries.firstOrNull { it.key == key }
    }
}
