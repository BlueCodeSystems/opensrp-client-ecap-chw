package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.util.List;

public class IndexRegisterPresenter implements IndexRegisterContract.Presenter {

    private IndexRegisterContract.View view;

    private IndexRegisterContract.Interactor interactor;

    public IndexRegisterPresenter(IndexRegisterContract.View view) {
        this.view = view;
        interactor = new IndexRegisterInteractor(this);
    }

    @Override
    public void registerViewConfigurations(List<String> list) {
        //Overridden
    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {
        //Overridden
    }

    @Override
    public void onDestroy(boolean b) {
        //Overridden
    }

    @Override
    public void updateInitials() {
        //Overridden
    }

    @Override
    public void saveForm(String json) {
        view.toggleDialogVisibility(true);
        Event event = null;
        Client client = null;
        ChildIndexEventClient childIndexEventClient = new ChildIndexEventClient(event, client);
        //TODO obtain the event and client from the json string
        interactor.saveRegistration(childIndexEventClient);
    }

    @Override
    public void onRegistrationSaved() {
        view.toggleDialogVisibility(false);
    }
}
