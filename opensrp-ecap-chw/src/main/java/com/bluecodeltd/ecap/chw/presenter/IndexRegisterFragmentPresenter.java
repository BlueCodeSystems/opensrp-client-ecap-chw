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
    public String getDefaultSortQuery() {
        return "ec_client_index.first_name ASC ";
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String children = Constants.EcapClientTable.EC_CLIENT_INDEX;

        String countSelect = "SELECT COUNT(*) FROM " + children + "WHERE case_status IS NOT NULL AND case_status != 3 AND deleted != '1'";
        String mainSelect = "SELECT *, COALESCE(ec_client_index.id,ec_client_index.unique_id,'') AS _id, ec_client_index.relationalid AS relational_id, ec_client_index.facility AS residence, first_name AS adolescent_first_name,last_name As adolescent_last_name, gender as adolescent_gender FROM ec_client_index ";

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
