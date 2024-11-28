package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.interactor.CoreAncMemberProfileInteractor;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Task;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.Date;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.getCommonPersonObjectClient;
import static org.smartregister.chw.core.utils.Utils.updateToolbarTitle;

public abstract class CoreAncMemberProfileActivity extends BaseAncMemberProfileActivity implements AncMemberProfileContract.View {

    protected RecyclerView notificationAndReferralRecyclerView;
    protected RelativeLayout notificationAndReferralLayout;
    protected boolean hasDueServices = false;
    private int loadingThreads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNotificationReferralRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_anc_member_registration) {
            startFormForEdit(R.string.edit_member_form_title, CoreConstants.JSON_FORM.getFamilyMemberRegister());
            return true;
        } else if (itemId == R.id.action_anc_registration) {
            startFormForEdit(R.string.edit_anc_registration_form_title, CoreConstants.JSON_FORM.getAncRegistration());
            return true;
        } else if (itemId == R.id.anc_danger_signs_outcome) {
            ancMemberProfilePresenter().startAncDangerSignsOutcomeForm(memberObject);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anc_member_profile_menu, menu);
        return true;
    }

    @Override // to chw
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case CoreConstants.ProfileActivityResults.CHANGE_COMPLETED:
                Intent intent = new Intent(CoreAncMemberProfileActivity.this, CoreAncRegisterActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
                break;
            case Constants.REQUEST_CODE_HOME_VISIT:
                this.displayView();
                break;
            default:
                break;
        }
    }

    public void startFormActivity(JSONObject formJson) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, formJson,
                Utils.metadata().familyMemberFormActivity), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    // to chw
    public void startFormForEdit(Integer title_resource, String formName) {
        //// TODO: 22/08/19
    }

    public CoreAncMemberProfilePresenter ancMemberProfilePresenter() {
        return (CoreAncMemberProfilePresenter) presenter;
    }


    protected static CommonPersonObjectClient getClientDetailsByBaseEntityID(@NonNull String baseEntityId) {
        return getCommonPersonObjectClient(baseEntityId);
    }

    @Override
    protected void registerPresenter() {
        presenter = new CoreAncMemberProfilePresenter(this, new CoreAncMemberProfileInteractor(this), memberObject);
    }

    @Override
    public void openMedicalHistory() {
        CoreAncMedicalHistoryActivity.startMe(this, memberObject);
    }

    @Override
    public abstract void openUpcomingService();

    @Override
    public abstract void openFamilyDueServices();


    @Override
    public void setFamilyStatus(AlertStatus status) {
        super.setFamilyStatus(status);
        if (status == AlertStatus.complete) {
            hasDueServices = false;
        } else if (status == AlertStatus.normal || status == AlertStatus.urgent) {
            hasDueServices = true;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    private void getLayoutVisibility() {
        layoutRecordView.setVisibility(View.VISIBLE);
        record_reccuringvisit_done_bar.setVisibility(View.GONE);
        textViewAncVisitNot.setVisibility(View.VISIBLE);
        layoutNotRecordView.setVisibility(View.GONE);
    }

    @Override
    public void onVisitStatusReloaded(String status, Date lastVisitDate) {
        if (status.equalsIgnoreCase(CoreConstants.VISIT_STATE.WITHIN_24_HR)) {

            String monthString = (String) DateFormat.format("MMMM", lastVisitDate);
            layoutRecordView.setVisibility(View.GONE);
            tvEdit.setVisibility(View.VISIBLE);
            textViewNotVisitMonth.setText(getContext().getString(R.string.anc_visit_done, monthString));
            imageViewCross.setImageResource(R.drawable.activityrow_visited);

            textViewUndo.setVisibility(View.GONE);
            textViewAncVisitNot.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase(CoreConstants.VISIT_STATE.WITHIN_MONTH)) {

            record_reccuringvisit_done_bar.setVisibility(View.VISIBLE);
            layoutNotRecordView.setVisibility(View.GONE);

            textViewUndo.setVisibility(View.GONE);
            textViewAncVisitNot.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE)) {

            openVisitMonthView();
            textViewUndo.setVisibility(View.GONE);
            textview_record_anc_visit.setBackgroundResource(R.drawable.record_btn_selector_overdue);
            getLayoutVisibility();
        } else if (status.equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE)) {

            openVisitMonthView();
            textViewUndo.setVisibility(View.GONE);
            textview_record_anc_visit.setBackgroundResource(R.drawable.record_btn_anc_selector);
            getLayoutVisibility();
        } else if (status.equalsIgnoreCase(CoreConstants.VISIT_STATE.NOT_VISIT_THIS_MONTH)) {

            openVisitMonthView();
            textViewUndo.setVisibility(View.VISIBLE);
            textViewUndo.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.undo));
            record_reccuringvisit_done_bar.setVisibility(View.GONE);
            openVisitMonthView();
        }
    }

    @Override
    public void setupViews() {
        super.setupViews();
        updateToolbarTitle(this, R.id.toolbar_title, memberObject.getFamilyName());
        ancMemberProfilePresenter().refreshVisitStatus(memberObject);
    }

    protected void initializeNotificationReferralRecyclerView() {
        notificationAndReferralLayout = findViewById(R.id.notification_and_referral_row);
        notificationAndReferralRecyclerView = findViewById(R.id.notification_and_referral_recycler_view);
        if (notificationAndReferralRecyclerView != null)
            notificationAndReferralRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void openFamilyLocation() {
        Intent intent = new Intent(this, CoreAncMemberMapActivity.class);
        intent.putExtra(CoreConstants.KujakuConstants.LAT_LNG, memberObject.getGps());
        intent.putExtra(CoreConstants.KujakuConstants.LAND_MARK, memberObject.getLandmark());
        intent.putExtra(CoreConstants.KujakuConstants.NAME, memberObject.getFullName());
        intent.putExtra(CoreConstants.KujakuConstants.FAMILY_NAME, memberObject.getFamilyName());
        intent.putExtra(CoreConstants.KujakuConstants.ANC_WOMAN_PHONE, memberObject.getPhoneNumber());
        intent.putExtra(CoreConstants.KujakuConstants.ANC_WOMAN_FAMILY_HEAD, memberObject.getFamilyHeadName());
        intent.putExtra(CoreConstants.KujakuConstants.ANC_WOMAN_FAMILY_HEAD_PHONE, memberObject.getFamilyHeadPhoneNumber());
        this.startActivity(intent);
    }

    @Override
    public abstract void setClientTasks(Set<Task> taskList);

    @Override
    public MemberObject getMemberObject(String baseEntityID) {
        return AncDao.getMember(baseEntityID);
    }

    @Override
    public void showProgressBar(boolean isLoading) {
        loadingThreads = isLoading ? (loadingThreads + 1) : (loadingThreads - 1);
        super.showProgressBar(loadingThreads > 0);
    }

    @Override
    public <E extends Exception> void onError(E exception) {
        Timber.e(exception);
    }
}
