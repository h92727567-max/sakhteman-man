package com.buildingmy.app

import com.buildingmy.app.data.ChargeMath
import com.buildingmy.app.data.UnitItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ChargeMathTest {
    private val units = listOf(
        UnitItem("1", "101", 1, 100, "A", null, 1_000_000),
        UnitItem("2", "102", 1, 100, "B", null, 2_000_000)
    )

    @Test
    fun equalSplitsEvenly() {
        val a = ChargeMath.amount("equal", 10_000_000, units, units[0])
        assertEquals(5_000_000L, a)
    }

    @Test
    fun customUsesUnitAmount() {
        val a = ChargeMath.amount("custom", 10_000_000, units, units[1])
        assertEquals(2_000_000L, a)
    }
}
