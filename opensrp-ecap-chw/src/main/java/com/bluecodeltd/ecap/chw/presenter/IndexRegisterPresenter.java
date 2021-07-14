package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

import timber.log.Timber;

public class IndexRegisterPresenter implements IndexRegisterContract.Presenter, IndexRegisterContract.InteractorCallBack {

    private IndexRegisterContract.View view;
    private IndexRegisterContract.Model model;
    private IndexRegisterContract.Interactor interactor;

    IndexRegisterActivity currentView = (IndexRegisterActivity) getView();

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
    public void saveForm(String jsonString, boolean isEditMode) {

        try {

            view.toggleDialogVisibility(true);


            ChildIndexEventClient childEventClientList = model.processRegistration(jsonString);

            if (childEventClientList == null || childEventClientList.isEmpty()) {
                return;
            }

            interactor.saveRegistration(childEventClientList, jsonString, isEditMode, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onRegistrationSaved() {
        view.toggleDialogVisibility(false);

    }

    @Override
    public IndexRegisterContract.View getView() {
        return null;
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {
       // Utils.showShortToast(this, "No Unique ID Found");
        //TODO : NO Unique ID toast message

    }

    @Override
    public void onRegistrationSaved(boolean isEditMode, boolean isSaved, List<ChildIndexEventClient> familyEventClientList) {

    }
}
