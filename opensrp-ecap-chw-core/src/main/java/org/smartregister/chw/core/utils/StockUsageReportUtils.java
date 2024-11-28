package org.smartregister.chw.core.utils;

import android.content.Context;

import org.joda.time.LocalDate;
import org.smartregister.chw.core.R;
import org.smartregister.domain.Obs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StockUsageReportUtils {

    public static Map<String, String> getPreviousMonths(Context context) {
        Map<String, String> monthsAndYearsMap = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate prevDate = new LocalDate().minusMonths(i);
            String month = monthConverter(prevDate.getMonthOfYear(), context);
            String year = String.valueOf(prevDate.getYear());
            monthsAndYearsMap.put(month, year);
        }
        return monthsAndYearsMap;
    }

    public static String getMonthNumber(String month) {
        String valMonth = "";
        switch (month) {
            case "Jan":
                valMonth = "01";
                break;
            case "Feb":
                valMonth = "02";
                break;
            case "Mar":
                valMonth = "03";
                break;
            case "Apr":
                valMonth = "04";
                break;
            case "May":
                valMonth = "05";
                break;
            case "Jun":
                valMonth = "06";
                break;
            case "Jul":
                valMonth = "07";
                break;
            case "Aug":
                valMonth = "08";
                break;
            case "Sep":
                valMonth = "09";
                break;
            case "Oct":
                valMonth = "10";
                break;
            case "Nov":
                valMonth = "11";
                break;
            case "Dec":
                valMonth = "12";
                break;
            default:
                break;
        }
        return valMonth;
    }


    public static String getFormattedItem(String item, Context context) {
        String formattedItem = "";
        switch (item) {
            case "ORS 5":
                formattedItem = context.getString(R.string.ors_5);
                break;
            case "Zinc 10":
                formattedItem = context.getString(R.string.zinc_10);
                break;
            case "Panadol":
                formattedItem = context.getString(R.string.paracetamol);
                break;
            case "ALU 6":
                formattedItem = context.getString(R.string.alu_6);
                break;
            case "ALU 12":
                formattedItem = context.getString(R.string.alu_12);
                break;
            case "ALU 18":
                formattedItem = context.getString(R.string.alu_18);
                break;
            case "ALU 24":
                formattedItem = context.getString(R.string.alu_24);
                break;
            case "COC":
                formattedItem = context.getString(R.string.coc);
                break;
            case "POP":
                formattedItem = context.getString(R.string.pop);
                break;
            case "Emergency contraceptive":
                formattedItem = context.getString(R.string.emergency_contraceptive);
                break;
            case "RDTs":
                formattedItem = context.getString(R.string.rdts);
                break;
            case "Male condom":
                formattedItem = context.getString(R.string.male_condoms);
                break;
            case "Female condom":
                formattedItem = context.getString(R.string.female_condoms);
                break;
            case "Standard day method":
                formattedItem = context.getString(R.string.cycle_beads);
                break;
            default:
                break;
        }
        return formattedItem;
    }

    public static String getUnitOfMeasure(String item, Context context) {
        String unitOfMeasure = "";
        switch (item) {
            case "ORS 5":
                unitOfMeasure = context.getString(R.string.packets);
                break;
            case "Zinc 10":
            case "Panadol":
            case "ALU 6":
            case "ALU 12":
            case "ALU 18":
            case "ALU 24":
                unitOfMeasure = context.getString(R.string.tablets);
                break;
            case "COC":
            case "POP":
            case "Emergency contraceptive":
                unitOfMeasure = context.getString(R.string.packs);
                break;
            case "RDTs":
                unitOfMeasure = context.getString(R.string.tests);
                break;
            case "Male condom":
            case "Female condom":
            case "Standard day method":
                unitOfMeasure = context.getString(R.string.pieces);
                break;
            default:
                break;
        }
        return unitOfMeasure;
    }

    public static String monthConverter(Integer value, Context context) {
        String formattedMonth;
        switch (value) {
            case 1:
                formattedMonth = context.getString(R.string.january);
                break;
            case 2:
                formattedMonth = context.getString(R.string.february);
                break;
            case 3:
                formattedMonth = context.getString(R.string.march);
                break;
            case 4:
                formattedMonth = context.getString(R.string.april);
                break;
            case 5:
                formattedMonth = context.getString(R.string.may);
                break;
            case 6:
                formattedMonth = context.getString(R.string.june);
                break;
            case 7:
                formattedMonth = context.getString(R.string.july);
                break;
            case 8:
                formattedMonth = context.getString(R.string.august);
                break;
            case 9:
                formattedMonth = context.getString(R.string.september);
                break;
            case 10:
                formattedMonth = context.getString(R.string.october);
                break;
            case 11:
                formattedMonth = context.getString(R.string.november);
                break;
            case 12:
                formattedMonth = context.getString(R.string.december);
                break;
            default:
                formattedMonth = context.getString(R.string.january);
                break;
        }
        return formattedMonth;
    }

    public static String getObsValue(Obs obs) {
        List<Object> values = obs.getValues();
        if (values.size() > 0) {
            return (String) values.get(0);
        }
        return null;
    }
}
