package com.bluecodeltd.ecap.chw.util;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DecimalValueFormatter extends ValueFormatter  {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return String.valueOf((int) value);
    }
}
