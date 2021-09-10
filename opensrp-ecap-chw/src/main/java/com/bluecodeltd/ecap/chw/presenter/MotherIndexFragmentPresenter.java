package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

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
        String mainSelect = "SELECT *, id as _id, mother_first_name AS first_name, mother_surname AS last_name, health_facility AS residence FROM " + mothers;

        getView().initializeQueryParams(Constants.EcapClientTable.EC_MOTHER_INDEX, countSelect, mainSelect);
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
