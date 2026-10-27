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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
import java.util.Locale;
import java.util.Objects;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.PublicGoogleDriveFolderDownloader;
import com.bluecodeltd.ecap.chw.util.Subpopulation;

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
    // The subpopulations charted on the dashboard, top to bottom, in Subpopulation order
    private final List<Subpopulation> dashboardSubpops = new ArrayList<>();
    Handler handler = new Handler();
    List<Child> allChildren;
    DateTimeFormatter dtf;
    LocalTime localTime;
    ProgressBar loadingDataProgressBar;
    Switch  facilityInformationSwitch;
    String phone = "";
    private static final long FORTY_FIVE_MINUTES = 45L * 60L * 1000L;
    Runnable runnable;
    AppUpdater appUpdater;
    private DashboardViewModel dashboardViewModel;
    private static final int REQUEST_CODE_IMPORT_CSV = 49011;
    private static final String ECAP_SOPS_FOLDER_ID = "1ojVjohWcR1S4RlHCHvyiA0jPSofiVQKo";
    private static final String ECAP_SOPS_TARGET_DIR = "ECAP II SOPs";
    private static final String ECAP_SOPS_FOLDER_URL = "https://drive.google.com/drive/folders/" + ECAP_SOPS_FOLDER_ID + "?usp=drive_link";
    // Background execution centralized via Threading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DashboardActivity isn't a SecuredActivity, so nothing else checks the session before
        // this point. If the process was killed and ChwApplication's silent session restore
        // (restoreSessionAfterProcessRestart) couldn't re-derive the DB password -- e.g. no
        // registered user, or the encrypted local credentials couldn't be decrypted -- proceeding
        // would crash the background dashboard queries with "Password has not been set!"
        // (Repository.getReadableDatabase). Bail out to LoginActivity instead.
        if (org.smartregister.Context.getInstance().IsUserLoggedOut()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        binding = com.bluecodeltd.ecap.chw.databinding.ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = binding.collapsingToolbarAppbarlayout;
        NavigationMenu.getInstance(this, null, toolbar);
        // Matches the top of the green header
        getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.login_header_start));
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
        binding.registerRowAppointments.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AppointmentsRegisterActivity.class)));
        binding.registerRowPrep.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PrepRegisterActivity.class)));
        binding.registerRowReferralLinkages.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ReferralLinkagesRegisterActivity.class)));
        Bundle extras = getIntent().getExtras();
        String username = extras != null ? extras.getString("username") : null;
        String password = extras != null ? extras.getString("password") : null;
        // Last updated format: 01 Jan 2025, 10:30
        dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        // Static chart appearance doesn't depend on data, so configure it once. This runs before the
        // ViewModel observer is registered, which can deliver a cached state synchronously.
        configureSubpopChart();

        appUpdater = new AppUpdater(DashboardActivity.this);
        UpdateManager.startOnce(this);

        // ViewModel: observe dashboard state
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getState().observe(this, state -> {
            if (state == null) return;
            try {
                int visits = state.getVisitsDue();
                allDueVisits.setText(String.valueOf(visits));
                bindVisitsDueStatus(visits);
                allHouseHoldsCount = binding.allHouseholdsNumber;
                allHouseHoldsCount.setText(state.getHouseholdsCount() != null ? state.getHouseholdsCount() : "0");
                allVcasCount.setText(state.getVcasCount());
                bindSubpops(state.getSubpops());
                boolean hasChartData = hasChartData(state.getSubpops());
                binding.dashSubpopList.setVisibility(hasChartData ? View.VISIBLE : View.INVISIBLE);
                binding.chartEmptyState.setVisibility(hasChartData ? View.GONE : View.VISIBLE);
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
                binding.registerAppointmentsCount.setText(state.getAppointmentsCount());
                binding.registerPrepCount.setText(state.getPrepCount());
                binding.registerReferralLinkagesCount.setText(state.getReferralLinkagesCount());
                if (state.getLastUpdated() != null) {
                    lastUpdated.setText(dtf.format(state.getLastUpdated()));
                }
            } catch (Exception ignored) {
            } finally {
                setChartLoading(false);
            }
        });

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
        setChartLoading(true);
        // Kick off initial async load and schedule periodic refreshes
        loadData();
        refreshData();
        facilityName.setText(facility);
        facilityInformationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (facilityInformationSwitch.isChecked()){
                    loadCaseworkerData();
                    handler.removeCallbacks(runnable);
                } else{
                    loadData();
                    refreshData();

                }
            }
        });




    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadData();
//    }

    /**
     * Sets up the subpopulation column chart: one column per Subpopulation shown on the dashboard, left
     * to right in declaration order, plus the key under it. Columns narrow as groups are added, and
     * the names tilt once there are too many to sit side by side.
     */
    private void configureSubpopChart() {
        // The key repeats each column's colour as a dot, so colour is never the only label
        android.text.SpannableStringBuilder key = new android.text.SpannableStringBuilder();
        for (Subpopulation subpop : Subpopulation.values()) {
            if (!subpop.onDashboard) continue;
            dashboardSubpops.add(subpop);
            if (subpop.description != 0) {
                if (key.length() > 0) key.append('\n');
                int dotStart = key.length();
                key.append("●  ");
                key.setSpan(new android.text.style.ForegroundColorSpan(
                                androidx.core.content.ContextCompat.getColor(this, subpop.color)),
                        dotStart, dotStart + 1, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int nameStart = key.length();
                key.append(subpop.shortName);
                key.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        nameStart, key.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                key.append(" – ").append(getString(subpop.description));
            }
        }
        binding.dashSubpopKey.setText(key);
        binding.dashSubpopKey.setVisibility(key.length() > 0 ? View.VISIBLE : View.GONE);

        int text = androidx.core.content.ContextCompat.getColor(this, R.color.dash_text);
        int muted = androidx.core.content.ContextCompat.getColor(this, R.color.dash_text_muted);
        int grid = androidx.core.content.ContextCompat.getColor(this, R.color.dash_divider);
        BarChart chart = binding.dashSubpopChart;
        chart.setRenderer(new com.bluecodeltd.ecap.chw.util.RoundedColumnRenderer(
                chart, chart.getAnimator(), chart.getViewPortHandler(), 6f));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setNoDataText("");
        chart.setExtraOffsets(0f, 8f, 0f, 4f);

        int columns = Math.max(1, dashboardSubpops.size());
        XAxis names = chart.getXAxis();
        names.setPosition(XAxis.XAxisPosition.BOTTOM);
        names.setDrawGridLines(false);
        names.setAxisLineColor(grid);
        names.setGranularity(1f);
        names.setLabelCount(columns);
        names.setTextColor(text);
        names.setTextSize(12f);
        names.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        names.setYOffset(8f);
        // Tilt the names once columns get too narrow for them to sit side by side
        if (columns > 6) {
            names.setLabelRotationAngle(-35f);
        }
        names.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = Math.round(value);
                return i >= 0 && i < dashboardSubpops.size() ? dashboardSubpops.get(i).shortName : "";
            }
        });

        YAxis counts = chart.getAxisLeft();
        counts.setAxisMinimum(0f);
        counts.setGranularity(1f);
        counts.setLabelCount(5, false);
        counts.setDrawAxisLine(false);
        counts.setGridColor(grid);
        counts.enableGridDashedLine(8f, 6f, 0f);
        counts.setTextColor(muted);
        counts.setTextSize(11f);
        // Headroom above the tallest column for its count label
        counts.setSpaceTop(18f);
        counts.setXOffset(8f);
        chart.getAxisRight().setEnabled(false);
    }

    /** Draws one column per charted group, labelled with its count. */
    private void bindSubpops(ArrayList<Integer> subpops) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dashboardSubpops.size(); i++) {
            entries.add(new BarEntry(i, subpopCount(subpops, dashboardSubpops.get(i))));
        }
        BarDataSet set = new BarDataSet(entries, "");
        // Each group keeps its own colour; the count above and the name below label every column
        ArrayList<Integer> colors = new ArrayList<>();
        for (Subpopulation subpop : dashboardSubpops) {
            colors.add(androidx.core.content.ContextCompat.getColor(this, subpop.color));
        }
        set.setColors(colors);
        set.setValueTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.dash_text));
        set.setValueTextSize(13f);
        set.setValueTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry entry) {
                return String.valueOf(Math.round(entry.getY()));
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);

        BarChart chart = binding.dashSubpopChart;
        chart.setData(data);
        chart.animateY(900, Easing.EaseInOutQuad);
    }

    /** The count for one subpopulation; DashboardState.subpops holds one per Subpopulation in declaration order. */
    private static int subpopCount(ArrayList<Integer> subpops, Subpopulation subpop) {
        int idx = subpop.ordinal();
        Integer value = subpops != null && idx < subpops.size() ? subpops.get(idx) : null;
        return value != null ? value : 0;
    }

    /** Colours the visits-due icon and status line with the reserved status colours; the label carries the meaning too. */
    private void bindVisitsDueStatus(int visits) {
        int status;
        int soft;
        int label;
        if (visits == 0) {
            status = R.color.dash_status_good;
            soft = R.color.dash_status_good_soft;
            label = R.string.dash_status_none_due;
        } else if (visits > 5) {
            status = R.color.dash_status_critical;
            soft = R.color.dash_status_critical_soft;
            label = R.string.dash_status_many_due;
        } else {
            status = R.color.dash_status_warning;
            soft = R.color.dash_status_warning_soft;
            label = R.string.dash_status_due;
        }
        int statusColor = androidx.core.content.ContextCompat.getColor(this, status);
        binding.iconDueVisitsChip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, soft)));
        binding.iconDueVisits.setColorFilter(statusColor, PorterDuff.Mode.SRC_IN);
        binding.dueStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));
        binding.dueStatusText.setText(label);
    }

    private boolean hasChartData(ArrayList<Integer> subpops) {
        for (Subpopulation subpop : dashboardSubpops) {
            if (subpopCount(subpops, subpop) > 0) {
                return true;
            }
        }
        return false;
    }

    private void setChartLoading(boolean isLoading) {
        if (loadingDataProgressBar != null) {
            loadingDataProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
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
        setChartLoading(true);
        binding.chartEmptyState.setVisibility(View.GONE);
        binding.dashSubpopList.setVisibility(View.VISIBLE);
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

                        Log.i("chobela_creds", "userinfo response: name=[" + name + "] given_name=[" + given_name + "] family_name=[" + family_name + "]");

                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String ward = jObj.optString("ward", "");
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
                        edit.putString("ward", ward);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();
                        recreate();

                    } catch (JSONException e){
                        Log.e("chobela_creds", "Failed to parse userinfo response: " + response, e);
                    }
                },
                error -> {
                    String status = error.networkResponse != null ? String.valueOf(error.networkResponse.statusCode) : "no response";
                    Log.e("chobela_creds", "userinfo request failed, status=" + status, error);
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
    }

    public void refreshData() {
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            public void run() {
                loadData();
                handler.postDelayed(runnable, FORTY_FIVE_MINUTES);
            }
        };
        handler.postDelayed(runnable, FORTY_FIVE_MINUTES);
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

                        Log.i("chobela_token", "token request succeeded");

                        String jsonInString = new Gson().toJson(response.toString().trim());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString().trim());

                            String token  = jsonObject.getString("access_token");

                            getCreds(token);
                            loadData();


                        } catch (JSONException e) {
                            Log.e("chobela_token", "Failed to parse token response: " + response, e);
                        }

                    }
                },
                error -> {
                    String status = error.networkResponse != null ? String.valueOf(error.networkResponse.statusCode) : "no response";
                    String body = error.networkResponse != null && error.networkResponse.data != null
                            ? new String(error.networkResponse.data) : "";
                    Log.e("chobela_token", "token request failed, status=" + status + " body=" + body, error);
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
                Log.i("chobela_token", "requesting token client_id=[" + BuildConfig.OAUTH_CLIENT_ID + "] username=[" + username + "]");
                return params;
            }};

        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    public void loadCaseworkerData(){
        setChartLoading(true);
        binding.chartEmptyState.setVisibility(View.GONE);
        binding.dashSubpopList.setVisibility(View.VISIBLE);
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
            case R.id.pmp_flags:
                startActivity(new Intent(this, FlagActivity.class));
                break;
            case R.id.report_problem:
                ReportProblemActivity.start(this);
                break;
            case R.id.download_ecap_sops:
                downloadEcapSops();
                break;
            case R.id.view_ecap_sops:
                openEcapSops();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadEcapSops() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading SOPs");
        progressDialog.setMessage("PreparingÃ¢â‚¬Â¦");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
        } catch (Exception ignored) {
        }

        // Android 14: use MediaStore Downloads + RELATIVE_PATH to automatically create Downloads/ECAP II SOPs.
        PublicGoogleDriveFolderDownloader.downloadPublicFolderToPublicDownloads(
                this,
                ECAP_SOPS_FOLDER_ID,
                ECAP_SOPS_TARGET_DIR,
                new PublicGoogleDriveFolderDownloader.Callback() {
                    @Override
                    public void onProgress(int downloaded, int total) {
                        try {
                            progressDialog.setIndeterminate(false);
                            progressDialog.setMax(Math.max(total, 1));
                            progressDialog.setProgress(downloaded);
                            progressDialog.setMessage("Downloading " + downloaded + " / " + total);
                        } catch (Exception ignored) {
                        }
                    }

                    @Override
                    public void onSuccess(java.io.File targetDir, int downloaded) {
                        try { progressDialog.dismiss(); } catch (Exception ignored) {}
                        showCustomDialog(DashboardActivity.this,
                                "Downloaded " + downloaded + " file(s) to:\nDownloads/" + ECAP_SOPS_TARGET_DIR);
                    }

                    @Override
                    public void onError(Throwable t) {
                        try { progressDialog.dismiss(); } catch (Exception ignored) {}
                        String msg = "Download failed: " + (t != null ? String.valueOf(t.getMessage()) : "Unknown error") +
                                "\n\nIf Drive asks for permission, request access in your browser and try again.";
                        showCustomDialog(DashboardActivity.this, msg, () -> {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ECAP_SOPS_FOLDER_URL)));
                            } catch (Exception ignored) { }
                        });
                    }
                }
        );
    }

    private void openEcapSops() {
        Intent i = new Intent(this, SopDocumentsActivity.class);
        i.putExtra(SopDocumentsActivity.EXTRA_TITLE, "ECAP II SOPs");
        // Default relative path in activity is Downloads/ECAP II SOPs/
        startActivity(i);
    }

    @Override
    public void showCSVGeneratedMessage(String filePath) {

    }

    @Override
    public void showError(String errorMessage) {

    }


    //format values on top of the bars rto return whole numbers


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
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode != REQUEST_CODE_IMPORT_CSV) {
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
