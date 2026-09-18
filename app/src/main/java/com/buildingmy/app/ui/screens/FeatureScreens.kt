package com.buildingmy.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.buildingmy.app.data.BuildingViewModel
import com.buildingmy.app.data.Money
import com.buildingmy.app.ui.theme.Danger
import com.buildingmy.app.ui.theme.Gold
import com.buildingmy.app.ui.theme.Ok
import com.buildingmy.app.ui.theme.Teal

@Composable
internal fun HomeScreen(vm: BuildingViewModel) {
    val s = vm.session ?: return
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Teal)) {
                Column(Modifier.padding(16.dp)) {
                    Text("سلام ${s.name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("${s.role.title} · ${vm.buildingName}", color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(8.dp))
                    Text("کد دعوت: ${vm.inviteCode}", color = Gold, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("صندوق", Money.toman(vm.fundBalance), Modifier.weight(1f))
                StatCard("بدهی‌ها", Money.toman(vm.unpaidTotal), Modifier.weight(1f), danger = true)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("واحدها", Money.faDigits(vm.units.size.toString()), Modifier.weight(1f))
                StatCard("درخواست باز", Money.faDigits(vm.tickets.count { it.status != "انجام‌شده" }.toString()), Modifier.weight(1f))
            }
        }
        item { Text("بدهکاران", fontWeight = FontWeight.Bold) }
        items(vm.charges.filter { it.paid < it.amount }) { c ->
            InfoCard("واحد ${c.unitNumber}", "مانده ${Money.toman(c.amount - c.paid)}")
        }
        item { Text("آخرین اعلانات", fontWeight = FontWeight.Bold) }
        items(vm.notices.take(2)) { n -> InfoCard(n.title, n.body) }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ChargesScreen(vm: BuildingViewModel) {
    val methods = listOf("equal" to "مساوی", "area" to "متراژ", "mixed" to "ترکیبی", "custom" to "سفارشی")
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("روش محاسبه", fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                methods.forEach { (k, label) ->
                    FilterChip(selected = vm.chargeMethod == k, onClick = { vm.setMethod(k) }, label = { Text(label) })
                }
            }
            Text("جمع دوره: ${Money.toman(vm.periodTotal)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(vm.charges) { c ->
            Card {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("واحد ${c.unitNumber}", fontWeight = FontWeight.Bold)
                        Text(c.period, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(Money.toman(c.amount), color = Teal)
                    }
                    if (c.paid >= c.amount) {
                        Text("پرداخت‌شده", color = Ok, fontWeight = FontWeight.SemiBold)
                    } else {
                        Button(onClick = { vm.payCharge(c.id) }) { Text("پرداخت") }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FixScreen(vm: BuildingViewModel) {
    var title by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("۱۰۱") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            OutlinedTextField(title, { title = it }, label = { Text("شرح خرابی") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(unit, { unit = it }, label = { Text("واحد") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (title.isNotBlank()) {
                    vm.addTicket(title, unit)
                    title = ""
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("ثبت درخواست") }
        }
        items(vm.tickets) { t ->
            InfoCard(t.title, "واحد ${t.unit} · ${t.status}\nپیشنهاد هوشمند: ${t.hint}")
        }
    }
}

@Composable
internal fun NewsScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("اعلانات", fontWeight = FontWeight.Bold) }
        items(vm.notices) { n -> InfoCard(n.title, "${n.body}\n${n.date}") }
        item { Text("رأی‌گیری", fontWeight = FontWeight.Bold) }
        items(vm.polls) { p ->
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text(p.question, fontWeight = FontWeight.Bold)
                    p.options.forEachIndexed { i, opt ->
                        TextButton(onClick = { vm.vote(p.id, i) }) {
                            Text("$opt  ·  ${Money.faDigits(p.votes[i].toString())} رای")
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MoreScreen(vm: BuildingViewModel, nav: NavHostController) {
    val links = listOf(
        "units" to "واحدها و متراژ",
        "expenses" to "هزینه‌ها و صندوق",
        "visitors" to "دفتر مراجعین",
        "packages" to "بسته‌ها",
        "parking" to "پارکینگ",
        "workers" to "کارکنان",
        "reports" to "گزارش مالی"
    )
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(links) { (r, label) ->
            Card(modifier = Modifier.fillMaxWidth().clickable { nav.navigate(r) }) {
                Text(label, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text("خروج از حساب")
            }
        }
    }
}

@Composable
internal fun UnitsScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.units) { u ->
            InfoCard(
                "واحد ${u.number} · طبقه ${Money.faDigits(u.floor.toString())}",
                "متراژ ${Money.faDigits(u.area.toString())} متر · مالک ${u.owner}" +
                    (u.tenant?.let { "\nمستأجر: $it" } ?: "")
            )
        }
    }
}

@Composable
internal fun ExpensesScreen(vm: BuildingViewModel) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("موجودی صندوق: ${Money.toman(vm.fundBalance)}", fontWeight = FontWeight.Bold, color = Teal)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(title, { title = it }, label = { Text("شرح هزینه") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(amount, { amount = it }, label = { Text("مبلغ (تومان)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val n = amount.filter { it.isDigit() }.toLongOrNull()
                if (!title.isBlank() && n != null) {
                    vm.addExpense(title, n)
                    title = ""
                    amount = ""
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("ثبت هزینه") }
        }
        items(vm.expenses) { e -> InfoCard(e.title, "${e.category} · ${e.date}\n${Money.toman(e.amount)}") }
    }
}

@Composable
internal fun VisitorsScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.visitors) { v -> InfoCard(v.name, "واحد ${v.unit} · ${v.time} · ${v.status}") }
    }
}

@Composable
internal fun PackagesScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.packages) { p -> InfoCard(p.from, "واحد ${p.unit} · ${p.status}") }
    }
}

@Composable
internal fun ParkingScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.parking) { p -> InfoCard("جایگاه ${p.slot}", "پلاک ${p.plate} · ${p.unit}") }
    }
}

@Composable
internal fun WorkersScreen(vm: BuildingViewModel) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.workers) { w -> InfoCard(w.name, "${w.job}\n${w.phone}") }
    }
}

@Composable
internal fun ReportsScreen(vm: BuildingViewModel) {
    val paid = vm.charges.sumOf { it.paid }
    val due = vm.charges.sumOf { it.amount }
    val exp = vm.expenses.sumOf { it.amount }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { StatCard("شارژ صادرشده", Money.toman(due), Modifier.fillMaxWidth()) }
        item { StatCard("وصول‌شده", Money.toman(paid), Modifier.fillMaxWidth()) }
        item { StatCard("هزینه‌ها", Money.toman(exp), Modifier.fillMaxWidth(), danger = true) }
        item { StatCard("صندوق", Money.toman(vm.fundBalance), Modifier.fillMaxWidth()) }
        item { Text("این گزارش روی دستگاه محاسبه شده و برای نسخه آزمایشی است.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
internal fun StatCard(label: String, value: String, modifier: Modifier = Modifier, danger: Boolean = false) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, color = if (danger) Danger else Teal, fontSize = 15.sp)
        }
    }
}

@Composable
internal fun InfoCard(title: String, body: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}
