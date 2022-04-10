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


        String countSelect = "SELECT COUNT(*) FROM ec_household WHERE household_id IS NOT NULL";
        String mainSelect = "SELECT ec_household.*, ec_household.household_id AS hid, ec_household.id AS _id FROM ec_household WHERE ec_household.caregiver_name IS NOT NULL AND ec_household.household_id IS NOT NULL";

        getView().initializeQueryParams("ec_household", countSelect, mainSelect);
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
