package org.smartregister.chw.core.presenter;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.CallableInteractor;
import org.smartregister.util.CallableInteractorCallBack;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import timber.log.Timber;

public class CoreAncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements
        FamilyProfileContract.InteractorCallBack, AncMemberProfileContract.Presenter, AncMemberProfileContract.InteractorCallBack {
    private String entityId;
    private WeakReference<AncMemberProfileContract.View> view;
    private AncMemberProfileContract.Interactor interactor;
    private FormUtils formUtils;

    public CoreAncMemberProfilePresenter(AncMemberProfileContract.View view, AncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        setEntityId(memberObject.getBaseEntityId());
        this.view = new WeakReference<>(view);
        this.interactor = interactor;
    }

    @Override
    public CallableInteractor getCallableInteractor() {
        return CoreChwApplication.getInstance().getCallableInteractor();
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
//        TODO Implement
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
//        TODO Implement
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
//        TODO Implement
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
//        TODO Implement
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {
//     TODO Implement
        Timber.d("onRegistrationSaved unimplemented");
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        if (getView() != null) {
            getView().setClientTasks(taskList);
        }
    }

    @Override
    public AncMemberProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void fetchTasks() {
        interactor.getClientTasks(CoreConstants.REFERRAL_PLAN_ID, getEntityId(), this);
    }

    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        interactor.createReferralEvent(allSharedPreferences, jsonString, getEntityId());
    }

    @Override
    public void startAncReferralForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAncReferralForm()));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void startAncDangerSignsOutcomeForm(MemberObject memberObject) {
        try {
            JSONObject formJsonObject = getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAncDangerSignsOutcomeForm());
            Map<String, String> valueMap = new HashMap<>();
            String lmp = memberObject.getLastMenstrualPeriod();
            DateTime lmpDateTime = new DateTime(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(lmp));
            String edd = DateTimeFormat.forPattern("dd-MM-yyyy").print(lmpDateTime.plusDays(280));
            valueMap.put("lmp", lmp);
            valueMap.put("gest_age", String.valueOf(memberObject.getGestationAge()));
            valueMap.put("edd", edd);

            CoreJsonFormUtils.populateJsonForm(formJsonObject, valueMap);
            getView().startFormActivity(formJsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void createAncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception {
        interactor.createAncDangerSignsOutcomeEvent(allSharedPreferences, jsonString, entityID);
    }

    private Pair<String, Date> visitWithinMonth(org.smartregister.chw.core.domain.VisitSummary lastVisit) {
        if (lastVisit == null)
            return null;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        boolean within24Hours = (Days.daysBetween(new DateTime(lastVisit.getDateCreated()), new DateTime()).getDays() < 1) &&
                (Days.daysBetween(new DateTime(lastVisit.getVisitDate()), new DateTime()).getDays() <= 1);

        String lastVisitDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(lastVisit.getVisitDate());
        int monthDiffs = Months.monthsBetween(formatter.parseLocalDate(lastVisitDate).withDayOfMonth(1), new LocalDate().withDayOfMonth(1)).getMonths();
        boolean isVisitThisMonth = monthDiffs < 1;

        if (isVisitThisMonth) {
            if (within24Hours) {
                Calendar cal = Calendar.getInstance();
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                long longDate = lastVisit.getVisitDate().getTime();
                Date date = new Date(longDate - (long) offset);
                return Pair.of(CoreConstants.VISIT_STATE.WITHIN_24_HR, date);
            } else {
                return Pair.of(CoreConstants.VISIT_STATE.WITHIN_MONTH, null);
            }
        }

        return null;
    }

    private VisitSummary computeVisitSummary(
            org.smartregister.chw.core.domain.VisitSummary lastVisit,
            org.smartregister.chw.core.domain.VisitSummary lastNotDoneVisit,
            org.smartregister.chw.core.domain.VisitSummary lastNotDoneVisitUndo,
            MemberObject memberObject
    ) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        RulesEngineHelper rulesEngineHelper = CoreChwApplication.getInstance().getRulesEngineHelper();

        Rules rules = rulesEngineHelper.rules(CoreConstants.RULE_FILE.ANC_HOME_VISIT);
        boolean notDoneIsAfterLastVisit = lastNotDoneVisit != null && lastNotDoneVisitUndo != null && lastNotDoneVisitUndo.getVisitDate().after(lastNotDoneVisit.getVisitDate());

        String visitDate = lastVisit != null ? new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(lastVisit.getVisitDate()) : null;
        String lastVisitNotDone = lastNotDoneVisit != null && notDoneIsAfterLastVisit ? new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(lastNotDoneVisit.getVisitDate()) : null;

        LocalDate ancCreatedDate = null;
        try {
            Date dateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDateCreated().substring(0, 10));
            String createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateCreated);
            ancCreatedDate = formatter.parseLocalDate(createdDate);
        } catch (Exception e) {
            Timber.e(e);
        }

        return HomeVisitUtil.getAncVisitStatus(getView().getContext(), rules, visitDate, lastVisitNotDone, ancCreatedDate);
    }

    @Override
    public void refreshVisitStatus(MemberObject memberObject) {
        CallableInteractor myInteractor = getCallableInteractor();
        if (getView() != null) {
            getView().showProgressBar(true);

            Callable<Pair<String, Date>> callable = () -> {
                String baseEntityID = memberObject.getBaseEntityId();

                Map<String, org.smartregister.chw.core.domain.VisitSummary> summaryMap = VisitDao.getVisitSummary(baseEntityID);
                org.smartregister.chw.core.domain.VisitSummary lastVisit = summaryMap.get(Constants.EVENT_TYPE.ANC_HOME_VISIT);
                org.smartregister.chw.core.domain.VisitSummary lastNotDoneVisit = summaryMap.get(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
                org.smartregister.chw.core.domain.VisitSummary lastNotDoneVisitUndo = summaryMap.get(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);

                Pair<String, Date> result = visitWithinMonth(lastVisit);
                if (result != null) return result;

                VisitSummary visitSummary = computeVisitSummary(lastVisit, lastNotDoneVisit, lastNotDoneVisitUndo, memberObject);
                return Pair.of(visitSummary.getVisitStatus(), null);
            };

            getView().showProgressBar(true);

            myInteractor.execute(callable, new CallableInteractorCallBack<Pair<String, Date>>() {
                @Override
                public void onResult(Pair<String, Date> result) {
                    if (getView() != null) {
                        if (result != null) {
                            getView().onVisitStatusReloaded(result.getLeft(), result.getRight());
                        }
                        getView().showProgressBar(false);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    if (getView() != null) {
                        getView().onError(ex);
                        getView().showProgressBar(false);
                    }
                }
            });
        }
    }

    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }
}


