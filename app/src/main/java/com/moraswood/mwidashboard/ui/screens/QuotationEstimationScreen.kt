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
import com.moraswood.mwidashboard.data.CalcItem
import com.moraswood.mwidashboard.data.CalcLogic
import com.moraswood.mwidashboard.data.CalcRow
import com.moraswood.mwidashboard.data.Costing
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun QuotationEstimationScreen(navController: NavController) {
    var items by remember { mutableStateOf(listOf(CalcItem())) }
    var costings by remember { mutableStateOf(listOf(Costing())) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CFT Calculator") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Estimation") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Quotation") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Saved") })
        }

        when (selectedTab) {
            0 -> CftCalculator(items = items, onItemsChange = { items = it })
            1 -> EstimationScreen(items = items, costings = costings, onCostingsChange = { costings = it })
            2 -> Text("Quotation coming soon", modifier = Modifier.padding(16.dp))
            3 -> Text("Saved notes / quotations / calculations coming soon", modifier = Modifier.padding(16.dp))
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) { Text("Back to Home") }
    }
}

@Composable
fun CftCalculator(items: List<CalcItem>, onItemsChange: (List<CalcItem>) -> Unit) {
    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        itemsIndexed(items) { index, item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = {
                            val newItems = items.toMutableList()
                            newItems[index] = item.copy(name = it)
                            onItemsChange(newItems)
                        },
                        label = { Text("Item name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    item.rows.forEachIndexed { rowIdx, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            OutlinedTextField(value = row.l, onValueChange = { updateRow(items, index, rowIdx, row.copy(l = it), onItemsChange) }, label = { Text("L") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.w, onValueChange = { updateRow(items, index, rowIdx, row.copy(w = it), onItemsChange) }, label = { Text("W") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.t, onValueChange = { updateRow(items, index, rowIdx, row.copy(t = it), onItemsChange) }, label = { Text("T") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            OutlinedTextField(value = row.q, onValueChange = { updateRow(items, index, rowIdx, row.copy(q = it), onItemsChange) }, label = { Text("Q") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                    }
                    val (cft, kol) = CalcLogic.itemTotals(item)
                    Text("Total: %.3f CFT | %.3f KOL".format(cft, kol), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                    Button(onClick = {
                        val newItems = items.toMutableList()
                        val newRows = item.rows.toMutableList().apply { add(CalcRow()) }
                        newItems[index] = item.copy(rows = newRows)
                        onItemsChange(newItems)
                    }) { Text("+ Row") }
                }
            }
        }
        item {
            Button(onClick = { onItemsChange(items + CalcItem()) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("+ New Item")
            }
        }
    }
}

@Composable
fun EstimationScreen(items: List<CalcItem>, costings: List<Costing>, onCostingsChange: (List<Costing>) -> Unit) {
    // Ensure costings list matches items
    val adjustedCostings = if (costings.size < items.size) costings + List(items.size - costings.size) { Costing() } else costings

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        itemsIndexed(items) { index, item ->
            val costing = adjustedCostings.getOrElse(index) { Costing() }
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(item.name.ifBlank { "Item ${index + 1}" }, style = MaterialTheme.typography.titleMedium)
                    val (cft, kol) = CalcLogic.itemTotals(item)
                    Text("CFT: %.3f | KOL: %.3f".format(cft, kol))

                    OutlinedTextField(value = costing.cftRate, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(cftRate = it), onCostingsChange) }, label = { Text("CFT Rate") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.kolRate, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(kolRate = it), onCostingsChange) }, label = { Text("KOL Rate") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.labourLumpsum, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(labourLumpsum = it), onCostingsChange) }, label = { Text("Labour (lumpsum)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.polishLumpsum, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(polishLumpsum = it), onCostingsChange) }, label = { Text("Polish (lumpsum)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.consumables, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(consumables = it), onCostingsChange) }, label = { Text("Consumables") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.other, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(other = it), onCostingsChange) }, label = { Text("Other") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = costing.sellingPrice, onValueChange = { updateCosting(adjustedCostings, index, costing.copy(sellingPrice = it), onCostingsChange) }, label = { Text("Selling Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

                    val (cost, selling, profit) = CalcLogic.costingResult(item, costing)
                    Text("Cost: ₹%.0f | Selling: ₹%.0f | Profit: ₹%.0f".format(cost, selling, profit), modifier = Modifier.padding(top = 8.dp))
                }
            }
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
