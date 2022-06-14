package com.ubb.citizen_u.ui.util

import com.github.mikephil.charting.formatter.ValueFormatter

class AbsoluteValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val formattedValue = "%,f".format(value).trimEnd('0').trimEnd('.')
        return "$formattedValue lei"
    }
}