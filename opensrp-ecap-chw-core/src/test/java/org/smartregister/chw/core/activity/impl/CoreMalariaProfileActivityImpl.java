package org.smartregister.chw.core.activity.impl;

import androidx.annotation.NonNull;

import org.mockito.Mockito;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreMalariaProfileActivity;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;

import timber.log.Timber;

public class CoreMalariaProfileActivityImpl extends CoreMalariaProfileActivity {
    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return CoreFamilyProfileActivity.class;
    }

    @Override
    protected void removeMember() {
        Timber.v("remove member");
    }

    @NonNull
    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return Mockito.mock(FamilyOtherMemberActivityPresenterImpl.class);
    }

    @Override
    public void setProfileImage(String s, String s1) {
        Timber.v("set profile image");
    }

    @Override
    public void setProfileDetailThree(String s) {
        Timber.v("set profile detail");
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        Timber.v("toggle family head");
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        Timber.v("toggle primary caregiver");
    }

    @Override
    public void refreshList() {
        Timber.v("refresh list");
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        Timber.v("update has phone");
    }

    @Override
    public void setFamilyServiceStatus(String status) {
        Timber.v("set family service status");
    }

    @Override
    public void verifyHasPhone() {
        Timber.v("verify has phone");
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        Timber.v("notify has phone");
    }
}
