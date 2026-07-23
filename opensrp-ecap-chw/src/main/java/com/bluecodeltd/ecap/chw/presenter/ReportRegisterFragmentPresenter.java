package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.ReportRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.util.Constants;

public class ReportRegisterFragmentPresenter implements ReportRegisterFragmentContract.Presenter {

    private ReportRegisterFragmentContract.View view;

    @Override
    public void initView(ReportRegisterFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public ReportRegisterFragmentContract.View getView() {
        return view;
    }

    @Override
    public void processViewConfigurations() {
        // No-op
    }

    @Override
    public void initializeQueries(String mainCondition) {
        getView().initializeAdapter();
    }

    @Override
    public void startSync() {
        // No-op
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // No-op
    }
}
