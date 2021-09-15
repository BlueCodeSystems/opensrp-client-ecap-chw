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

        String households = Constants.EcapClientTable.EC_FAMILY;

        String countSelect = "SELECT COUNT(*) FROM " + households;
        String mainSelect = "SELECT id AS _id, first_name, last_name, health_facility AS residence FROM " + households;

        getView().initializeQueryParams(Constants.EcapClientTable.EC_FAMILY, countSelect, mainSelect);
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
