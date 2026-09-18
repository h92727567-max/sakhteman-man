package com.buildingmy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.buildingmy.app.data.BuildingViewModel
import com.buildingmy.app.data.Role
import com.buildingmy.app.ui.theme.Cream
import com.buildingmy.app.ui.theme.Danger
import com.buildingmy.app.ui.theme.Teal

@Composable
fun AppRoot(vm: BuildingViewModel) {
    if (vm.session == null) LoginScreen(vm) else MainShell(vm)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(vm: BuildingViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf(Role.MANAGER) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("ساختمان من", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Teal)
        Text("مدیریت شارژ، هزینه و ساکنان — نسخه اندروید", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(name, { name = it }, label = { Text("نام") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            phone,
            { phone = it },
            label = { Text("شماره موبایل") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text("نقش شما", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Role.entries.forEach { r ->
                FilterChip(selected = role == r, onClick = { role = r }, label = { Text(r.title) })
            }
        }
        Spacer(Modifier.height(12.dp))
        if (!sent) {
            Button(
                onClick = {
                    if (phone.length < 10) error = "شماره موبایل را کامل وارد کنید"
                    else {
                        error = null
                        sent = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("ارسال کد تایید") }
        } else {
            OutlinedTextField(
                otp,
                { otp = it },
                label = { Text("کد تایید (۱۲۳۴)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (otp == "1234" || otp.length >= 4) {
                        vm.login(name, phone, role)
                    } else error = "کد تایید نادرست است"
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("ورود به ساختمان") }
        }
        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Danger)
        }
        Spacer(Modifier.height(16.dp))
        Text("برای نسخه آزمایشی کد ۱۲۳۴ را وارد کنید.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "خانه", Icons.Default.Home),
    Tab("charges", "شارژ", Icons.Default.Payments),
    Tab("fix", "تعمیرات", Icons.Default.Build),
    Tab("news", "اعلانات", Icons.Default.Notifications),
    Tab("more", "بیشتر", Icons.Default.MoreHoriz)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShell(vm: BuildingViewModel) {
    val nav = rememberNavController()
    val back by nav.currentBackStackEntryAsState()
    val route = back?.destination?.route ?: "home"
    val title = when (route) {
        "home" -> vm.buildingName
        "charges" -> "شارژ و بدهی"
        "fix" -> "تعمیرات"
        "news" -> "اعلانات و رأی"
        "more" -> "خدمات ساختمان"
        "units" -> "واحدها"
        "expenses" -> "هزینه‌ها"
        "visitors" -> "مراجعین"
        "packages" -> "بسته‌ها"
        "parking" -> "پارکینگ"
        "workers" -> "کارکنان"
        "reports" -> "گزارش‌ها"
        else -> "ساختمان من"
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (route !in tabs.map { it.route }) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                        }
                    } else {
                        Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = route == tab.route || (tab.route == "more" && route in listOf("units", "expenses", "visitors", "packages", "parking", "workers", "reports")),
                        onClick = { nav.navigate(tab.route) { launchSingleTop = true } },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") { HomeScreen(vm) }
            composable("charges") { ChargesScreen(vm) }
            composable("fix") { FixScreen(vm) }
            composable("news") { NewsScreen(vm) }
            composable("more") { MoreScreen(vm, nav) }
            composable("units") { UnitsScreen(vm) }
            composable("expenses") { ExpensesScreen(vm) }
            composable("visitors") { VisitorsScreen(vm) }
            composable("packages") { PackagesScreen(vm) }
            composable("parking") { ParkingScreen(vm) }
            composable("workers") { WorkersScreen(vm) }
            composable("reports") { ReportsScreen(vm) }
        }
    }
}
