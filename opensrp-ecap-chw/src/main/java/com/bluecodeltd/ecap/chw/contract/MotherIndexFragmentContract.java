package com.bluecodeltd.ecap.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface MotherIndexFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void initView(MotherIndexFragmentContract.View view);

        MotherIndexFragmentContract.View getView();
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
