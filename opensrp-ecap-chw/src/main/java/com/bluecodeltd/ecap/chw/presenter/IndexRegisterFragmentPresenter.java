package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.family.util.DBConstants;

public class IndexRegisterFragmentPresenter implements IndexRegisterFragmentContract.Presenter {

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
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String children = Constants.EcapClientTable.EC_CLIENT_INDEX;

        String countSelect = "SELECT COUNT(*) FROM " + children + "WHERE case_status IS NOT NULL";
        String mainSelect = "SELECT *, ec_client_index.id as _id, ec_client_index.relationalid AS relational_id, ec_client_index.health_facility AS residence FROM ec_client_index WHERE case_status IS NOT NULL ORDER BY first_name ASC";

        getView().initializeQueryParams(Constants.EcapClientTable.EC_CLIENT_INDEX, countSelect, mainSelect);
        getView().initializeAdapter();
        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public String getDefaultSortQuery() {
        return  " first_name ASC ";
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
