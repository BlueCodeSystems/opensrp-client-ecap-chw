package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

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
        String population = Constants.EcapClientTable.EC_POPULATION;

        String countSelect = "SELECT COUNT(*) FROM " + children;
        String mainSelect = "SELECT ec_sub_population.*, ec_client_index.*, ec_client_index.id as _id, ec_client_index.relationalid AS relational_id, ec_client_index.health_facility AS residence FROM ec_client_index JOIN ec_sub_population ON ec_client_index.base_entity_id = ec_sub_population.base_entity_id";

        getView().initializeQueryParams(Constants.EcapClientTable.EC_CLIENT_INDEX, countSelect, mainSelect);
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
