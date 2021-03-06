package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.interactor.CoreFamilyRemoveMemberInteractor;

public class FamilyRemoveMemberInteractor extends CoreFamilyRemoveMemberInteractor {
    private static FamilyRemoveMemberInteractor instance;

    private FamilyRemoveMemberInteractor() {
        setCoreChwApplication();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = ChwApplication.getInstance();
    }

    public static FamilyRemoveMemberInteractor getInstance() {
        if (instance == null) {
            instance = new FamilyRemoveMemberInteractor();
        }
        return instance;
    }

}