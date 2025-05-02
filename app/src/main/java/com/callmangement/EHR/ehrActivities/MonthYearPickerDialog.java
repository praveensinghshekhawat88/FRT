package com.callmangement.EHR.ehrActivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;

public class MonthYearPickerDialog extends DatePickerDialog {

    public MonthYearPickerDialog(Context context, OnDateSetListener callBack, int year, int month) {
        super(context, callBack, year, month, 1);
        this.setTitle(year + " / " + (month + 1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        } else {
            // Use reflection to access the private fields in older versions
            try {
                java.lang.reflect.Field[] datePickerDialogFields = this.getClass().getDeclaredFields();
                for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                    if (datePickerDialogField.getName().equals("mDatePicker")) {
                        datePickerDialogField.setAccessible(true);
                        DatePicker datePicker = (DatePicker) datePickerDialogField.get(this);
                        java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                        for (java.lang.reflect.Field datePickerField : datePickerFields) {
                            if ("mDaySpinner".equals(datePickerField.getName()) || "mDayPicker".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}