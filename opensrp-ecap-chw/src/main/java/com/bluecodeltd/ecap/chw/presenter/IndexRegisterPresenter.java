package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;

import org.apache.commons.lang3.tuple.Triple;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class IndexRegisterPresenter implements IndexRegisterContract.Presenter {

    private IndexRegisterContract.View view;
    private IndexRegisterContract.Model model;
    private IndexRegisterContract.Interactor interactor;

    private WeakReference<IndexRegisterContract.View> activityWeakReference;

    public IndexRegisterPresenter(IndexRegisterContract.View view) {
        this.view = view;
        activityWeakReference = new WeakReference<>(view);
        interactor = new IndexRegisterInteractor(this);
        setModel(new IndexRegisterModel());
    }

    public void setModel(IndexRegisterContract.Model model) {
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


            ChildIndexEventClient childIndexEventClient = model.processRegistration(jsonString);

            if (childIndexEventClient == null) {
                return;
            }

            interactor.saveRegistration(childIndexEventClient, isEditMode);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onRegistrationSaved() {
        view.toggleDialogVisibility(false);
    }

    @Override
    public IndexRegisterActivity getView() {
        if (activityWeakReference.get() != null) {
            return (IndexRegisterActivity) view;
        }
        return null;
    }
}
