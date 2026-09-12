package com.buildingmy.app.data

enum class Role(val title: String) {
    MANAGER("مدیر ساختمان"),
    OWNER("مالک"),
    TENANT("مستأجر"),
    GUARD("نگهبان")
}

data class UnitItem(
    val id: String,
    val number: String,
    val floor: Int,
    val area: Int,
    val owner: String,
    val tenant: String?,
    val customAmount: Long = 0L
)

data class ChargeItem(
    val id: String,
    val unitNumber: String,
    val period: String,
    val amount: Long,
    val paid: Long,
    val method: String
)

data class ExpenseItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val date: String
)

data class TicketItem(
    val id: String,
    val title: String,
    val unit: String,
    val status: String,
    val hint: String
)

data class NoticeItem(
    val id: String,
    val title: String,
    val body: String,
    val date: String
)

data class PollItem(
    val id: String,
    val question: String,
    val options: List<String>,
    val votes: List<Int>
)

data class VisitorItem(
    val id: String,
    val name: String,
    val unit: String,
    val time: String,
    val status: String
)

data class PackageItem(
    val id: String,
    val from: String,
    val unit: String,
    val status: String
)

data class ParkingItem(
    val id: String,
    val slot: String,
    val plate: String,
    val unit: String
)

data class WorkerItem(
    val id: String,
    val name: String,
    val job: String,
    val phone: String
)

data class Session(
    val name: String,
    val phone: String,
    val role: Role
)

object Money {
    private val fa = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun faDigits(raw: String): String = buildString {
        raw.forEach { ch ->
            append(if (ch in '0'..'9') fa[ch - '0'] else ch)
        }
    }

    fun toman(amount: Long): String {
        val grouped = "%,d".format(amount).replace(',', '٬')
        return faDigits(grouped) + " تومان"
    }
}

object ChargeMath {
    fun amount(
        method: String,
        total: Long,
        units: List<UnitItem>,
        unit: UnitItem,
        equalPct: Int = 50,
        areaPct: Int = 50
    ): Long {
        if (units.isEmpty()) return 0L
        return when (method) {
            "equal" -> total / units.size
            "area" -> {
                val sum = units.sumOf { it.area }.coerceAtLeast(1)
                total * unit.area / sum
            }
            "mixed" -> {
                val eq = total * equalPct / 100 / units.size
                val sum = units.sumOf { it.area }.coerceAtLeast(1)
                val ar = total * areaPct / 100 * unit.area / sum
                eq + ar
            }
            "custom" -> unit.customAmount
            else -> total / units.size
        }
    }
}
