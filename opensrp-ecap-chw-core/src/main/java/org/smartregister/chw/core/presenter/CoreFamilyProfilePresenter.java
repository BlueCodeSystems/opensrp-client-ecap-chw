package org.smartregister.chw.core.presenter;

import android.content.Context;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.interactor.CoreChildRegisterInteractor;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.core.repository.AncRegisterRepository;
import org.smartregister.chw.core.repository.PncRegisterRepository;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;
import org.smartregister.view.LocationPickerView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.toList;

public abstract class CoreFamilyProfilePresenter extends BaseFamilyProfilePresenter implements FamilyProfileExtendedContract.Presenter, CoreChildRegisterContract.InteractorCallBack, FamilyProfileExtendedContract.PresenterCallBack {

    protected CoreChildProfileModel childProfileModel;
    private WeakReference<FamilyProfileExtendedContract.View> viewReference;
    private CoreChildRegisterInteractor childRegisterInteractor;


    public CoreFamilyProfilePresenter(FamilyProfileExtendedContract.View view, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(view, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        viewReference = new WeakReference<>(view);
        childRegisterInteractor = new CoreChildRegisterInteractor();
        childProfileModel = new CoreChildProfileModel(familyName);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startChildForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Timber.e(e);
            getView().displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    @Override
    public FamilyProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        JSONObject form = CoreJsonFormUtils.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyDetailsRegister(), getView().getApplicationContext(), client, Utils.metadata().familyRegister.updateEventType);
        try {
            getView().startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (viewReference.get() != null) {
            viewReference.get().updateHasPhone(hasPhone);
        }
    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            childRegisterInteractor.getNextUniqueId(triple, this, familyBaseEntityId);
            return;
        }

        JSONObject form = childProfileModel.getFormAsJson(formName, entityId, currentLocationId, familyBaseEntityId);
        getView().startFormActivity(form);
    }


    @Override
    public void saveChildRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, CoreChildRegisterContract.InteractorCallBack callBack) {
        childRegisterInteractor.saveRegistration(pair, jsonString, isEditMode, this);
    }


    @Override
    public void saveChildForm(String jsonString, boolean isEditMode) {
        try {

            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            Pair<Client, Event> pair = getChildRegisterModel().processRegistration(jsonString);
            if (pair == null) {
                return;
            }
            saveChildRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected abstract CoreChildRegisterModel getChildRegisterModel();

    @Override
    public void verifyHasPhone() {
        ((CoreFamilyProfileInteractor) interactor).verifyHasPhone(familyBaseEntityId, this);
    }

    @Override
    public String saveChwFamilyMember(Context context, String jsonString) {
        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = model.processMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return null;
            }
            FamilyMember familyMember = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, familyBaseEntityId);
            Event eventMember = familyEventClient.getEvent();
            eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.EverSchool.CODE, "",
                    toList(CoreJsonFormUtils.getEverSchoolOptions(context).get(familyMember.getEverSchool())), toList(familyMember.getEverSchool()), null, CoreConstants.JsonAssets.FAMILY_MEMBER.EVER_SCHOOL));

            eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.SchoolLevel.CODE, "",
                    toList(CoreJsonFormUtils.getSchoolLevels(context).get(familyMember.getSchoolLevel())), toList(familyMember.getSchoolLevel()), null, CoreConstants.JsonAssets.FAMILY_MEMBER.SCHOOL_LEVEL));


            interactor.saveRegistration(familyEventClient, jsonString, false, this);
            return familyEventClient.getClient().getBaseEntityId();
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID) {

        boolean res = false;
        try {
            FamilyMember member = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, entityID);
            if (member != null && member.getPrimaryCareGiver()) {
                LocationPickerView lpv = new LocationPickerView(context);
                lpv.init();
                res = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }

    public boolean isAncMember(String baseEntityId) {
        return AncDao.isANCMember(baseEntityId);
    }

    public HashMap<String, String> getAncFamilyHeadNameAndPhone(String baseEntityId) {
        return getAncRegisterRepository().getFamilyNameAndPhone(baseEntityId);
    }

    public CommonPersonObject getAncCommonPersonObject(String baseEntityId) {
        return getAncRegisterRepository().getAncCommonPersonObject(baseEntityId);
    }

    public CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        return getPncRegisterRepository().getPncCommonPersonObject(baseEntityId);
    }

    public boolean isPncMember(String baseEntityId) {
        return PNCDao.isPNCMember(baseEntityId);
    }

    private AncRegisterRepository getAncRegisterRepository() {
        return CoreChwApplication.ancRegisterRepository();
    }

    private PncRegisterRepository getPncRegisterRepository() {
        return CoreChwApplication.pncRegisterRepository();
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        super.refreshProfileTopSection(client);

        if (client == null || client.getColumnmaps() == null) {
            return;
        }
        String eventDateValue = Utils.getValue(client.getColumnmaps(), ChwDBConstants.EVENT_DATE, true);
        String eventDate = eventDateValue != null && !eventDateValue.equals("") ? eventDateValue.substring(0,10) : "";

        getView().setEventDate(eventDate);
    }
}
