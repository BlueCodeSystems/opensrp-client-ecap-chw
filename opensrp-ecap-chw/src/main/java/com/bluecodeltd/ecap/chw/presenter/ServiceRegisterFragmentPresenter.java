package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;

public class ServiceRegisterFragmentPresenter implements IndexRegisterFragmentContract.Presenter {

    private final ServiceRegister register;
    private IndexRegisterFragmentContract.View view;

    public ServiceRegisterFragmentPresenter(ServiceRegister register) {
        this.register = register;
    }

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
        return register.table + ".last_interacted_with DESC ";
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {
        String table = register.table;
        // These tables are not full-text indexed, and RecyclerViewFragment only applies mainCondition
        // on the FTS path, so the "not deleted" condition lives in the selects themselves; the
        // fragment appends search terms to them as "AND (...)".
        String notDeleted = " WHERE (" + table + ".delete_status IS NULL OR " + table + ".delete_status != '1') ";
        String countSelect = "SELECT COUNT(*) FROM " + table + notDeleted;
        String mainSelect = "SELECT *, " + table + ".base_entity_id as _id FROM " + table + notDeleted;

        getView().initializeQueryParams(table, countSelect, mainSelect);
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
