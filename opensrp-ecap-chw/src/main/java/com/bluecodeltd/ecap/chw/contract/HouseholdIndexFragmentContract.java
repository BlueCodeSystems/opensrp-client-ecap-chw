package com.bluecodeltd.ecap.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface HouseholdIndexFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void initView(HouseholdIndexFragmentContract.View view);

        HouseholdIndexFragmentContract.View getView();
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
