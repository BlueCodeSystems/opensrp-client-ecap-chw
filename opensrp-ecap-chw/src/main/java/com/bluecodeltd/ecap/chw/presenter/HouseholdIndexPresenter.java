package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.activity.HouseholdIndexActivity;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.interactor.HouseholdIndexInteractor;
import com.bluecodeltd.ecap.chw.model.HouseholdIndexEventClient;
import com.bluecodeltd.ecap.chw.model.HouseholdIndexModel;

import org.smartregister.domain.FetchStatus;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class HouseholdIndexPresenter implements HouseholdIndexContract.Presenter{

    private HouseholdIndexContract.View view;
    private HouseholdIndexContract.Model model;
    private HouseholdIndexContract.Interactor interactor;

    private WeakReference<HouseholdIndexContract.View> activityWeakReference;

    public HouseholdIndexPresenter (HouseholdIndexContract.View view) {
        this.view = view;
        activityWeakReference = new WeakReference<>(view);
        interactor = new HouseholdIndexInteractor(this);
        setModel(new HouseholdIndexModel());
    }

    public void setModel(HouseholdIndexContract.Model model) {
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
    public void saveForm(String jsonString, boolean isEditMode) {

        try {

            view.toggleDialogVisibility(true);

            HouseholdIndexEventClient householdIndexEventClient = model.processRegistration(jsonString);

            if (householdIndexEventClient == null) {
                return;
            }

            interactor.saveRegistration(householdIndexEventClient, isEditMode);

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
    public HouseholdIndexActivity getView() {
        if (activityWeakReference.get() != null) {
            return (HouseholdIndexActivity) view;
        }
        return null;
    }
}
