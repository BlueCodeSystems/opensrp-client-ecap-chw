package org.smartregister.chw.core.presenter;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
// TODO: Add OPD module dependency or implement alternative
// import org.smartregister.opd.contract.OpdRegisterActivityContract;
// import org.smartregister.opd.pojo.RegisterParams;
// import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;

public class CoreAllClientsRegisterPresenter {

    public CoreAllClientsRegisterPresenter() {
        // Basic constructor - implement when OPD module is available
    }

    public void onNoUniqueId() {
        //implement
    }

    public void onRegistrationSaved(boolean b) {
        //implement
    }

    public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {
        //implement
    }

    public void saveForm(String s, @NonNull Object registerParams) {
        //implement
    }
}
