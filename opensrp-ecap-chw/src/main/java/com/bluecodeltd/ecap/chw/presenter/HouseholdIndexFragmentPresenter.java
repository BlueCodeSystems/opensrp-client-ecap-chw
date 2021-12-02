package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.HouseholdIndexFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

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

        String households = Constants.EcapClientTable.EC_HOUSEHOLD;

        String countSelect = "SELECT COUNT(*) FROM ec_household";
        String mainSelect = "SELECT *, id AS _id, health_facility AS residence FROM ec_household WHERE caregiver_name IS NOT NULL";

        getView().initializeQueryParams(Constants.EcapClientTable.EC_HOUSEHOLD, countSelect, mainSelect);
        getView().initializeAdapter();
        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
