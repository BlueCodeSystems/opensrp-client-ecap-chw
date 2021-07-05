package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.pinlogin.PinLogger;

public interface PinViewContract {

    interface Controller {

        void navigateToFragment(String destinationFragment);

        void startPasswordLogin();

        void startHomeActivity();

        PinLogger getPinLogger();
    }
}
