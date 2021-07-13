package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

public class IndexRegisterInteractor implements IndexRegisterContract.Interactor {

    private final IndexRegisterContract.Presenter presenter;

    public IndexRegisterInteractor(IndexRegisterContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void saveRegistration(ChildIndexEventClient childIndexEventClient) {
        //TODO Save event and client json and invoke client processor
        presenter.onRegistrationSaved();
    }
}
