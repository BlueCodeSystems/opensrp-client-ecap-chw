package com.bluecodeltd.ecap.chw.presenter;

import android.content.Context;

import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.util.Utils;

public class IndexRegisterFragmentPresenter implements IndexRegisterFragmentContract.Presenter {

    private IndexRegisterFragmentContract.View view;

    @Override
    public void sayHello(Context context) {
        Utils.showShortToast(context, "Hey there");
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
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

        String countSelect = "SELECT COUNT(1) FROM ec_family";
        String mainSelect = "SELECT id as _id, relationalid, relationalid as relational_id, first_name, landmark FROM ec_family";

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
