package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

public class PMTCTRegisterFragmentPresenter implements IndexRegisterFragmentContract.Presenter {

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
        return "ec_pmtct_mother.caregiver_name ASC ";
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String pmtct = Constants.EcapClientTable.EC_MOTHER_PMTCT;

        String countSelect = "SELECT COUNT(*) FROM " + pmtct + " WHERE is_closed IS NOT NULL (pmtct_id IS NOT NULL AND first_name IS NOT NULL)";
        String mainSelect = "SELECT *, ec_pmtct_mother.pmtct_id as _id, ec_pmtct_mother.facility FROM ec_pmtct_mother WHERE pmtct_id IS NOT NULL AND first_name IS NOT NULL";

        getView().initializeQueryParams(Constants.EcapClientTable.EC_MOTHER_PMTCT, countSelect, mainSelect);
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
