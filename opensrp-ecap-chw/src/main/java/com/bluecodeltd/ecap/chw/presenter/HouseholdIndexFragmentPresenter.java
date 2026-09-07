package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.HouseholdIndexFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;

public class HouseholdIndexFragmentPresenter implements HouseholdIndexFragmentContract.Presenter{

    private HouseholdIndexFragmentContract.View view;

    @Override
    public void initView(HouseholdIndexFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public HouseholdIndexFragmentContract.View getView() {
        return this.view;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {


        String countSelect = "SELECT COUNT(*) FROM ec_household";
        String mainSelect = "SELECT ec_household.*, ec_household.household_id AS hid, ec_household.id AS _id FROM ec_household";

        getView().initializeQueryParams("ec_household", countSelect, mainSelect);
        getView().initializeAdapter();

        // countExecute() runs a synchronous COUNT(*) query against the SQLCipher-encrypted DB.
        // Called here from Fragment.onResume() -> renderView(), it used to run on the UI thread
        // and could block long enough (e.g. under DB write contention) to trigger an ANR. Run it
        // off the main thread; only the LoaderManager-driven work needs to stay on the UI thread.
        Threading.io(() -> {
            HouseholdIndexFragmentContract.View countingView = getView();
            if (countingView != null) {
                countingView.countExecute();
            }
            Threading.main(() -> {
                HouseholdIndexFragmentContract.View view = getView();
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
