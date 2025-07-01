package org.smartregister.chw.core.activity.impl;

import android.content.Context;

import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;

public class FamilyOtherMemberActivityPresenterImpl extends CoreFamilyOtherMemberActivityPresenter {
    public FamilyOtherMemberActivityPresenterImpl(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected CoreFamilyProfileInteractor getFamilyProfileInteractor() {
        return null;
    }

    @Override
    protected FamilyProfileContract.Model getFamilyProfileModel(String familyName) {
        return null;
    }

    @Override
    protected void setProfileInteractor() {
        // Do nothing
    }

    @Override
    public void updateFamilyMember(Context context, String jsonString, boolean isIndependent) {
        // Do nothing
    }
}
