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

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> CftCalculator(items = items, onItemsChange = { items = it })
                1 -> EstimationScreen(items = items, costings = costings, onCostingsChange = { costings = it })
                2 -> QuotationScreen(quoteLines, { quoteLines = it }, discount, { discount = it }, gst, { gst = it })
                3 -> Text("Saved notes / quotations / calculations will be persisted with Room or DataStore.", modifier = Modifier.padding(16.dp))
            }
        }

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).fillMaxWidth()) { Text("Back to Home") }
    }
}

@Composable
fun CftCalculator(items: List<CalcItem>, onItemsChange: (List<CalcItem>) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        itemsIndexed(items) { index, item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(value = item.name, onValueChange = { val n = items.toMutableList(); n[index] = item.copy(name = it); onItemsChange(n) }, label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
                    item.rows.forEachIndexed { rowIdx, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            OutlinedTextField(value = row.l, onValueChange = { updateRow(items, index, rowIdx, row.copy(l = it), onItemsChange) }, label = { Text("L") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.w, onValueChange = { updateRow(items, index, rowIdx, row.copy(w = it), onItemsChange) }, label = { Text("W") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.t, onValueChange = { updateRow(items, index, rowIdx, row.copy(t = it), onItemsChange) }, label = { Text("T") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.q, onValueChange = { updateRow(items, index, rowIdx, row.copy(q = it), onItemsChange) }, label = { Text("Q") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                    }
                    val (cft, kol) = CalcLogic.itemTotals(item)
                    Text("Total: %.3f CFT | %.3f KOL".format(cft, kol), modifier = Modifier.padding(top = 8.dp))
                    Button(onClick = { val n = items.toMutableList(); val rows = item.rows.toMutableList().apply { add(CalcRow()) }; n[index] = item.copy(rows = rows); onItemsChange(n) }) { Text("+ Row") }
                }
            }
        }
        item { Button(onClick = { onItemsChange(items + CalcItem()) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("+ New Item") } }
    }
}

@Composable
fun EstimationScreen(items: List<CalcItem>, costings: List<Costing>, onCostingsChange: (List<Costing>) -> Unit) {
    val adjusted = if (costings.size < items.size) costings + List(items.size - costings.size) { Costing() } else costings
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        itemsIndexed(items) { index, item ->
            val c = adjusted.getOrElse(index) { Costing() }
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(item.name.ifBlank { "Item ${index + 1}" }, style = MaterialTheme.typography.titleMedium)
                    val (cft, kol) = CalcLogic.itemTotals(item)
                    Text("CFT: %.3f | KOL: %.3f".format(cft, kol))
                    OutlinedTextField(value = c.cftRate, onValueChange = { updateCosting(adjusted, index, c.copy(cftRate = it), onCostingsChange) }, label = { Text("CFT Rate") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.kolRate, onValueChange = { updateCosting(adjusted, index, c.copy(kolRate = it), onCostingsChange) }, label = { Text("KOL Rate") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.labourLumpsum, onValueChange = { updateCosting(adjusted, index, c.copy(labourLumpsum = it), onCostingsChange) }, label = { Text("Labour (lumpsum)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.polishLumpsum, onValueChange = { updateCosting(adjusted, index, c.copy(polishLumpsum = it), onCostingsChange) }, label = { Text("Polish (lumpsum)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.consumables, onValueChange = { updateCosting(adjusted, index, c.copy(consumables = it), onCostingsChange) }, label = { Text("Consumables") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.other, onValueChange = { updateCosting(adjusted, index, c.copy(other = it), onCostingsChange) }, label = { Text("Other") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = c.sellingPrice, onValueChange = { updateCosting(adjusted, index, c.copy(sellingPrice = it), onCostingsChange) }, label = { Text("Selling Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    val (cost, selling, profit) = CalcLogic.costingResult(item, c)
                    Text("Cost: ₹%.0f | Selling: ₹%.0f | Profit: ₹%.0f".format(cost, selling, profit), modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
fun QuotationScreen(lines: List<QuoteLine>, onLinesChange: (List<QuoteLine>) -> Unit, discount: String, onDiscountChange: (String) -> Unit, gst: String, onGstChange: (String) -> Unit) {
    val subtotal = lines.sumOf { (it.qty.toDoubleOrNull() ?: 0.0) * (it.unitPrice.toDoubleOrNull() ?: 0.0) }
    val disc = discount.toDoubleOrNull() ?: 0.0
    val taxable = (subtotal - disc).coerceAtLeast(0.0)
    val gstAmount = taxable * (gst.toDoubleOrNull() ?: 0.0) / 100.0
    val grand = taxable + gstAmount

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
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

private fun updateRow(items: List<CalcItem>, itemIdx: Int, rowIdx: Int, newRow: CalcRow, onItemsChange: (List<CalcItem>) -> Unit) {
    val newItems = items.toMutableList()
    val newRows = items[itemIdx].rows.toMutableList()
    newRows[rowIdx] = newRow
    newItems[itemIdx] = items[itemIdx].copy(rows = newRows)
    onItemsChange(newItems)
}

private fun updateCosting(costings: List<Costing>, index: Int, newCosting: Costing, onCostingsChange: (List<Costing>) -> Unit) {
    val newList = costings.toMutableList()
    if (index < newList.size) newList[index] = newCosting else newList.add(newCosting)
    onCostingsChange(newList)
}

private fun updateLine(lines: List<QuoteLine>, index: Int, newLine: QuoteLine, onLinesChange: (List<QuoteLine>) -> Unit) {
    val newList = lines.toMutableList()
    newList[index] = newLine
    onLinesChange(newList)
}
