package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String[] SUBPOPS = { "CALHIV", " HEI", " CWLHIV", "AGYW", "C/ASSV", "FSW"};
    private BarChart chart;
    Handler handler = new Handler();
    List<Child> allChildren;
    DateTimeFormatter dtf;
    LocalTime localTime;
    ProgressBar loadingDataProgressBar;
    Switch  facilityInformationSwitch;
    String phone = "";
    private final int FIVE_SECONDS = 2000;
    Runnable runnable;
    ArrayList<Integer> colors;
    AppUpdater appUpdater;

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
        lastUpdated = findViewById(R.id.last_updated);
        facilityName = findViewById(R.id.dash_facility_name);
        loadingDataProgressBar = findViewById(R.id.dash_progressbar);
        facilityInformationSwitch = findViewById(R.id.information_switch);
        allHouseHoldsCount = findViewById(R.id.allHouseholdsNumber);
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String password = extras.getString("password");
        dtf = DateTimeFormatter.ofPattern("HH:mm");
        colors = new ArrayList<Integer>();

        appUpdater = new AppUpdater(DashboardActivity.this);
        appUpdater.start();

        colors.add(Color.parseColor("#9B51E0"));
        colors.add(Color.parseColor("#E84AE0"));
        colors.add(Color.parseColor("#BF51E0"));
        colors.add(Color.parseColor("#D338A0"));
        colors.add(Color.parseColor("#DA617E"));
        colors.add(Color.parseColor("#FBA1B7")  );
        if (username != null && password != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
            String code = sp.getString("code", "0000");

            if (!sp.contains("code") || code.equals("0000")) {

                getToken(username, password);

            }

        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
        String facility = sp.getString("facility", "anonymous");
        phone = sp.getString("phone", "anonymous");



        loadingDataProgressBar.setVisibility(View.VISIBLE);
        allHouseHoldsCount.setText(HouseholdDao.countNumberoFHouseholds());
       // loadData();
        refreshData();
        facilityName.setText(facility);
        facilityInformationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (facilityInformationSwitch.isChecked()){
                   loadingDataProgressBar.setVisibility(View.VISIBLE);
                    loadCaseworkerData();
                    handler.removeCallbacks(runnable);
                    loadingDataProgressBar.setVisibility(View.INVISIBLE);
                } else{
                    refreshData();
                    loadData();

                }
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
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
        set1.setColors(colors);
        set1.setColors(colors);
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
        set1.setColors(ColorTemplate.MATERIAL_COLORS);
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
                //return SUBPOPS[(int) value];
                return "";
            }
        });

        chart.getXAxis().setDrawGridLines(false);
        Legend l = chart.getLegend();

        l.getEntries();

       // l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        l.setYEntrySpace(10f);
        LegendEntry l1=new LegendEntry("CALHIV",Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#9B51E0"));
        LegendEntry l2=new LegendEntry("HEI", Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#E84AE0"));
        LegendEntry l3=new LegendEntry("CWLHIV",Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#BF51E0"));
        LegendEntry l4=new LegendEntry("AGYW", Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#D338A0"));
        LegendEntry l5=new LegendEntry("C/ASSV",Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#DA617E"));
        LegendEntry l6=new LegendEntry("FSW", Legend.LegendForm.CIRCLE,10f,2f,null,Color.parseColor("#FBA1B7"));
        l.setCustom(new LegendEntry[]{l1,l2,l3,l4,l5,l6});
       // l.setWordWrapEnabled(true);

       // LegendEntry l1=new LegendEntry("Male",Legend.LegendForm.CIRCLE,10f,2f,null,Color.YELLOW);
       // LegendEntry l2=new LegendEntry("Female", Legend.LegendForm.CIRCLE,10f,2f,null,Color.RED);

      //  l.setCustom(new LegendEntry[]{l1,l2});

        l.setEnabled(true);

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
       // allChildren = IndexPersonDao.getAllChildrenSubpops();
        int visitsDue = getDueVisits(CaregiverVisitationDao.countAllVisits());

       // int subPop = Collections.max(countSubpop(IndexPersonDao.getAllChildrenSubpops()));
        allDueVisits.setText(String.valueOf(visitsDue));


        if(visitsDue > 0 )
        {
            allDueVisits.setTextColor(Color.RED);

        }
        //BarData data = createChartData();
        BarData data = dataForBarchart(countSubpop(IndexPersonDao.getAllChildrenSubpops()));
        configureChartAppearance();
        prepareChartData(data);
        allHouseHoldsCount = findViewById(R.id.allHouseholdsNumber);
        loadingDataProgressBar.setVisibility(View.INVISIBLE);
        allHouseHoldsCount.setText(HouseholdDao.countNumberoFHouseholds());
        allVcasCount.setText(IndexPersonDao.countAllChildren());
        lastUpdated.setText(String.valueOf(dtf.format(localTime)));

        appUpdater.start();

    }

    private void getCreds(String token){

        Log.i("chobela_token ", "chobela_token" + token);

        String tag_string_creds = "req_creds";

        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/userinfo";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                url,
                (Response.Listener<String>) response -> {

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String sub = jObj.getString("sub");
                        String code = jObj.getString("code");
                        String name = jObj.getString("name");
                        String given_name = jObj.getString("given_name");
                        String family_name = jObj.getString("family_name");
                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String facility = jObj.getString("facility");
                        String email = jObj.getString("email");
                        String nrc = jObj.getString("nrc");

                        // save user data
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                        SharedPreferences.Editor edit = sp.edit();


                        edit.putString("sub", sub);
                        edit.putString("code", code);
                        edit.putString("caseworker_name", name);
                        edit.putString("given_name", given_name);
                        edit.putString("family_name", family_name);
                        edit.putString("province", province);
                        edit.putString("partner", partner);
                        edit.putString("phone", phone);
                        edit.putString("district", district);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();
                        finish();
                        startActivity(getIntent());

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }};


        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_creds);

    }

    public void refreshData() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, FIVE_SECONDS);
                loadData();
            }
        }, FIVE_SECONDS);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    private void getToken (final String username, final String password) {

        String tag_string_req = "req_login";

        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/token";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {


                        String jsonInString = new Gson().toJson(response.toString().trim());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString().trim());

                            String token  = jsonObject.getString("access_token");

                            getCreds(token);
                            loadData();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                error -> {

                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("grant_type","password");
                params.put("username",username);
                params.put("password",password);
                params.put("scope","openid");
                params.put("client_id", BuildConfig.OAUTH_CLIENT_ID);
                params.put("client_secret",BuildConfig.OAUTH_CLIENT_SECRET);
                return params;
            }};

        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    public void loadCaseworkerData(){
        localTime = LocalTime.now();
        // allChildren = IndexPersonDao.getAllChildrenSubpops();
        int visitsDue = getDueVisits(CaregiverVisitationDao.countAllVisits());

        // int subPop = Collections.max(countSubpop(IndexPersonDao.getAllChildrenSubpops()));
        allDueVisits.setText(String.valueOf(visitsDue));


        if(visitsDue > 0 )
        {
            allDueVisits.setTextColor(Color.RED);

        }
        //BarData data = createChartData();
        BarData data = dataForBarchart(countSubpop(IndexPersonDao.getAllChildrenSubpopsByCaseworkerPhoneNumber(phone)));
        configureChartAppearance();
        prepareChartData(data);
        allHouseHoldsCount.setText(HouseholdDao.countNumberOfHouseholdsByCaseworkerPhone(phone));
        allVcasCount.setText(IndexPersonDao.countAllChildrenByCaseworkerPhoneNumber(phone));
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