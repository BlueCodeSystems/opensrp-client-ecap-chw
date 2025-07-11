package org.smartregister.chw.core.presenter;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;

public class CoreAllClientsRegisterPresenter extends BaseOpdRegisterActivityPresenter {

    public CoreAllClientsRegisterPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void onNoUniqueId() {
        //implement
    }

    @Override
    public void onRegistrationSaved(boolean b) {
        //implement
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {
        //implement
    }

    @Override
    public void saveForm(String s, @NonNull RegisterParams registerParams) {
        //implement
    }
}
