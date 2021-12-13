package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.activity.MotherIndexActivity;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.interactor.MotherIndexInteractor;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.model.MotherIndexModel;

import org.json.JSONException;
import org.smartregister.domain.FetchStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MotherIndexPresenter implements MotherIndexContract.Presenter {

    private MotherIndexContract.View view;
    private MotherIndexContract.Model model;
    private MotherIndexContract.Interactor interactor;

    private WeakReference<MotherIndexContract.View> activityWeakReference;

    public MotherIndexPresenter(MotherIndexContract.View view) {
        this.view = view;
        activityWeakReference = new WeakReference<>(view);
        interactor = new MotherIndexInteractor(this);
        setModel(new MotherIndexModel());
    }

    public void setModel(MotherIndexContract.Model model) {
        this.model = model;
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
    public void saveForm(String jsonString, boolean isEditMode) throws JSONException {

       try {

            view.toggleDialogVisibility(false);


            ArrayList<EventClient> eventClients = model.processRegistration(jsonString);

            if (eventClients == null) {
                return;
            }

            interactor.saveRegistration(eventClients, isEditMode);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onRegistrationSaved() {
        getView().toggleDialogVisibility(false);
        getView().refreshList(FetchStatus.fetched);
    }

    @Override
    public MotherIndexActivity getView() {
        if (activityWeakReference.get() != null) {
            return (MotherIndexActivity) view;
        }
        return null;
    }
}
