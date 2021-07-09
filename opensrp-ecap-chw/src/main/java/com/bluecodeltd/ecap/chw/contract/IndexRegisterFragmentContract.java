package com.bluecodeltd.ecap.chw.contract;

import android.content.Context;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface IndexRegisterFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void sayHello(Context context);

        void initView(View view);

        View getView();
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
