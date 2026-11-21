package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moraswood.mwidashboard.data.*
import androidx.compose.foundation.text.KeyboardOptions

data class QuoteLine(var description: String = "", var qty: String = "1", var unitPrice: String = "")

@Composable
fun QuotationEstimationScreen(navController: NavController) {
    var items by remember { mutableStateOf(listOf(CalcItem())) }
    var costings by remember { mutableStateOf(listOf(Costing())) }
    var quoteLines by remember { mutableStateOf(listOf(QuoteLine())) }
    var discount by remember { mutableStateOf("") }
    var gst by remember { mutableStateOf("18") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CFT") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Estimation") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Quotation") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Saved") })
        }

        when (selectedTab) {
            0 -> CftCalculator(items = items, onItemsChange = { items = it })
            1 -> EstimationScreen(items = items, costings = costings, onCostingsChange = { costings = it })
            2 -> QuotationScreen(quoteLines, { quoteLines = it }, discount, { discount = it }, gst, { gst = it })
            3 -> Text("Saved notes / quotations / calculations (use Room or DataStore later)", modifier = Modifier.padding(16.dp))
        }

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).fillMaxWidth()) { Text("Back to Home") }
    }
}

@Composable
fun QuotationScreen(
    lines: List<QuoteLine>,
    onLinesChange: (List<QuoteLine>) -> Unit,
    discount: String,
    onDiscountChange: (String) -> Unit,
    gst: String,
    onGstChange: (String) -> Unit
) {
    val subtotal = lines.sumOf { (it.qty.toDoubleOrNull() ?: 0.0) * (it.unitPrice.toDoubleOrNull() ?: 0.0) }
    val disc = discount.toDoubleOrNull() ?: 0.0
    val taxable = (subtotal - disc).coerceAtLeast(0.0)
    val gstAmount = taxable * (gst.toDoubleOrNull() ?: 0.0) / 100.0
    val grand = taxable + gstAmount

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        itemsIndexed(lines) { index, line ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(8.dp)) {
                    OutlinedTextField(value = line.description, onValueChange = { updateLine(lines, index, line.copy(description = it), onLinesChange) }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = line.qty, onValueChange = { updateLine(lines, index, line.copy(qty = it), onLinesChange) }, label = { Text("Qty") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        OutlinedTextField(value = line.unitPrice, onValueChange = { updateLine(lines, index, line.copy(unitPrice = it), onLinesChange) }, label = { Text("Unit Price") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    }
                }
            }
        }
        item {
            Button(onClick = { onLinesChange(lines + QuoteLine()) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("+ Line") }
            OutlinedTextField(value = discount, onValueChange = onDiscountChange, label = { Text("Discount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = gst, onValueChange = onGstChange, label = { Text("GST %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            Text("Subtotal: ₹%.0f".format(subtotal))
            Text("Taxable: ₹%.0f".format(taxable))
            Text("GST: ₹%.0f".format(gstAmount))
            Text("Grand Total: ₹%.0f".format(grand), style = MaterialTheme.typography.titleLarge)
        }
    }
}

private fun updateLine(lines: List<QuoteLine>, index: Int, newLine: QuoteLine, onLinesChange: (List<QuoteLine>) -> Unit) {
    val newList = lines.toMutableList()
    newList[index] = newLine
    onLinesChange(newList)
}

// ... (CftCalculator and EstimationScreen remain the same as before)
