package com.bluecodeltd.ecap.chw.fragment;

import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.AllClientsRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.AncRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.FamilyRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.IndividualProfileRemoveActivity;
import org.smartregister.chw.core.activity.CoreAncRegisterActivity;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.fragment.CoreFamilyProfileChangeDialog;
import org.smartregister.chw.core.fragment.CoreIndividualProfileRemoveFragment;
import org.smartregister.chw.core.fragment.FamilyRemoveMemberConfirmDialog;
import org.smartregister.chw.core.model.ChildModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import com.bluecodeltd.ecap.chw.model.FamilyRemoveMemberModel;
import com.bluecodeltd.ecap.chw.presenter.FamilyRemoveMemberPresenter;
import com.bluecodeltd.ecap.chw.provider.FamilyRemoveMemberProvider;
import com.bluecodeltd.ecap.chw.schedulers.ChwScheduleTaskExecutor;
import com.bluecodeltd.ecap.chw.service.ChildAlertService;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;

import java.util.Date;
import java.util.Set;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;

public class IndividualProfileRemoveFragment extends CoreIndividualProfileRemoveFragment {

    public static IndividualProfileRemoveFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        IndividualProfileRemoveFragment fragment = new IndividualProfileRemoveFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setRemoveMemberProvider(Set visibleColumns, String familyHead, String primaryCaregiver) {
        this.removeMemberProvider = new FamilyRemoveMemberProvider(familyBaseEntityId, this.getActivity(),
                this.commonRepository(), visibleColumns, null, null, familyHead, primaryCaregiver);
    }

    @Override
    protected void setPresenter(String familyHead, String primaryCareGiver) {
        this.presenter = new FamilyRemoveMemberPresenter(this, new FamilyRemoveMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
    }

    @Override
    public void onMemberRemoved(String removalType) {
        if (getActivity() != null) {
            if (CoreConstants.EventType.REMOVE_FAMILY.equalsIgnoreCase(removalType)) {
                Intent intent = new Intent(getActivity(), FamilyRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                if (getActivity() != null) {
                    if (getActivity() instanceof IndividualProfileRemoveActivity) {
                        getActivity().finish();
                        if (familyBaseEntityId != null) { //Independent clients don't belong to family
                            CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(familyBaseEntityId);
                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                                    personObject.getDetails(), "");
                            pClient.setColumnmaps(personObject.getColumnmaps());

                            goToPatientDetailActivity(pClient);
                        }
                    }
                }
            }

            String baseEntityId = pc.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID);
            for (ChildModel child : PNCDao.childrenForPncWoman(baseEntityId)) {
                ChwScheduleTaskExecutor.getInstance().execute(child.getBaseEntityId(), CoreConstants.EventType.CHILD_HOME_VISIT, new Date());
                ChildAlertService.updateAlerts(child.getBaseEntityId());
            }
        }
    }

    protected void goToPatientDetailActivity(CommonPersonObjectClient patient) {
        String entityType = patient.getColumnmaps().get(DBConstants.KEY.ENTITY_TYPE);
        Intent intent;
        if (CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT.equalsIgnoreCase(entityType)) {
            intent = new Intent(getActivity(), AllClientsRegisterActivity.class);
        } else {
            intent = new Intent(getActivity(), Utils.metadata().profileActivity);
            intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, patient.getCaseId());
            intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false));
            intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false));
            intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false));
            intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false));
            intent.putExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false);
        }
        startActivity(intent);
    }


    @Override
    public void onEveryoneRemoved() {
        if (getActivity() != null && getActivity() instanceof IndividualProfileRemoveActivity) {
            IndividualProfileRemoveActivity p = (IndividualProfileRemoveActivity) getActivity();
            p.onRemoveMember();
        }
    }

    @Override
    protected Class<? extends CoreFamilyRegisterActivity> getFamilyRegisterActivityClass() {
        return FamilyRegisterActivity.class;
    }

    @Override
    protected CoreFamilyProfileChangeDialog getChangeFamilyCareGiverDialog() {
        return FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                CoreConstants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER);
    }

    @Override
    protected CoreFamilyProfileChangeDialog getChangeFamilyHeadDialog() {
        return FamilyProfileChangeDialog.newInstance(getContext(), familyBaseEntityId,
                CoreConstants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY);
    }

    @Override
    public void confirmRemove(final JSONObject form) {
        if (StringUtils.isNotBlank(memberName) && getFragmentManager() != null) {
            FamilyRemoveMemberConfirmDialog dialog = FamilyRemoveMemberConfirmDialog.newInstance(
                    String.format(getString(org.smartregister.chw.core.R.string.confirm_remove_text), memberName)
            );
            dialog.show(getFragmentManager(), FamilyRemoveMemberFragment.DIALOG_TAG);
            dialog.setOnRemove(() -> {
                getPresenter().processRemoveForm(form);
            });
            dialog.setOnRemoveActivity(() -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    protected String getRemoveFamilyMemberDialogTag() {
        return FamilyRemoveMemberFragment.DIALOG_TAG;
    }

    @Override
    protected Class<? extends CoreAncRegisterActivity> getAncRegisterActivityClass() {
        return AncRegisterActivity.class;
    }

}
