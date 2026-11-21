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

data class Costing(
    var cftRate: String = "",
    var kolRate: String = "",
    var labourRate: String = "",
    var labourLumpsum: String = "",
    var polishRate: String = "",
    var polishLumpsum: String = "",
    var consumables: String = "",
    var other: String = "",
    var sellingPrice: String = ""
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

    fun costingResult(item: CalcItem, costing: Costing): Triple<Double, Double, Double> {
        val (cft, kol) = itemTotals(item)
        val wood = cft * num(costing.cftRate) + kol * num(costing.kolRate)
        val labour = if (costing.labourLumpsum.isNotBlank()) num(costing.labourLumpsum) else 0.0
        val polish = if (costing.polishLumpsum.isNotBlank()) num(costing.polishLumpsum) else 0.0
        val cost = wood + labour + polish + num(costing.consumables) + num(costing.other)
        val selling = num(costing.sellingPrice)
        val profit = selling - cost
        return Triple(cost, selling, profit)
    }
}
