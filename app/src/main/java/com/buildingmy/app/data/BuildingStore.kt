package com.buildingmy.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BuildingViewModel : ViewModel() {
    var session by mutableStateOf<Session?>(null)
        private set

    val buildingName = "برج سپهر"
    val inviteCode = "SPHR-۱۴۰۳"
    var chargeMethod by mutableStateOf("mixed")
    var equalPct by mutableStateOf(40)
    var areaPct by mutableStateOf(60)
    var periodTotal by mutableStateOf(12_000_000L)

    val units = listOf(
        UnitItem("1", "۱۰۱", 1, 95, "احمد رضایی", "سارا محمدی", 1_100_000),
        UnitItem("2", "۱۰۲", 1, 80, "نیما کرمی", null, 900_000),
        UnitItem("3", "۲۰۱", 2, 120, "مریم حسینی", "علی نوری", 1_400_000),
        UnitItem("4", "۲۰۲", 2, 110, "رضا اکبری", null, 1_250_000),
        UnitItem("5", "۳۰۱", 3, 140, "الهام صادقی", "کیان پناهی", 1_600_000),
        UnitItem("6", "۳۰۲", 3, 90, "بهرام یوسفی", null, 1_000_000)
    )

    var charges by mutableStateOf(generateCharges())
        private set

    var expenses by mutableStateOf(
        listOf(
            ExpenseItem("e1", "قبض برق مشاع", "قبوض", 2_400_000, "۱۴۰۳/۰۶/۱۲"),
            ExpenseItem("e2", "تعمیر آسانسور", "تعمیرات", 6_800_000, "۱۴۰۳/۰۶/۰۸"),
            ExpenseItem("e3", "نظافت راه‌پله", "خدمات", 1_200_000, "۱۴۰۳/۰۶/۰۱")
        )
    )
        private set

    var tickets by mutableStateOf(
        listOf(
            TicketItem("t1", "نشتی لوله واحد ۲۰۱", "۲۰۱", "باز", "احتمالاً واشر شیر آب کهنه است."),
            TicketItem("t2", "صدای غیرعادی آسانسور", "مشاعات", "در حال انجام", "نیاز به سرویس موتورخانه."),
            TicketItem("t3", "چراغ پارکینگ سوخته", "پارکینگ", "انجام‌شده", "تعویض لامپ LED.")
        )
    )
        private set

    val notices = listOf(
        NoticeItem("n1", "قطع آب روز جمعه", "از ساعت ۹ تا ۱۳ برای تعمیر کنتور اصلی.", "۱۴۰۳/۰۶/۱۸"),
        NoticeItem("n2", "شارژ شهریور صادر شد", "مهلت پرداخت تا پایان ماه.", "۱۴۰۳/۰۶/۰۱"),
        NoticeItem("n3", "جلسه هیئت‌مدیره", "سه‌شنبه ساعت ۱۹ در لابی.", "۱۴۰۳/۰۵/۲۸")
    )

    var polls by mutableStateOf(
        listOf(
            PollItem("p1", "رنگ‌آمیزی راه‌پله انجام شود؟", listOf("بله", "خیر", "سال بعد"), listOf(8, 2, 3))
        )
    )
        private set

    var visitors by mutableStateOf(
        listOf(
            VisitorItem("v1", "پیک اسنپ‌فود", "۱۰۱", "۱۸:۴۲", "داخل ساختمان"),
            VisitorItem("v2", "مهمان واحد ۳۰۱", "۳۰۱", "۱۷:۱۰", "خروج")
        )
    )
        private set

    val packages = listOf(
        PackageItem("k1", "دیجی‌کالا", "۲۰۲", "منتظر تحویل"),
        PackageItem("k2", "پست", "۱۰۲", "تحویل‌شده")
    )

    val parking = listOf(
        ParkingItem("pk1", "A-۱۲", "۱۲ب۳۴۵ ایران ۹۹", "۱۰۱"),
        ParkingItem("pk2", "B-۰۴", "۲۱س۸۱۲ ایران ۱۱", "۲۰۱"),
        ParkingItem("pk3", "مهمان ۱", "—", "آزاد")
    )

    val workers = listOf(
        WorkerItem("w1", "حسن مرادی", "نگهبان شیفت روز", "۰۹۱۲۱۲۳۴۵۶۷"),
        WorkerItem("w2", "اکبر نعمتی", "تاسیسات", "۰۹۳۵۹۸۷۶۵۴۳")
    )

    val fundBalance: Long get() = 18_450_000L
    val unpaidTotal: Long get() = charges.filter { it.paid < it.amount }.sumOf { it.amount - it.paid }

    fun login(name: String, phone: String, role: Role) {
        session = Session(name.ifBlank { "کاربر ساختمان" }, phone, role)
    }

    fun logout() {
        session = null
    }

    fun setMethod(method: String) {
        chargeMethod = method
        charges = generateCharges()
    }

    fun payCharge(id: String) {
        charges = charges.map {
            if (it.id == id) it.copy(paid = it.amount) else it
        }
    }

    fun addExpense(title: String, amount: Long) {
        expenses = listOf(
            ExpenseItem("e${expenses.size + 1}", title, "سایر", amount, "امروز")
        ) + expenses
    }

    fun addTicket(title: String, unit: String) {
        tickets = listOf(
            TicketItem("t${tickets.size + 1}", title, unit, "باز", "بررسی اولیه توسط مدیر.")
        ) + tickets
    }

    fun vote(pollId: String, option: Int) {
        polls = polls.map { p ->
            if (p.id != pollId) p
            else p.copy(votes = p.votes.mapIndexed { i, n -> if (i == option) n + 1 else n })
        }
    }

    private fun generateCharges(): List<ChargeItem> {
        val period = "شهریور ۱۴۰۳"
        return units.map { u ->
            val amt = ChargeMath.amount(chargeMethod, periodTotal, units, u, equalPct, areaPct)
            val paid = if (u.number == "۱۰۲" || u.number == "۳۰۲") 0L else amt
            ChargeItem("c${u.id}", u.number, period, amt, paid, chargeMethod)
        }
    }
}
