package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;

import org.smartregister.chw.core.utils.CoreConstants;

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

        String countSelect = "SELECT COUNT(1) FROM ec_client_index";
        String mainSelect = "SELECT id as _id, relationalid, relationalid as relational_id, first_name, last_name, residence FROM ec_client_index";

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY, countSelect, mainSelect);
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
