package org.smartregister.chw.anc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONException;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.custom_views.BaseAncFloatingMenu;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.interactor.BaseAncMemberProfileInteractor;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.opensrp_chw_anc.R;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.AlertStatus;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static org.smartregister.chw.anc.AncLibrary.getInstance;
import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME;
import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE;
import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT;
import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.TITLE_VIEW_TEXT;
import static org.smartregister.util.Utils.getName;

public class BaseAncMemberProfileActivity extends BaseProfileActivity implements BaseAncMemberProfileContract.View {
    protected MemberObject memberObject;
    protected String baseEntityID;
    protected TextView text_view_anc_member_name;
    protected TextView text_view_ga;
    protected TextView text_view_address;
    protected TextView text_view_id;
    protected TextView textViewGravida;
    protected TextView pregnancyRiskLabel;
    protected TextView textview_record_anc_visit;
    protected TextView textViewAncVisitNot;
    protected TextView textViewNotVisitMonth;
    protected TextView textViewUndo;
    protected TextView tvEdit;
    protected LinearLayout layoutRecordView;
    protected LinearLayout record_reccuringvisit_done_bar;
    protected RelativeLayout rlLastVisit;
    protected RelativeLayout rlUpcomingServices;
    protected RelativeLayout rlFamilyServicesDue;
    protected RelativeLayout layoutRecordButtonDone;
    protected RelativeLayout layoutNotRecordView;
    protected RelativeLayout rlFamilyLocation;
    protected TextView textview_record_visit;
    protected TextView recordRecurringVisit;
    protected View view_anc_record;
    protected View view_last_visit_row;
    protected View view_most_due_overdue_row;
    protected View view_family_row;
    protected View view_family_location_row;
    protected CircleImageView imageView;
    protected BaseAncFloatingMenu baseAncFloatingMenu;
    protected TextView tvLastVisitDate;
    protected ImageView imageViewCross;
    private TextView tvUpComingServices;
    private TextView tvFamilyStatus;
    private ProgressBar progressBar;
    private String ancWomanName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private String titleViewText;
    private LinearLayout defaultProfileHeaderLayout;
    private LinearLayout pregnancyRiskProfileHeaderLayout;
    protected TextView text_view_pg_risk_ga;
    protected TextView text_view_pg_risk_address;
    protected  TextView text_view_pg_risk_id;


    public BaseAncMemberProfileActivity() {
        memberObject = new MemberObject();
    }

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, BaseAncHomeVisitActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_anc_member_profile);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            memberObject = (MemberObject) getIntent().getSerializableExtra(MEMBER_PROFILE_OBJECT);
            baseEntityID = getIntent().getStringExtra(Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID);
            if (memberObject == null) {
                memberObject = getMemberObject(baseEntityID);
            }
            memberObject.setFamilyHead(getIntent().getStringExtra(FAMILY_HEAD_NAME));
            memberObject.setFamilyHeadPhoneNumber(getIntent().getStringExtra(FAMILY_HEAD_PHONE));
            setTitleViewText(getIntent().getStringExtra(TITLE_VIEW_TEXT));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
        imageRenderHelper = new ImageRenderHelper(this);

        progressBar = findViewById(R.id.progress_bar);
        view_last_visit_row = findViewById(R.id.view_last_visit_row);
        view_most_due_overdue_row = findViewById(R.id.view_most_due_overdue_row);
        view_family_row = findViewById(R.id.view_family_row);
        view_family_location_row = findViewById(R.id.view_family_location_row);

        tvLastVisitDate = findViewById(R.id.textview_last_vist_day);
        tvUpComingServices = findViewById(R.id.textview_name_due);
        tvFamilyStatus = findViewById(R.id.textview_family_has);
        record_reccuringvisit_done_bar = findViewById(R.id.record_reccuringvisit_done_bar);
        textview_record_visit = findViewById(R.id.textview_record_visit);

        rlFamilyLocation = findViewById(R.id.rlFamilyLocation);

        initializePresenter();
        setFamilyLocation();
    }

    protected void registerPresenter() {
        presenter = new BaseAncMemberProfilePresenter(this, new BaseAncMemberProfileInteractor(), memberObject);
    }

    public void initializeFloatingMenu() {
        if (StringUtils.isNotBlank(memberObject.getPhoneNumber()) || StringUtils.isNotBlank(memberObject.getFamilyHeadPhoneNumber())) {
            baseAncFloatingMenu = new BaseAncFloatingMenu(this, ancWomanName, memberObject.getPhoneNumber(), memberObject.getFamilyHeadName(), memberObject.getFamilyHeadPhoneNumber(), getProfileType());
            baseAncFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            addContentView(baseAncFloatingMenu, linearLayoutParams);
        }
    }

    protected void displayView() {
        Visit lastAncHomeVisitNotDoneEvent = getVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
        Visit lastAncHomeVisitNotDoneUndoEvent = getVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);

        if (lastAncHomeVisitNotDoneEvent != null && lastAncHomeVisitNotDoneUndoEvent != null &&
                lastAncHomeVisitNotDoneUndoEvent.getDate().before(lastAncHomeVisitNotDoneEvent.getDate())
                && ancHomeVisitNotDoneEvent(lastAncHomeVisitNotDoneEvent)) {
            setVisitViews();
        } else if (lastAncHomeVisitNotDoneUndoEvent == null && lastAncHomeVisitNotDoneEvent != null && ancHomeVisitNotDoneEvent(lastAncHomeVisitNotDoneEvent)) {
            setVisitViews();
        }
        Visit lastVisit = getVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT);
        if (lastVisit != null) {
            setUpEditViews(true, VisitUtils.isVisitWithin24Hours(lastVisit), lastVisit.getDate().getTime());
        }
    }

    protected String getProfileType() {
        return Constants.MEMBER_PROFILE_TYPES.ANC;
    }

    public @Nullable Visit getVisit(String eventType) {
        return getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), eventType);
    }

    protected boolean ancHomeVisitNotDoneEvent(Visit visit) {

        return visit != null
                && (new DateTime(visit.getDate()).getMonthOfYear() == new DateTime().getMonthOfYear())
                && (new DateTime(visit.getDate()).getYear() == new DateTime().getYear());
    }

    protected void setVisitViews() {
        openVisitMonthView();
        textViewNotVisitMonth.setText(getString(R.string.not_visiting_this_month));
        textViewUndo.setText(getString(R.string.undo));
        textViewUndo.setVisibility(View.VISIBLE);
        imageViewCross.setImageResource(R.drawable.activityrow_notvisited);
    }

    protected void setUpEditViews(boolean enable, boolean within24Hours, Long longDate) {
        openVisitMonthView();
        if (enable) {
            if (within24Hours) {
                Calendar cal = Calendar.getInstance();
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                Date date = new Date(longDate - (long) offset);
                String monthString = (String) DateFormat.format("MMMM", date);
                layoutRecordView.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                textViewNotVisitMonth.setText(getContext().getString(R.string.anc_visit_done, monthString));
                imageViewCross.setImageResource(R.drawable.activityrow_visited);
            } else {
                record_reccuringvisit_done_bar.setVisibility(View.VISIBLE);
                layoutNotRecordView.setVisibility(View.GONE);
            }
            textViewUndo.setVisibility(View.GONE);
        } else
            tvEdit.setVisibility(View.GONE);
    }

    //TODO: Find source of NPE
    public void openVisitMonthView() {
        if (layoutNotRecordView == null || layoutRecordButtonDone == null || layoutRecordView == null)
            return;

        layoutNotRecordView.setVisibility(View.VISIBLE);
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setMemberName(String memberName) {
        text_view_anc_member_name.setText(memberName);
    }

    @Override
    public void setRecordVisitTitle(String title) {
        textview_record_anc_visit.setText(title);
    }

    private String gestAge(String memberGA){
      return  String.format(getString(R.string.gest_age), String.valueOf(memberGA)) + " " + getString(R.string.gest_age_weeks);
    }

    @Override
    public void setMemberGA(String memberGA) {
        text_view_ga.setText(gestAge(memberGA));
    }

    @Override
    public void setPgRiskMemberGA(String memberGA) {
        text_view_pg_risk_ga.setText(gestAge(memberGA));
    }

    @Override
    public void setMemberPgRiskGravida(String gravida) {
        if (textViewGravida != null && StringUtils.isNotBlank(gravida)) {
            String gravidaTextValue = String.format(getString(R.string.gravida_text), gravida);
            textViewGravida.setText(gravidaTextValue);
        }
    }

    @Override
    public void setMemberPgRiskAddress(String memberAddress) {
        text_view_pg_risk_address.setText(memberAddress);
    }

    public void setMemberPgRiskChwMemberId(String memberChwMemberId) {
        String uniqueId = String.format(getString(R.string.unique_id_text), memberChwMemberId);
        text_view_pg_risk_id.setText(uniqueId);
    }

    @Override
    public void setMemberAddress(String memberAddress) {
        text_view_address.setText(memberAddress);
    }

    public void setMemberChwMemberId(String memberChwMemberId) {
        String uniqueId = String.format(getString(R.string.unique_id_text), memberChwMemberId);
        text_view_id.setText(uniqueId);
    }

    @Override
    public void setMemberGravida(String gravida) {
        if (textViewGravida != null && StringUtils.isNotBlank(gravida)) {
            String gravidaTextValue = String.format(getString(R.string.gravida_text), gravida);
            textViewGravida.setText(gravidaTextValue);
        }
    }

    @Override
    public void setPregnancyRiskLabel(String pregnancyRiskLevel) {
        if (pregnancyRiskLabel != null && StringUtils.isNotBlank(pregnancyRiskLevel)) {
            int labelTextColor;
            int background;
            String labelText;
            switch (pregnancyRiskLevel) {
                case Constants.HOME_VISIT.PREGNANCY_RISK_LOW:
                    labelTextColor = context().getColorResource(R.color.low_risk_text_green);
                    background = R.drawable.low_risk_label;
                    labelText = getContext().getString(R.string.low_pregnancy_risk);
                    break;
                case Constants.HOME_VISIT.PREGNANCY_RISK_MEDIUM:
                    labelTextColor = context().getColorResource(R.color.medium_risk_text_orange);
                    background = R.drawable.medium_risk_label;
                    labelText = getContext().getString(R.string.medium_pregnancy_risk);
                    break;
                case Constants.HOME_VISIT.PREGNANCY_RISK_HIGH:
                    labelTextColor = context().getColorResource(R.color.high_risk_text_red);
                    background = R.drawable.high_risk_label;
                    labelText = getContext().getString(R.string.high_pregnancy_risk);
                    break;
                default:
                    labelTextColor = context().getColorResource(R.color.default_risk_text_black);
                    background = R.drawable.risk_label;
                    labelText = getContext().getString(R.string.low_pregnancy_risk);
                    break;
            }
            pregnancyRiskLabel.setVisibility(View.VISIBLE);
            pregnancyRiskLabel.setText(labelText);
            pregnancyRiskLabel.setTextColor(labelTextColor);
            pregnancyRiskLabel.setBackgroundResource(background);
        }
    }

    @Override
    public MemberObject getMemberObject(String baseEntityID) {
        return null;
    }

    @Override
    public BaseAncMemberProfileContract.Presenter presenter() {
        return (BaseAncMemberProfileContract.Presenter) presenter;
    }

    @Override
    public void openMedicalHistory() {
        BaseAncMedicalHistoryActivity.startMe(this, memberObject);
    }

    @Override
    public void openUpcomingService() {
        BaseAncUpcomingServicesActivity.startMe(this, memberObject);
    }

    @Override
    public void openFamilyDueServices() {
        // TODO implement
    }

    @Override
    public void openFamilyLocation() {
        // TODO implement
    }

    @Override
    public void setProfileImage(String baseEntityId, String entityType) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, NCUtils.getMemberProfileImageResourceIDentifier("anc"));
    }

    @Override
    public void setVisitNotDoneThisMonth() {
        setVisitViews();
        saveVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
    }

    private void saveVisit(String eventType) {
        try {
            Event event = JsonFormUtils.createUntaggedEvent(memberObject.getBaseEntityId(), eventType, Constants.TABLES.ANC_MEMBERS);
            Visit visit = NCUtils.eventToVisit(event, JsonFormUtils.generateRandomUUIDString());
            visit.setPreProcessedJson(new Gson().toJson(event));
            getInstance().visitRepository().addVisit(visit);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void updateVisitNotDone(long value) {
        textViewUndo.setVisibility(View.GONE);
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordButtonDone.setVisibility(View.VISIBLE);
        layoutRecordView.setVisibility(View.VISIBLE);
        saveVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);
    }

    @Override
    public void showProgressBar(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLastVisit(Date lastVisitDate) {
        if (lastVisitDate == null)
            return;

        view_last_visit_row.setVisibility(View.VISIBLE);
        rlLastVisit.setVisibility(View.VISIBLE);

        int numOfDays = Days.daysBetween(new DateTime(lastVisitDate).toLocalDate(), new DateTime().toLocalDate()).getDays();
        tvLastVisitDate.setText(getString(R.string.last_visit_40_days_ago, (numOfDays <= 1) ? getString(R.string.less_than_twenty_four) : String.valueOf(numOfDays) + " " + getString(R.string.days)));
    }

    @Override
    public void setUpComingServicesStatus(String service, AlertStatus status, Date date) {
        if (status == AlertStatus.complete)
            return;
        view_most_due_overdue_row.setVisibility(View.VISIBLE);
        rlUpcomingServices.setVisibility(View.VISIBLE);

        if (status == AlertStatus.upcoming) {
            tvUpComingServices.setText(NCUtils.fromHtml(getString(R.string.vaccine_service_upcoming, service, dateFormat.format(date))));
        } else if (status == AlertStatus.urgent) {
            tvUpComingServices.setText(NCUtils.fromHtml(getString(R.string.vaccine_service_overdue, service, dateFormat.format(date))));
        } else {
            tvUpComingServices.setText(NCUtils.fromHtml(getString(R.string.vaccine_service_due, service, dateFormat.format(date))));
        }
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        view_family_row.setVisibility(View.VISIBLE);
        rlFamilyServicesDue.setVisibility(View.VISIBLE);

        if (status == AlertStatus.complete) {
            tvFamilyStatus.setText(getString(R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            tvFamilyStatus.setText(getString(R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            tvFamilyStatus.setText(NCUtils.fromHtml(getString(R.string.family_has_service_overdue)));
        }
    }

    public void setFamilyLocation() {
        view_family_location_row.setVisibility(View.GONE);
        rlFamilyLocation.setVisibility(View.GONE);
    }

    @Override
    public void onMemberDetailsReloaded(MemberObject memberObject) {
        this.memberObject = memberObject;

        // update the screen with profile info
        setupViews();
        fetchProfileData();
        presenter().refreshProfileBottom();
        initializeFloatingMenu();
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return false;
    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlLastVisit) {
            this.openMedicalHistory();
        } else if (v.getId() == R.id.rlUpcomingServices) {
            this.openUpcomingService();
        } else if (v.getId() == R.id.rlFamilyLocation) {
            this.openFamilyLocation();
        } else if (v.getId() == R.id.rlFamilyServicesDue) {
            this.openFamilyDueServices();
        } else if (v.getId() == R.id.textview_anc_visit_not) {
            presenter().getView().setVisitNotDoneThisMonth();
        } else if (v.getId() == R.id.textview_undo) {
            presenter().getView().updateVisitNotDone(0);
        }
    }

    @Override
    protected void initializePresenter() {
        registerPresenter();
        presenter().reloadMemberDetails(baseEntityID);
    }

    @Override
    protected void setupViews() {
        CustomFontTextView titleView = findViewById(R.id.toolbar_title);
        String titleText = TextUtils.isEmpty(getTitleViewText()) ? getString(R.string.return_to_all_anc_women) : getTitleViewText();
        titleView.setText(titleText);

        if (StringUtils.isNotBlank(memberObject.getMiddleName())) {
            ancWomanName = getName(memberObject.getFirstName(), memberObject.getMiddleName());
            ancWomanName = getName(ancWomanName, memberObject.getLastName());
        } else {
            ancWomanName = getName(memberObject.getFirstName(), memberObject.getLastName());
        }

        if (StringUtils.isNotBlank(memberObject.getFamilyHead()) && memberObject.getFamilyHead().equals(memberObject.getBaseEntityId())) {
            findViewById(R.id.family_anc_head).setVisibility(View.VISIBLE);
        }
        if (StringUtils.isNotBlank(memberObject.getPrimaryCareGiver()) && memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId())) {
            findViewById(R.id.primary_anc_caregiver).setVisibility(View.VISIBLE);
        }
        defaultProfileHeaderLayout = findViewById(R.id.default_profile_header_layout);
        pregnancyRiskProfileHeaderLayout = findViewById(R.id.pregnancy_risk_profile_header_layout);

        initializeFloatingMenu();
        text_view_anc_member_name = findViewById(R.id.text_view_anc_member_name);
        text_view_ga = findViewById(R.id.text_view_ga);

        text_view_pg_risk_ga = findViewById(R.id.text_view_pg_risk_ga);
        text_view_pg_risk_address = findViewById(R.id.text_view_pg_risk_address);
        text_view_pg_risk_id = findViewById(R.id.text_view_pg_risk_id);

        text_view_address = findViewById(R.id.text_view_address);
        text_view_id = findViewById(R.id.text_view_id);
        textViewGravida = findViewById(R.id.text_view_pg_risk_gravida);
        pregnancyRiskLabel = findViewById(R.id.risk_label);
        textview_record_anc_visit = findViewById(R.id.textview_record_visit);
        view_anc_record = findViewById(R.id.view_record);
        layoutRecordView = findViewById(R.id.record_visit_bar);
        textViewNotVisitMonth = findViewById(R.id.textview_not_visit_this_month);
        tvEdit = findViewById(R.id.textview_edit);


        rlLastVisit = findViewById(R.id.rlLastVisit);
        rlUpcomingServices = findViewById(R.id.rlUpcomingServices);
        rlFamilyServicesDue = findViewById(R.id.rlFamilyServicesDue);
        textViewAncVisitNot = findViewById(R.id.textview_anc_visit_not);
        layoutRecordButtonDone = findViewById(R.id.record_visit_done_bar);
        textViewUndo = findViewById(R.id.textview_undo);
        imageViewCross = findViewById(R.id.tick_image);
        layoutNotRecordView = findViewById(R.id.record_visit_status_bar);
        recordRecurringVisit = findViewById(R.id.textview_record_reccuring_visit);

        textview_record_anc_visit.setOnClickListener(this);
        rlLastVisit.setOnClickListener(this);
        rlUpcomingServices.setOnClickListener(this);
        rlFamilyServicesDue.setOnClickListener(this);
        rlFamilyLocation.setOnClickListener(this);
        tvEdit.setOnClickListener(this);

        textViewAncVisitNot.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);
        imageViewCross.setOnClickListener(this);
        layoutRecordButtonDone.setOnClickListener(this);
        recordRecurringVisit.setOnClickListener(this);

        imageView = findViewById(R.id.imageview_profile);
        imageView.setBorderWidth(2);
        setRecordVisitTitle(getString(R.string.record_anc_visit));

        displayView();
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
    }

    public void setTitleViewText(String titleText) {
        this.titleViewText = titleText;
    }

    public String getTitleViewText() {
        return this.titleViewText;
    }

    @Override
    public void setDefaultProfileHeaderActive() {
        defaultProfileHeaderLayout.setVisibility(View.VISIBLE);
        pregnancyRiskProfileHeaderLayout.setVisibility(View.GONE);
    }

    @Override
    public void setPregnancyRiskProfileHeaderActive() {
        defaultProfileHeaderLayout.setVisibility(View.GONE);
        pregnancyRiskProfileHeaderLayout.setVisibility(View.VISIBLE);
    }

    public String getFamilyHeadName() {
        return memberObject.getFamilyHeadName();
    }

    public void setFamilyHeadName(String familyHeadName) {
        memberObject.setFamilyHeadName(familyHeadName);
    }

    public String getFamilyHeadPhoneNumber() {
        return memberObject.getFamilyHeadPhoneNumber();
    }

    public void setFamilyHeadPhoneNumber(String familyHeadPhoneNumber) {
        memberObject.setFamilyHeadPhoneNumber(familyHeadPhoneNumber);
    }

    public String getAncWomanName() {
        return ancWomanName;
    }

    public void setAncWomanName(String ancWomanName) {
        this.ancWomanName = ancWomanName;
    }

}
