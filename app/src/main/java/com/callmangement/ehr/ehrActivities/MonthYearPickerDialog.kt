package com.callmangement.ehr.ehrActivities

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.DatePicker

class MonthYearPickerDialog(context: Context, callBack: OnDateSetListener?, year: Int, month: Int) :
    DatePickerDialog(context, callBack, year, month, 1) {
    init {
        this.setTitle(year.toString() + " / " + (month + 1))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.datePicker.findViewById<View>(
                Resources.getSystem().getIdentifier("day", "id", "android")
            ).visibility = View.GONE
        } else {
            // Use reflection to access the private fields in older versions
            try {
                val datePickerDialogFields = javaClass.declaredFields
                for (datePickerDialogField in datePickerDialogFields) {
                    if (datePickerDialogField.name == "mDatePicker") {
                        datePickerDialogField.isAccessible = true
                        val datePicker = datePickerDialogField[this] as DatePicker
                        val datePickerFields = datePickerDialogField.type.declaredFields
                        for (datePickerField in datePickerFields) {
                            if ("mDaySpinner" == datePickerField.name || "mDayPicker" == datePickerField.name) {
                                datePickerField.isAccessible = true
                                val dayPicker = datePickerField[datePicker]
                                (dayPicker as View).visibility = View.GONE
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}