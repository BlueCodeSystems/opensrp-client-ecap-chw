package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.chw.core.domain.ProfileTask;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Task;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;
import static org.smartregister.chw.core.utils.Utils.updateToolbarTitle;

public class CoreChildProfileActivity extends BaseProfileActivity implements CoreChildProfileContract.View, CoreChildRegisterContract.InteractorCallBack {
    public static IntentFilter sIntentFilter;

    static {
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    public String childBaseEntityId;
    public String lastVisitDay;
    public OnClickFloatingMenu onClickFloatingMenu;
    public Handler handler = new Handler();
    public RelativeLayout layoutFamilyHasRow;
    public TextView textViewLastVisit;
    public TextView textViewVaccineHistory;
    public TextView textViewDueToday;
    public RelativeLayout layoutLastVisitRow;
    public RelativeLayout layoutVaccineHistoryRow;
    public RelativeLayout layoutServiceDueRow;
    public View viewLastVisitRow;
    public View viewVaccineHistoryRow;
    public TextView textViewFamilyHas;
    protected TextView textViewParentName;
    protected TextView textViewMedicalHistory;
    protected CircleImageView imageViewProfile;
    protected View recordVisitPanel;
    protected MemberObject memberObject;
    protected ImageView imageViewCrossChild;
    protected RelativeLayout notificationAndReferralLayout;
    protected RecyclerView notificationAndReferralRecyclerView;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    private TextView textViewTitle;
    private TextView textViewChildName;
    private TextView textViewGender;
    private TextView textViewAddress;
    private TextView textViewId;
    private TextView textViewRecord;
    private TextView textViewVisitNot;
    private TextView tvEdit;
    protected TextView physicallyChallenged;
    private RelativeLayout layoutNotRecordView;
    private RelativeLayout layoutMostDueOverdue;
    private RelativeLayout layoutSickVisit;
    private RelativeLayout layoutRecordButtonDone;
    private LinearLayout layoutRecordView;
    private View viewMostDueRow;
    public final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                fetchProfileData();

            }
        }
    };
    public View viewFamilyRow;
    private View viewDividerSickRow;
    private TextView textViewNotVisitMonth;
    private TextView textViewUndo;
    private TextView textViewNameDue;
    private TextView textViewSickChild;
    private ImageView imageViewCross;
    private ProgressBar progressBar;

    public static void startMe(Activity activity, MemberObject memberObject, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        passToolbarTitle(activity, intent);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    public static IntentFilter getsIntentFilter() {
        return sIntentFilter;
    }

    public static void setsIntentFilter(IntentFilter sIntentFilter) {
        CoreChildProfileActivity.sIntentFilter = sIntentFilter;
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_child_profile);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null && getIntent().hasExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT)) {
            memberObject = (MemberObject) getIntent().getSerializableExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT);
            childBaseEntityId = memberObject.getBaseEntityId();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        textViewTitle.setOnClickListener(v -> onBackPressed());
        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
        imageRenderHelper = new ImageRenderHelper(this);
        registerReceiver(mDateTimeChangedReceiver, getsIntentFilter());
        initializeNotificationReferralRecyclerView();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.textview_visit_not) {
            setProgressBarState(true);
            presenter().updateVisitNotDone(System.currentTimeMillis());
            tvEdit.setVisibility(View.GONE);
        } else if (i == R.id.textview_undo) {
            setProgressBarState(true);
            presenter().updateVisitNotDone(0);
        }
    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        if (presenter == null) {
            presenter = new CoreChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
        }

        fetchProfileData();
    }

    @Override
    protected void setupViews() {
        textViewParentName = findViewById(R.id.textview_parent_name);
        textViewChildName = findViewById(R.id.textview_name_age);
        textViewGender = findViewById(R.id.textview_gender);
        textViewAddress = findViewById(R.id.textview_address);
        textViewId = findViewById(R.id.textview_id);
        tvEdit = findViewById(R.id.textview_edit);
        imageViewProfile = findViewById(R.id.imageview_profile);
        recordVisitPanel = findViewById(R.id.record_visit_panel);
        textViewRecord = findViewById(R.id.textview_record_visit);
        textViewVisitNot = findViewById(R.id.textview_visit_not);
        textViewNotVisitMonth = findViewById(R.id.textview_not_visit_this_month);
        textViewLastVisit = findViewById(R.id.textview_last_vist_day);
        textViewVaccineHistory = findViewById(R.id.textview_view_vaccine_history);
        textViewDueToday = findViewById(R.id.textview_view_due_today);
        textViewUndo = findViewById(R.id.textview_undo);
        imageViewCrossChild = findViewById(R.id.cross_image_child);
        imageViewCross = findViewById(R.id.cross_image);
        layoutRecordView = findViewById(R.id.record_visit_bar);
        layoutNotRecordView = findViewById(R.id.record_visit_status_bar);
        layoutLastVisitRow = findViewById(R.id.last_visit_row);
        layoutVaccineHistoryRow = findViewById(R.id.vaccine_history);
        layoutServiceDueRow = findViewById(R.id.view_due_today);
        textViewMedicalHistory = findViewById(R.id.text_view_medical_hstory);
        layoutMostDueOverdue = findViewById(R.id.most_due_overdue_row);
        textViewNameDue = findViewById(R.id.textview_name_due);
        layoutFamilyHasRow = findViewById(R.id.family_has_row);
        textViewFamilyHas = findViewById(R.id.textview_family_has);
        layoutRecordButtonDone = findViewById(R.id.record_visit_done_bar);
        viewLastVisitRow = findViewById(R.id.view_last_visit_row);
        viewVaccineHistoryRow = findViewById(R.id.view_vaccine_history_row);
        viewMostDueRow = findViewById(R.id.view_most_due_overdue_row);
        viewFamilyRow = findViewById(R.id.view_family_row);
        progressBar = findViewById(R.id.progress_bar);
        physicallyChallenged = findViewById(R.id.physically_challenged);
        textViewRecord.setOnClickListener(this);
        textViewVisitNot.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);
        imageViewCross.setOnClickListener(this);
        imageViewCrossChild.setOnClickListener(this);
        layoutLastVisitRow.setOnClickListener(this);
        layoutVaccineHistoryRow.setOnClickListener(this);
        layoutServiceDueRow.setOnClickListener(this);
        layoutMostDueOverdue.setOnClickListener(this);
        layoutFamilyHasRow.setOnClickListener(this);
        layoutRecordButtonDone.setOnClickListener(this);

        viewDividerSickRow = findViewById(R.id.view_sick_visit_row);
        layoutSickVisit = findViewById(R.id.sick_visit_row);
        textViewSickChild = findViewById(R.id.textview_sick_visit_has);
        fetchProfileTasks();
    }

    protected void initializeNotificationReferralRecyclerView() {
        notificationAndReferralLayout = findViewById(R.id.notification_and_referral_row);
        notificationAndReferralRecyclerView = findViewById(R.id.notification_and_referral_recycler_view);
        notificationAndReferralRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
        updateImmunizationData();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarLayoutScrollRange = (appBarLayoutScrollRange == -1) ? appBarLayout.getTotalScrollRange() : appBarLayoutScrollRange;
        if (appBarLayoutScrollRange + verticalOffset == 0) {
            textViewTitle.setText(patientName);
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            setUpToolbar();
            appBarTitleIsShown = false;
        }
    }

    public void setUpToolbar() {
        updateToolbarTitle(this, R.id.toolbar_title, memberObject.getFamilyName());
    }

    /**
     * update immunization data and commonpersonobject for child as data may be updated
     * from childhomevisitfragment screen and need at medical history/upcoming service data.
     * need postdelay to update the client map
     */
    private void updateImmunizationData() {
        handler.postDelayed(() -> {
            layoutMostDueOverdue.setVisibility(View.GONE);
            viewMostDueRow.setVisibility(View.GONE);
            presenter().fetchVisitStatus(childBaseEntityId);
            presenter().fetchUpcomingServiceAndFamilyDue(childBaseEntityId);
            presenter().updateChildCommonPerson(childBaseEntityId);
        }, 100);
    }

    /**
     * By this method it'll process the event client at home visit in background. After finish
     * it'll update the child client because for edit it's need the vaccine card,illness,birthcert.
     */
    public void processBackgroundEvent() {
        layoutMostDueOverdue.setVisibility(View.GONE);
        viewMostDueRow.setVisibility(View.GONE);
        presenter().fetchVisitStatus(childBaseEntityId);
        presenter().fetchUpcomingServiceAndFamilyDue(childBaseEntityId);
        presenter().updateChildCommonPerson(childBaseEntityId);
        presenter().processBackGroundEvent();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, jsonForm,
                Utils.metadata().familyMemberFormActivity), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void refreshProfile(FetchStatus fetchStatus) {
        if (fetchStatus.equals(FetchStatus.fetched)) {
            handler.postDelayed(() -> presenter().fetchProfileData(), 100);
        }
    }

    @Override
    public void displayShortToast(int resourceId) {
        super.displayToast(resourceId);
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        int defaultImage = R.drawable.rowavatar_child;// gender.equalsIgnoreCase(Gender.MALE.toString()) ? R.drawable.row_boy : R.drawable.row_girl;
        imageRenderHelper.refreshProfileImage(baseEntityId, imageViewProfile, defaultImage);
    }

    @Override
    public void setParentName(String parentName) {
        textViewParentName.setText(parentName);
    }

    @Override
    public void setGender(String gender) {
        textViewGender.setText(getGenderTranslated(gender));
        updateTopBar(gender);
    }

    private String getGenderTranslated(String gender) {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            return getResources().getString(R.string.male);
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            return getResources().getString(R.string.female);
        }
        return "";
    }

    @Override
    public void setAddress(String address) {
        textViewAddress.setText(address);
    }

    @Override
    public void setId(String id) {
        textViewId.setText(id);
    }

    @Override
    public void setProfileName(String fullName) {
        patientName = fullName;
        textViewChildName.setText(fullName);
    }

    @Override
    public void setAge(String age) {
        textViewChildName.append(", " + age);
    }

    public void setNoButtonView() {
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);
    }

    @Override
    public void setVisitButtonDueStatus() {
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_due);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setVisitButtonOverdueStatus() {
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_overdue);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setVisitNotDoneThisMonth(boolean withinEditPeriod) {
        openVisitMonthView();
        textViewNotVisitMonth.setText(R.string.not_visiting_this_month);
        textViewUndo.setText(R.string.undo);
        textViewUndo.setVisibility(withinEditPeriod ? View.VISIBLE : View.GONE);
        imageViewCrossChild.setImageResource(R.drawable.activityrow_notvisited);
    }

    @Override
    public void setLastVisitRowView(String days) {
        lastVisitDay = days;
        if (TextUtils.isEmpty(days)) {
            layoutLastVisitRow.setVisibility(View.GONE);
            viewLastVisitRow.setVisibility(View.GONE);
        } else {
            layoutLastVisitRow.setVisibility(View.VISIBLE);
            textViewLastVisit.setText(getString(R.string.last_visit_40_days_ago, days));
            viewLastVisitRow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setServiceNameDue(String serviceName, String dueDate) {
        if (!TextUtils.isEmpty(serviceName)) {
            layoutMostDueOverdue.setVisibility(View.VISIBLE);
            viewMostDueRow.setVisibility(View.VISIBLE);
            textViewNameDue.setText(CoreChildUtils.fromHtml(getString(R.string.vaccine_service_due, serviceName, dueDate)));
        } else {
            layoutMostDueOverdue.setVisibility(View.GONE);
            viewMostDueRow.setVisibility(View.GONE);
        }
    }

    @Override
    public void setServiceNameOverDue(String serviceName, String dueDate) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewNameDue.setText(CoreChildUtils.fromHtml(getString(R.string.vaccine_service_overdue, serviceName, dueDate)));
    }

    @Override
    public void setServiceNameUpcoming(String serviceName, String dueDate) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewNameDue.setText(CoreChildUtils.fromHtml(getString(R.string.vaccine_service_upcoming, serviceName, dueDate)));
    }

    public void setDueTodayServices() {
        layoutServiceDueRow.setVisibility(View.VISIBLE);
        textViewDueToday.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisitLessTwentyFourView(String monthName) {
        textViewNotVisitMonth.setText(getString(R.string.visit_month, monthName));
        textViewUndo.setText(getString(R.string.edit));
        textViewUndo.setVisibility(View.GONE);
        imageViewCrossChild.setImageResource(R.drawable.activityrow_visited);
        openVisitMonthView();
    }

    @Override
    public void setVisitAboveTwentyFourView() {
        textViewVisitNot.setVisibility(View.GONE);
        openVisitRecordDoneView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_above_twentyfr);
        textViewRecord.setTextColor(getResources().getColor(R.color.light_grey_text));
    }

    @Override
    public void setFamilyHasNothingElseDue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFamilyHasNothingDue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(getString(R.string.family_has_nothing_due));
    }

    @Override
    public void setFamilyHasServiceDue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(getString(R.string.family_has_services_due));
    }

    @Override
    public void setFamilyHasServiceOverdue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(CoreChildUtils.fromHtml(getString(R.string.family_has_service_overdue)));
    }

    @Override
    public CoreChildProfileContract.Presenter presenter() {
        return (CoreChildProfileContract.Presenter) presenter;
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        //// TODO: 15/08/19
    }

    @Override
    public void enableEdit(boolean enable) {
        if (enable) {
            tvEdit.setVisibility(View.VISIBLE);
            tvEdit.setOnClickListener(this);
        } else {
            tvEdit.setVisibility(View.GONE);
            tvEdit.setOnClickListener(null);
        }
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openVisitMonthView() {
        layoutNotRecordView.setVisibility(View.VISIBLE);
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);
    }

    @Override
    public void showUndoVisitNotDoneView() {
        presenter().fetchVisitStatus(childBaseEntityId);
    }

    @Override
    public void updateAfterBackgroundProcessed() {
        presenter().updateChildCommonPerson(childBaseEntityId);
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        //// TODO: 06/08/19
    }

    @Override
    public void setProgressBarState(@NotNull Boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onJsonProcessed(String eventType, String taskType, @Nullable ProfileTask profileTask) {
        onProfileTaskFetched(taskType, profileTask);
    }

    @Override
    public void fetchProfileTasks() {
        setProgressBarState(true);
        presenter().fetchProfileTask(getContext(), childBaseEntityId);
    }

    @Override
    public void onProfileTaskFetched(@NonNull String taskType, @Nullable ProfileTask profileTask) {
        if (profileTask != null) {
            layoutSickVisit.setVisibility(View.VISIBLE);
            viewDividerSickRow.setVisibility(View.VISIBLE);

            textViewSickChild.setText(profileTask.getTitle());
            layoutSickVisit.setOnClickListener(getSickListener());

        }
        setProgressBarState(false);
    }

    @Override
    public void thinkMdAssessmentProcessed() {
        invalidateOptionsMenu();
    }


    protected View.OnClickListener getSickListener() {
        return null;
    }

    protected void updateTopBar(String gender) {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_blue));
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_pink));
        }
    }

    private void openVisitButtonView() {
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.VISIBLE);
    }

    private void openVisitRecordDoneView() {
        layoutRecordButtonDone.setVisibility(View.VISIBLE);
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);
    }

    @Override
    public void onNoUniqueId() {
        //TODO
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        //TODO
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean editMode, boolean isSaved, FamilyEventClient familyEventClient) {
        Timber.d("onRegistrationSaved unimplemented");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_registration) {
            presenter().startFormForEdit(getString(R.string.edit_child_form_title, memberObject.getFirstName()), presenter().getChildClient());
            return true;
        } else if (i == R.id.action_sick_child_form) {
            presenter().startSickChildForm(presenter().getChildClient());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_profile_menu, menu);
        menu.findItem(R.id.action_sick_child_form).setVisible(false);
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            unregisterReceiver(mDateTimeChangedReceiver);
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED) {
            this.finish();
        }

        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case CoreConstants.ProfileActivityResults.CHANGE_COMPLETED:
                finish();
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                try {
                    String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                    JSONObject form = new JSONObject(jsonString);
                    String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                    if (encounterType.equals(CoreConstants.EventType.UPDATE_CHILD_REGISTRATION)) {
                        presenter().updateChildProfile(jsonString);
                    } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.CHILD_REFERRAL)) {
                        presenter().createSickChildEvent(Utils.getAllSharedPreferences(), jsonString);
                        displayToast(R.string.referral_submitted);
                    } else {
                        presenter().processJson(getContext(), encounterType, "", jsonString);
                    }
                } catch (Exception e) {
                    Timber.e(e, "CoreChildProfileActivity --> onActivityResult");
                }
                break;
            case org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT:
                updateImmunizationData();
                break;
            default:
                break;
        }
    }
}
