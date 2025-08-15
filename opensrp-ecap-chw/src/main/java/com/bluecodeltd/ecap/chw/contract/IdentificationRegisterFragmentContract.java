package com.bluecodeltd.ecap.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface IdentificationRegisterFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void initView(View view);

        View getView();
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
