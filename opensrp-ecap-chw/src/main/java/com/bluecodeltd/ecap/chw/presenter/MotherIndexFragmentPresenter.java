package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;

public class MotherIndexFragmentPresenter implements MotherIndexFragmentContract.Presenter{

    private MotherIndexFragmentContract.View view;

    @Override
    public void initView(MotherIndexFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public MotherIndexFragmentContract.View getView() {
        return this.view;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String mothers = Constants.EcapClientTable.EC_MOTHER_INDEX;

        String countSelect = "SELECT COUNT(*) FROM " + mothers;
        String mainSelect = "SELECT *, base_entity_id as _id FROM " + mothers;

        getView().initializeQueryParams(Constants.EcapClientTable.EC_MOTHER_INDEX, countSelect, mainSelect);
        getView().initializeAdapter();

        // countExecute() runs a synchronous COUNT query against the SQLCipher-encrypted DB on
        // whatever thread calls it; called here from Fragment.onResume() it can block the UI
        // thread long enough to ANR. Run it off the main thread instead.
        Threading.io(() -> {
            MotherIndexFragmentContract.View countingView = getView();
            if (countingView != null) {
                countingView.countExecute();
            }
            Threading.main(() -> {
                MotherIndexFragmentContract.View view = getView();
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
