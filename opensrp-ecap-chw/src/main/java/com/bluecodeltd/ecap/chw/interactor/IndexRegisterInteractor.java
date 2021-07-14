package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

import org.smartregister.domain.FetchStatus;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.AppExecutors;

import java.util.List;

public class IndexRegisterInteractor implements IndexRegisterContract.Interactor {

    private final IndexRegisterContract.Presenter presenter;

    public AppExecutors appExecutors;

    public IndexRegisterInteractor(IndexRegisterContract.Presenter presenter) {
        this.presenter = presenter;
    }

  
    @Override
    public boolean saveRegistration(List<ChildIndexEventClient> childEventClientList, String jsonString, boolean isEditMode, IndexRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final boolean isSaved = saveRegistration(childEventClientList, jsonString, isEditMode, callBack);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode, isSaved, childEventClientList);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
        return false;
    }
    @Override
    public void onRegistrationSaved(boolean editMode, boolean isSaved, ChildIndexEventClient childEventClient) {
       /* if (this.getView() != null) {
            this.getView().refreshList(FetchStatus.fetched);
            this.getView().hideProgressDialog();
        }*/

    }
}
