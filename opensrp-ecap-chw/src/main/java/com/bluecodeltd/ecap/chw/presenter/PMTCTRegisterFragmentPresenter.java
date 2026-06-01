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
        return "last_interacted_with DESC ";
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {
        String pmtct = Constants.EcapClientTable.EC_MOTHER_PMTCT;
        // Deduplicate rows that share the same pmtct_id or household_id by keeping only the MAX(id) row.
        // BaseRegisterFragment appends mainCondition and ORDER BY from getMainCondition()/getDefaultSortQuery().
        String dedupeKey = "COALESCE(NULLIF(TRIM(pmtct_id),''), NULLIF(TRIM(household_id),''), base_entity_id)";
        String countSelect = "SELECT COUNT(*) FROM " + pmtct + " WHERE id IN " +
                "(SELECT MAX(id) FROM " + pmtct + " GROUP BY " + dedupeKey + ") ";
        String mainSelect = "SELECT *, ec_pmtct_mother.pmtct_id as _id FROM ec_pmtct_mother " +
                "WHERE id IN (SELECT MAX(id) FROM ec_pmtct_mother GROUP BY " + dedupeKey + ") ";

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
