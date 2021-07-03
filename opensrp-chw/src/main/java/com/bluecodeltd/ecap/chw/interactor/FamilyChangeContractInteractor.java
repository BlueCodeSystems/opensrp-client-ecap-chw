package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;

public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = ChwApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new FamilyChangeContractInteractorFlv();
    }
}
