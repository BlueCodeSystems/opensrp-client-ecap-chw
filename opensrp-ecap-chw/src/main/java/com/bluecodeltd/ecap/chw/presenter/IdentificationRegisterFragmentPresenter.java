package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IdentificationRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Threading;


public class IdentificationRegisterFragmentPresenter implements IdentificationRegisterFragmentContract.Presenter {

    private IdentificationRegisterFragmentContract.View view;

    @Override
    public void initView(IdentificationRegisterFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public IdentificationRegisterFragmentContract.View getView() {
        return this.view;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String countSelect = "80";
        String mainSelect = "SELECT id as _id, relationalid, relationalid as relational_id, first_name, last_name, residence FROM ec_client_index";

        getView().initializeQueryParams("ec_client_index", countSelect, mainSelect);
        getView().initializeAdapter();

        // countExecute() runs a synchronous COUNT query against the SQLCipher-encrypted DB on
        // whatever thread calls it; called here from Fragment.onResume() it can block the UI
        // thread long enough to ANR. Run it off the main thread instead.
        Threading.io(() -> {
            IdentificationRegisterFragmentContract.View countingView = getView();
            if (countingView != null) {
                countingView.countExecute();
            }
            Threading.main(() -> {
                IdentificationRegisterFragmentContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.setTotalPatients();
                view.filterandSortInInitializeQueries();
            });
        });
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }

}
