package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IdentificationRegisterFragmentContract;


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
