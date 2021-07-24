package com.bluecodeltd.ecap.chw.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface IdentificationRegisterFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void initView(IdentificationRegisterFragmentContract.View view);

        IdentificationRegisterFragmentContract.View getView();
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
