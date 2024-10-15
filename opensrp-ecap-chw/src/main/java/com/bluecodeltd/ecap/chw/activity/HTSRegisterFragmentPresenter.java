package com.bluecodeltd.ecap.chw.activity;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

public class HTSRegisterFragmentPresenter implements IndexRegisterFragmentContract.Presenter {

    private IndexRegisterFragmentContract.View view;

    @Override
    public void initView(IndexRegisterFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public IndexRegisterFragmentContract.View getView() {
        return this.view;
    }


    @Override
    public String getDefaultSortQuery() {
        return "ec_hiv_testing_service.first_name ASC ";
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String hivTestingService = Constants.EcapClientTable.EC_HIV_TESTING_SERVICE;

        String countSelect = "SELECT COUNT(*) FROM " + hivTestingService + " WHERE client_number IS NOT NULL";
        String mainSelect = "SELECT *, ec_hiv_testing_service.client_number as _id,ec_hiv_testing_service.health_facility AS facility, first_name,gender FROM ec_hiv_testing_service";

        getView().initializeQueryParams(Constants.EcapClientTable.EC_HIV_TESTING_SERVICE, countSelect, mainSelect);
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
