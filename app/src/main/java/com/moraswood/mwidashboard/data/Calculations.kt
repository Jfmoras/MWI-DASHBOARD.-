package com.moraswood.mwidashboard.data

data class CalcRow(
    var l: String = "",
    var lUnit: String = "ft",
    var w: String = "",
    var wUnit: String = "in",
    var t: String = "",
    var tUnit: String = "in",
    var q: String = "",
    var mode: String = "CFT"
)

data class CalcItem(
    var name: String = "",
    var rows: MutableList<CalcRow> = mutableListOf(CalcRow())
)

object CalcLogic {
    private fun num(v: String) = v.toDoubleOrNull() ?: 0.0

    fun toFeet(value: String, unit: String = "ft"): Double {
        val v = num(value)
        return when (unit) {
            "in" -> v / 12.0
            "cm" -> v / 30.48
            "mm" -> v / 304.8
            "m" -> v * 3.280839895
            else -> v
        }
    }

    fun toInches(value: String, unit: String = "in"): Double {
        val v = num(value)
        return when (unit) {
            "ft" -> v * 12.0
            "cm" -> v / 2.54
            "mm" -> v / 25.4
            "m" -> v * 39.37007874
            else -> v
        }
    }

    fun calcRow(r: CalcRow): Pair<String, Double> {
        val lFt = toFeet(r.l, r.lUnit)
        val wIn = toInches(r.w, r.wUnit)
        val tIn = toInches(r.t, r.tUnit)
        val raw = lFt * wIn * tIn * num(r.q)
        val valResult = raw / if (r.mode == "CFT") 144.0 else 37.5
        return r.mode to valResult
    }

    fun itemTotals(item: CalcItem): Pair<Double, Double> {
        var cft = 0.0
        var kol = 0.0
        item.rows.forEach { row ->
            val (mode, value) = calcRow(row)
            if (mode == "CFT") cft += value else kol += value
        }
        return cft to kol
    }
}
