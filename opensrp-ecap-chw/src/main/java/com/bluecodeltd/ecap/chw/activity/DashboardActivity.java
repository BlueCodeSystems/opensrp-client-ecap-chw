package com.bluecodeltd.ecap.chw.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.actionhelper.CSVGeneratorHelper;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.GenerateCSVContract;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.presenter.GenerateCSVPresenter;
import com.bluecodeltd.ecap.chw.util.CsvFormImportService;
import com.github.javiersantos.appupdater.AppUpdater;
import com.bluecodeltd.ecap.chw.util.UpdateManager;
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
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import com.bluecodeltd.ecap.chw.util.Threading;

public class DashboardActivity extends AppCompatActivity  implements GenerateCSVContract.View {
    private com.bluecodeltd.ecap.chw.databinding.ActivityDashboardBinding binding;
    private GenerateCSVContract.Presenter presenter;

    private AppBarLayout myAppbar;
    private CSVGeneratorHelper csvGenerator;
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
    // Displayed subpopulations: AGYW and FSW removed
    private static final String[] SUBPOPS = { "CALHIV", "HEI", "CWLHIV", "C/ASSV"};
    private BarChart chart;
    Handler handler = new Handler();
    List<Child> allChildren;
    DateTimeFormatter dtf;
    LocalTime localTime;
    ProgressBar loadingDataProgressBar;
    Switch  facilityInformationSwitch;
    String phone = "";
    private final int FORTY_FIVE_MINUTES = 3000;
    Runnable runnable;
    ArrayList<Integer> colors;
    AppUpdater appUpdater;
    private DashboardViewModel dashboardViewModel;
    private static final int REQUEST_CODE_IMPORT_CSV = 49011;
    // Background execution centralized via Threading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = binding.collapsingToolbarAppbarlayout;
        NavigationMenu.getInstance(this, null, toolbar);
        chart = binding.fragmentVerticalbarchartChart;
        allHouseHoldsCount = binding.allHouseholdsNumber;
        presenter = new GenerateCSVPresenter(this);

        csvGenerator = new CSVGeneratorHelper();

        // Defer DB work to background; set placeholder until loaded
        allHouseHoldsCount.setText("-");

        allVcasCount = binding.allVcasNumber;
        allDueVisits = binding.dueVisits;
        dueCardview = binding.dueCardView;
        lastUpdated = binding.lastUpdated;
        facilityName = binding.dashFacilityName;
        loadingDataProgressBar = binding.dashProgressbar;
        facilityInformationSwitch = binding.informationSwitch;
        allHouseHoldsCount = binding.allHouseholdsNumber;
        // Card taps: open respective registers
        if (binding.cardView1 != null) {
            binding.cardView1.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, IndexRegisterActivity.class);
                startActivity(intent);
            });
        }
        if (binding.cardView2 != null) {
            binding.cardView2.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, HouseholdIndexActivity.class);
                startActivity(intent);
            });
        }
        // Register row click listeners (Other Registers)
        if (binding.registerRowMother != null) {
            binding.registerRowMother.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MotherIndexActivity.class)));
        }
        if (binding.registerRowHts != null) {
            binding.registerRowHts.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, HivTestingServiceActivity.class)));
        }
        if (binding.registerRowPmtct != null) {
            binding.registerRowPmtct.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PMTCTRegisterActivity.class)));
        }
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String password = extras.getString("password");
        // Last updated format: 01 Jan 2025, 10:30
        dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        colors = new ArrayList<Integer>();

        appUpdater = new AppUpdater(DashboardActivity.this);
        UpdateManager.startOnce(this);

        // ViewModel: observe dashboard state
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getState().observe(this, state -> {
            if (state == null) return;
            try {
                int visits = state.getVisitsDue();
                allDueVisits.setText(String.valueOf(visits));
                // Color code due card: 0 = grey, 1-5 amber, >5 red
                int bgColor;
                int fgColor;
                if (visits == 0) {
                    bgColor = Color.parseColor("#E5E7EB"); // light grey
                    fgColor = Color.parseColor("#374151"); // dark grey text/icon
                } else if (visits > 5) {
                    bgColor = Color.parseColor("#EF4444");
                    fgColor = Color.WHITE;
                } else {
                    bgColor = Color.parseColor("#F1BA0B");
                    fgColor = Color.WHITE;
                }
                if (dueCardview != null) dueCardview.setCardBackgroundColor(bgColor);
                allDueVisits.setTextColor(fgColor);
                try { binding.iconDueVisits.setColorFilter(fgColor, PorterDuff.Mode.SRC_IN); } catch (Exception ignored) {}
                try { binding.dueVisitsView.setTextColor(fgColor); } catch (Exception ignored) {}
                BarData data = dataForBarchart(state.getSubpops());
                configureChartAppearance();
                prepareChartData(data);
                allHouseHoldsCount = binding.allHouseholdsNumber;
                allHouseHoldsCount.setText(state.getHouseholdsCount() != null ? state.getHouseholdsCount() : "0");
                allVcasCount.setText(state.getVcasCount());
                // Gender breakdown in Children card
                binding.cardMaleCount.setText(state.getMaleCount());
                binding.cardFemaleCount.setText(state.getFemaleCount());
                // Gender breakdown in Households card (caregivers)
                binding.cardCaregiverMaleCount.setText(state.getCaregiverMaleCount() != null ? state.getCaregiverMaleCount() : "0");
                binding.cardCaregiverFemaleCount.setText(state.getCaregiverFemaleCount() != null ? state.getCaregiverFemaleCount() : "0");
                // Register counts (Other Registers)
                binding.registerMotherCount.setText(state.getMothersCount());
                binding.registerHtsCount.setText(state.getHtsCount());
                binding.registerPmtctCount.setText(state.getPmtctCount());
                if (state.getLastUpdated() != null) {
                    lastUpdated.setText(dtf.format(state.getLastUpdated()));
                }
                loadingDataProgressBar.setVisibility(View.INVISIBLE);
            } catch (Exception ignored) {}
        });

        // Vibrant palette matching the new card gradients
        colors.clear();
        colors.add(Color.parseColor("#0097A7")); // CALHIV  (teal)
        colors.add(Color.parseColor("#26C6DA")); // HEI     (cyan)
        colors.add(Color.parseColor("#5C6BC0")); // CWLHIV  (indigo)
        colors.add(Color.parseColor("#AB47BC")); // C/ASSV  (purple)
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
        // Kick off initial async load and schedule periodic refreshes
        loadData();
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadData();
//    }

    private BarData dataForBarchart(ArrayList<Integer> subpops)
    {
        ArrayList<BarEntry> values = new ArrayList<>();
        // Only include indices: 0=CALHIV, 1=HEI, 2=CWLHIV, 4=C/ASSV (skip 3=AGYW, 5=FSW)
        int[] include = new int[]{0, 1, 2, 4};
        int xIndex = 0;
        if (subpops == null) subpops = new ArrayList<>();
        for (int idx : include) {
            float y = 0f;
            if (idx >= 0 && idx < subpops.size()) {
                Integer v = subpops.get(idx);
                y = (v != null) ? v : 0f;
            }
            values.add(new BarEntry(xIndex, y));
            xIndex++;
        }
        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
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
                return "";
            }
        });

        chart.getXAxis().setDrawGridLines(false);
        Legend l = chart.getLegend();

        l.getEntries();

        l.setYEntrySpace(10f);

        LegendEntry l1 = new LegendEntry("CALHIV",  Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.parseColor("#0097A7"));
        LegendEntry l2 = new LegendEntry("HEI",     Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.parseColor("#26C6DA"));
        LegendEntry l3 = new LegendEntry("CWLHIV",  Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.parseColor("#5C6BC0"));
        LegendEntry l5 = new LegendEntry("C/ASSV",  Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.parseColor("#AB47BC"));
        l.setCustom(new LegendEntry[]{l1, l2, l3, l5});

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

    // Overload using lightweight date strings
    private int getDueVisitsFromDates(List<String> visitDates) {
        int dueVisits = 0;
        DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
        if (visitDates != null) {
            for (int i = 0; i < visitDates.size(); i++) {
                String d = visitDates.get(i);
                if (d != null && !d.isEmpty()) {
                    try {
                        LocalDate localDate = LocalDate.parse(d, formatter);
                        LocalDate today = LocalDate.now();
                        Period p = getPeriodBetweenDateOfVisitAndNow(localDate, today);
                        if (p.getDays() < 1) dueVisits++;
                    } catch (Exception ignore) {
                        // Skip badly formatted dates
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



    public void loadData() {
        loadingDataProgressBar.setVisibility(View.VISIBLE);
        // Delegate to ViewModel (all-CHW view)
        dashboardViewModel.refresh(null);
        // AppUpdater is started in onCreate
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
                        recreate();

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

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    public void refreshData() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, FORTY_FIVE_MINUTES);
                loadData();
            }
        }, FORTY_FIVE_MINUTES);
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
        loadingDataProgressBar.setVisibility(View.VISIBLE);
        // Delegate to ViewModel (filtered by caseworker phone)
        dashboardViewModel.refresh(phone);
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
            case R.id.generate_pdf:

                csvGenerator.generateCSVWithProgress(this, presenter, () ->
                        showCustomDialog(DashboardActivity.this,
                                getString(R.string.csv_generated_location, getString(R.string.app_name))));
                break;
            case R.id.import_csv:
                openCsvPicker();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCSVGeneratedMessage(String filePath) {

    }

    @Override
    public void showError(String errorMessage) {

    }


    //format values on top of the bars rto return whole numbers
    private  class MyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return ""+(int)value;
        }


    }


    public void showCustomDialog(Context context, String message) {
        showCustomDialog(context, message, null);
    }

    public void showCustomDialog(Context context, String message, Runnable onDismiss) {

        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.custom_dialog);

        dialog.setCancelable(false);
        TextView messageTextView = dialog.findViewById(R.id.dialog_message);
        messageTextView.setText(message);

        Button okButton = dialog.findViewById(R.id.dialog_ok_button);
        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95f);
            int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.8f);
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void openCsvPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/csv", "text/comma-separated-values", "application/vnd.ms-excel"});
        startActivityForResult(intent, REQUEST_CODE_IMPORT_CSV);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_IMPORT_CSV || resultCode != RESULT_OK || data == null) {
            return;
        }

        List<Uri> csvUris = extractCsvUris(data);
        if (csvUris.isEmpty()) {
            showCustomDialog(this, "No CSV file selected.");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setMessage("Preparing import...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Threading.io(() -> {
            final int[] lastPercent = {-1};
            Map<Uri, Integer> perFileRows = new LinkedHashMap<>();
            int totalRowsAllFiles = 0;
            for (Uri uri : csvUris) {
                int rows = CsvFormImportService.getDataRowCount(DashboardActivity.this, uri);
                int normalizedRows = Math.max(1, rows);
                perFileRows.put(uri, normalizedRows);
                totalRowsAllFiles += normalizedRows;
            }
            final int overallTotalRows = totalRowsAllFiles;

            int importedAll = 0;
            int skippedAll = 0;
            int failedAll = 0;
            int processedRowsAll = 0;
            List<String> perFileSummary = new ArrayList<>();

            for (int fileIndex = 0; fileIndex < csvUris.size(); fileIndex++) {
                Uri csvUri = csvUris.get(fileIndex);
                int currentFileIndex = fileIndex + 1;
                int totalFiles = csvUris.size();
                int rowsForCurrentFile = perFileRows.get(csvUri);
                int processedBeforeFile = processedRowsAll;

                CsvFormImportService.ImportSummary summary = CsvFormImportService.importFromCsvUri(
                        DashboardActivity.this,
                        csvUri,
                        (processedRows, totalRows, importedRows, skippedRows, failedRows) -> {
                            int currentFileTotal = totalRows > 0 ? totalRows : rowsForCurrentFile;
                            int globalProcessedRows = Math.min(overallTotalRows, processedBeforeFile + Math.min(processedRows, currentFileTotal));

                            // Keep row-processing phase below 100%; finalization jumps to 100 when all files complete.
                            int percent = overallTotalRows > 0
                                    ? Math.min(95, (globalProcessedRows * 95) / overallTotalRows)
                                    : 95;
                            if (percent == lastPercent[0]) {
                                return;
                            }
                            lastPercent[0] = percent;
                            Threading.main(() -> {
                                progressDialog.setProgress(percent);
                                String status = "Importing file " + currentFileIndex + "/" + totalFiles + ": " + percent + "%"
                                        + "\nRows " + globalProcessedRows + "/" + overallTotalRows
                                        + "\nImported " + importedRows + ", Skipped " + skippedRows + ", Failed " + failedRows;
                                progressDialog.setMessage(status);
                            });
                        }
                );

                importedAll += summary.importedRows;
                skippedAll += summary.skippedRows;
                failedAll += summary.failedRows;
                processedRowsAll += rowsForCurrentFile;

                String fileLabel = summary.fileName != null ? summary.fileName : ("File " + currentFileIndex);
                String status;
                if (summary.timedOutDuringProcessing) {
                    status = "TIMEOUT";
                } else if (summary.hasFileFailure()) {
                    status = "FAILED";
                } else {
                    status = "OK";
                }
                perFileSummary.add(fileLabel + " [" + status + "]: Imported " + summary.importedRows
                        + ", Skipped " + summary.skippedRows
                        + ", Failed " + summary.failedRows);
            }

            StringBuilder finalMessage = new StringBuilder();
            finalMessage.append("CSV import finished for ").append(csvUris.size()).append(" file(s).")
                    .append("\nImported: ").append(importedAll)
                    .append(", Skipped: ").append(skippedAll)
                    .append(", Failed: ").append(failedAll);

            if (!perFileSummary.isEmpty()) {
                finalMessage.append("\n\nPer-file summary:");
                for (String line : perFileSummary) {
                    finalMessage.append("\n- ").append(line);
                }
            }

            String finalSummaryText = finalMessage.toString();
            Threading.main(() -> {
                progressDialog.setProgress(100);
                progressDialog.setMessage("Finalizing: 100%");
                progressDialog.dismiss();
                showCustomDialog(DashboardActivity.this, finalSummaryText, this::loadData);
            });
        });
    }

    private List<Uri> extractCsvUris(Intent data) {
        List<Uri> uris = new ArrayList<>();
        Uri singleUri = data.getData();
        if (singleUri != null) {
            uris.add(singleUri);
        }

        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                if (item == null) {
                    continue;
                }
                Uri uri = item.getUri();
                if (uri != null && !uris.contains(uri)) {
                    uris.add(uri);
                }
            }
        }
        return uris;
    }



}
