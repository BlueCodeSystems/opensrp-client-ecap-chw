package org.smartregister.chw.core.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.SectionsPagerAdapter;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.domain.ReportHia2Indicator;
import org.smartregister.chw.core.fragment.DraftMonthlyFragment;
import org.smartregister.chw.core.fragment.SendMonthlyDraftDialogFragment;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.chw.core.task.FetchEditedMonthlyTalliesTask;
import org.smartregister.chw.core.task.StartDraftMonthlyFormTask;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.ReportUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.MultiLanguageActivity;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;
import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class HIA2ReportsActivity extends MultiLanguageActivity
        implements NavigationView.OnNavigationItemSelectedListener, SyncStatusBroadcastReceiver.SyncStatusListener {
    public static final int MONTH_SUGGESTION_LIMIT = 36;
    public static final DateFormat dfyymmdd = new SimpleDateFormat(CoreConstants.DB_DATE_FORMAT, Locale.ENGLISH);
    public static final DateFormat dfyymm = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
    public static final String REPORT_NAME = "HIA2";
    public static final int REQUEST_CODE_GET_JSON = 3432;
    public static final String FORM_KEY_CONFIRM = "confirm";
    public static final int TOOLBAR_ID = R.id.location_switching_toolbar;
    protected static final String TAG = HIA2ReportsActivity.class.getCanonicalName();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    protected ViewPager mViewPager;
    protected TabLayout tabLayout;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_hia2_reports);
        Toolbar toolbar = findViewById(getToolbarId());
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.hia_tabs);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        refreshDraftMonthlyTitle();
        //  mSectionsPagerAdapter.getItem(1);
        mViewPager.setCurrentItem(1);
        findViewById(R.id.toggle_action_menu).setOnClickListener(v -> onClickReport(v));
    }


    protected Fragment currentFragment() {
        if (mViewPager == null || mSectionsPagerAdapter == null) {
            return null;
        }

        return mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    public void startMonthlyReportForm(String formName, Date date) {
        try {
            Fragment currentFragment = currentFragment();
            if (currentFragment instanceof DraftMonthlyFragment) {
                Utils.startAsyncTask(new StartDraftMonthlyFormTask(this, date, formName), null);
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra("json");

                boolean skipValidationSet = data.getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
                JSONObject form = new JSONObject(jsonString);
                String monthString = form.getString("report_month");
                Date month = dfyymmdd.parse(monthString);

                JSONObject monthlyDraftForm = new JSONObject(jsonString);

                JSONArray fieldsArray = JsonFormUtils.getMultiStepFormFields(monthlyDraftForm);

                Map<String, String> result = new HashMap<>();
                for (int j = 0; j < fieldsArray.length(); j++) {
                    JSONObject fieldJsonObject = fieldsArray.getJSONObject(j);
                    String key = fieldJsonObject.getString(KEY);
                    String value = !fieldJsonObject.has(VALUE) ? "" : fieldJsonObject.getString(VALUE);
                    if(!value.equals("")){
                        result.put(key, value);
                    }
                }

                boolean saveClicked;
                if (result.containsKey(FORM_KEY_CONFIRM)) {
                    saveClicked = Boolean.valueOf(result.get(FORM_KEY_CONFIRM));
                    result.remove(FORM_KEY_CONFIRM);
                    if (skipValidationSet) {
                        Snackbar.make(tabLayout, R.string.all_changes_saved, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    saveClicked = true;
                }

                CoreChwApplication.getInstance().monthlyTalliesRepository().save(result, month);
                if (saveClicked && !skipValidationSet) {
                    sendReport(month);
                }
            } catch (JSONException e) {
                Timber.e("JSONException " + e.getMessage());
            } catch (ParseException e) {
                Timber.e("JSONException" + e.getMessage());
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    protected void sendReport(final Date month) {
        if (month != null) {
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            android.app.Fragment prev = getFragmentManager()
                    .findFragmentByTag("SendMonthlyDraftDialogFragment");
            if (prev != null) {
                ft.remove(prev);
            }

            String monthString = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(month);
            // Create and show the dialog.
            SendMonthlyDraftDialogFragment newFragment = SendMonthlyDraftDialogFragment
                    .newInstance(monthString,
                            new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Calendar.getInstance().getTime()),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String[] params = new String[]{new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(month)};
                                    Utils.startAsyncTask(new GenerateAndSendMonthlyTask(), params);
                                }
                            });
            ft.add(newFragment, "SendMonthlyDraftDialogFragment");
            ft.commitAllowingStateLoss();
        }
    }

    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    public void onBackPressed() {
        DrawerLayout drawer = findViewById(getDrawerLayoutId());
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected int getToolbarId() {
        return TOOLBAR_ID;
    }

    protected Class onBackActivity() {
        return null;
    }

    public void refreshDraftMonthlyTitle() {
        Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPostExecute(final List<MonthlyTally> monthlyTallies) {
                tabLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                            TabLayout.Tab tab = tabLayout.getTabAt(i);
                            if (tab != null && tab.getText() != null && tab.getText().toString()
                                    .contains(getString(R.string.hia2_draft_monthly))) {
                                tab.setText(String.format(
                                        getString(R.string.hia2_draft_monthly_with_count),
                                        monthlyTallies == null ? 0 : monthlyTallies.size()));
                            }
                        }
                    }
                });
            }
        }), null);
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            initializeProgressDialog();
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onClickReport(View view) {
        try{
            if (view != null && view.getId() == R.id.toggle_action_menu) {
                NavigationMenu.getInstance(this, null, null).getDrawer().openDrawer(GravityCompat.START);
            }
        }
        catch (Exception ex){
            Timber.e(ex, "View is NUll");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onSyncStart() {
//Implement Super
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
//Implement Super

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
//Implement Super

    }

    /*
      Generates monthly report and pushes to server
     */
    protected class GenerateAndSendMonthlyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String monthString = params[0];
            try {
                if (!TextUtils.isEmpty(monthString)) {
                    Date month = dfyymm.parse(monthString);
                    generateMonthlyReport(month);
                }
                // push report to server
                pushReportsToServer();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            super.onPostExecute(res);
            hideProgressDialog();
            // update drafts view
            refreshDraftMonthlyTitle();
            Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
                @Override
                public void onPostExecute(List<MonthlyTally> monthlyTallies) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
                    ((DraftMonthlyFragment) fragment).updateDraftsReportListView(monthlyTallies);
                }
            }), null);
        }

        private void generateMonthlyReport(Date month) {
            MonthlyTalliesRepository monthlyTalliesRepository = CoreChwApplication.getInstance().monthlyTalliesRepository();
            try {
                if (month != null) {
                    List<MonthlyTally> tallies = monthlyTalliesRepository
                            .find(MonthlyTalliesRepository.DF_YYYYMM.format(month));
                    if (tallies != null) {
                        List<ReportHia2Indicator> tallyReports = new ArrayList<>();
                        for (MonthlyTally curTally : tallies) {
                            tallyReports.add(curTally.getReportHia2Indicator());
                        }

                        ReportUtils.createReport(tallyReports, month, REPORT_NAME);

                        for (MonthlyTally curTally : tallies) {
                            Date dateSent = curTally.getDateSent() != null ? curTally.getDateSent() : Calendar.getInstance().getTime();
                            Date dateCreated = curTally.getCreatedAt() != null ? curTally.getCreatedAt() : Calendar.getInstance().getTime();
                            curTally.setDateSent(dateSent);
                            curTally.setCreatedAt(dateCreated);
                            ReportUtils.addEvent(ReportUtils.geValues(curTally, null), curTally.getSubmissionId(), CoreConstants.JSON_FORM.IN_APP_REPORT_FORM, CoreConstants.TABLE_NAME.MONTHLY_TALLIES_REPORT);
                        }
                        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                        Date lastSyncDate = new Date(lastSyncTimeStamp);
                        NCUtils.getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
                        getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
                    } else {
                        Log.d("Tallies", "tallies are  null");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }


        protected void pushReportsToServer() {
            final String REPORTS_SYNC_PATH = "/rest/report/add";
            final Context context = CoreChwApplication.getInstance().getContext().applicationContext();
            HTTPAgent httpAgent = CoreChwApplication.getInstance().getContext().getHttpAgent();
            Hia2ReportRepository hia2ReportRepository = CoreChwApplication.getInstance().hia2ReportRepository();
            try {
                boolean keepSyncing = true;
                int limit = 50;
                while (keepSyncing) {
                    List<JSONObject> pendingReports = hia2ReportRepository.getUnSyncedReports(limit);

                    if (pendingReports.isEmpty()) {
                        return;
                    }

                    String baseUrl = CoreChwApplication.getInstance().getContext().configuration().dristhiBaseURL();
                    if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                    }
                    // create request body
                    JSONObject request = new JSONObject();

                    request.put("reports", pendingReports);
                    String jsonPayload = request.toString();
                    Response<String> response = httpAgent.post(
                            MessageFormat.format("{0}/{1}",
                                    baseUrl,
                                    REPORTS_SYNC_PATH),
                            jsonPayload);
                    if (response.isFailure()) {
                        Log.e(getClass().getName(), "Reports sync failed.");
                        return;
                    }
                    hia2ReportRepository.markReportsAsSynced(pendingReports);
                    Log.i(getClass().getName(), "Reports synced successfully.");

                    // update drafts view
                    refreshDraftMonthlyTitle();
                    Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
                        @Override
                        public void onPostExecute(List<MonthlyTally> monthlyTallies) {
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
                            ((DraftMonthlyFragment) fragment).updateDraftsReportListView(monthlyTallies);
                        }
                    }), null);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }
}

