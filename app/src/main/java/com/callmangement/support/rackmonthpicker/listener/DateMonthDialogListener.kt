package com.callmangement.support.rackmonthpicker.listener

/**
 * Created by kristiawan on 31/12/16.
 */
fun interface DateMonthDialogListener {
    fun onDateMonth(month: Int, startDate: Int, endDate: Int, year: Int, monthLabel: String?)
}
