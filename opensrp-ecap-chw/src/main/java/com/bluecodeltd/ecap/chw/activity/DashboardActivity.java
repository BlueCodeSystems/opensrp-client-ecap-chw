package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.util.DecimalValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.appbar.AppBarLayout;
import com.rey.material.widget.TextView;

import org.json.JSONException;
import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {
    private AppBarLayout myAppbar;
    private Toolbar toolbar;
    private android.widget.TextView allHouseHoldsCount;
    private android.widget.TextView allVcasCount;
    private android.widget.TextView allDueVisits;
    private  android.widget.TextView lastUpdated;
    private  android.widget.TextView facilityName;
    CardView dueCardview;
    private static final int MAX_X_VALUE = 7;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 5;
    private static final String SET_LABEL = "Sub populations";
    private static final String[] DAYS = { "CALHIV", " HEI", " CWLHIV", "AGYW", "CSV", "FSW"};
    private BarChart chart;
    List<Child> allChildren;
    DateTimeFormatter dtf;
    LocalTime localTime;
    ProgressBar loadingDataProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);
        chart = findViewById(R.id.fragment_verticalbarchart_chart);
        allHouseHoldsCount = findViewById(R.id.allHouseholdsNumber);
        allHouseHoldsCount.setText(HouseholdDao.countNumberoFHouseholds());
        allVcasCount = findViewById(R.id.allVcasNumber);
        allDueVisits = findViewById(R.id.due_visits);
        dueCardview = findViewById(R.id.due_card_view);
        allVcasCount.setText(IndexPersonDao.countAllChildren());
        lastUpdated = findViewById(R.id.last_updated);
        facilityName = findViewById(R.id.dash_facility_name);
        loadingDataProgressBar = findViewById(R.id.dash_progressbar);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
        String facility = sp.getString("facility", "anonymous");
         dtf = DateTimeFormatter.ofPattern("HH:mm");


         loadingDataProgressBar.setVisibility(View.VISIBLE);


        facilityName.setText(facility);
        loadData();


    }


    private BarData dataForBarchart(ArrayList<Integer> subpops)
    {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < subpops.size(); i++) {
            float x = i;
            float y = subpops.get(i);
            //new Util .randomFloatBetween(MIN_Y_VALUE, MAX_Y_VALUE);
            values.add(new BarEntry(x, y));
        }
        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());

        return data;
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE) + MIN_Y_VALUE;
                    //new Util .randomFloatBetween(MIN_Y_VALUE, MAX_Y_VALUE);
            values.add(new BarEntry(x, y));
        }
        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);


        BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyValueFormatter());


        return data;
    }


    private void configureChartAppearance() {
        chart.getDescription().setEnabled(false);
        chart.setDrawValueAboveBar(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DAYS[(int) value];
            }
        });

        chart.getXAxis().setDrawGridLines(false);

        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setGranularity(10f);
        axisLeft.setAxisMinimum(0);
        axisLeft.setDrawGridLines(false);

        YAxis axisRight = chart.getAxisRight();
        axisRight.setDrawGridLines(false);
        axisRight.setGranularity(10f);
        axisRight.setAxisMinimum(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(12f);
        chart.setData(data);
        chart.invalidate();
    }

    private int getDueVisits(List<CaregiverVisitationModel> visitDates) {
        int dueVisits = 0;
        DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
        if (visitDates != null) {
            for (int i = 0; i < visitDates.size(); i++) {
                if(visitDates.get(i).getVisit_date() != null)
                {
                    LocalDate localDateBirthdate = LocalDate.parse(visitDates.get(i).getVisit_date(), formatter);
                    LocalDate today = LocalDate.now();
                    Period periodBetweenDateOfVisitAndNow = getPeriodBetweenDateOfVisitAndNow(localDateBirthdate, today);
                    if (periodBetweenDateOfVisitAndNow.getDays() < 1) {
                        dueVisits = dueVisits + 1;
                    }
                }

            }
        }

        return dueVisits;

    }


    public DateTimeFormatter formatDateByPattern(String pattern)
    {
        return DateTimeFormatter.ofPattern(pattern);
    }


    public Period getPeriodBetweenDateOfVisitAndNow(LocalDate localDateBirthdate, LocalDate today){
        return   Period.between(localDateBirthdate, today);
    }

    //Will need to change  to hashmap for cleaner implementation
    public ArrayList<Integer> countSubpop(List<Child> childList)
    {
        ArrayList <Integer> totalSubpops = new ArrayList<>();
        int subpopOne = 0;
        int subpopTwo = 0;
        int subpopThree = 0;
        int subpopFour = 0;
        int subpopFive = 0;
        int subpop = 0;

        if (childList != null)
        {
            for(int i = 0; i < childList.size(); i++){
                if(childList.get(i).getSubpop1() != null && Objects.equals(childList.get(i).getSubpop1(), "true")){
                    subpopOne = subpopOne + 1;
                }

                if(childList.get(i).getSubpop2() != null && Objects.equals(childList.get(i).getSubpop2(), "true")){
                    subpopTwo = subpopTwo + 1;

                }

                if(childList.get(i).getSubpop3() != null && Objects.equals(childList.get(i).getSubpop3(), "true")){
                    subpopThree = subpopThree + 1;
                }


                if(childList.get(i).getSubpop4() != null && Objects.equals(childList.get(i).getSubpop4(), "true")){
                    subpopFour = subpopFour + 1;
                }

                if(childList.get(i).getSubpop5() != null && Objects.equals(childList.get(i).getSubpop5(), "true")){
                    subpopFive = subpopFive + 1;
                }


                if(childList.get(i).getSubpop6() != null && Objects.equals(childList.get(i).getSubpop6(), "true")){
                    subpop = subpop + 1;
                }







            }

            totalSubpops.add(subpopOne); //CALHIV
            totalSubpops.add(subpopTwo); //HEI
            totalSubpops.add(subpopThree); //CWLHIV
            totalSubpops.add(subpopFour); //AGYW
            totalSubpops.add(subpopFive); //CSV
            totalSubpops.add(subpop); //FSW
        }

        return totalSubpops;
    }



    public void loadData()
    {
        localTime = LocalTime.now();
        allChildren = IndexPersonDao.getAllChildrenSubpops();
        int visitsDue = getDueVisits(CaregiverVisitationDao.countAllVisits());

        int subPop = Collections.max(countSubpop(IndexPersonDao.getAllChildrenSubpops()));
        allDueVisits.setText(String.valueOf(visitsDue));


        if(visitsDue > 0 )
        {
            allDueVisits.setTextColor(Color.RED);

        }
        //BarData data = createChartData();
        BarData data = dataForBarchart(countSubpop(IndexPersonDao.getAllChildrenSubpops()));
        configureChartAppearance();
        prepareChartData(data);
        loadingDataProgressBar.setVisibility(View.INVISIBLE);
        lastUpdated.setText(String.valueOf(dtf.format(localTime)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dash_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
               loadData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    //format values on top of the bars rto return whole numbers
    private  class MyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return ""+(int)value;
        }


    }
}