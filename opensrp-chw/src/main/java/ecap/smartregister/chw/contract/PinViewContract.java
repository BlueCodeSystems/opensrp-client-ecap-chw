package ecap.smartregister.chw.contract;

import ecap.smartregister.chw.pinlogin.PinLogger;

public interface PinViewContract {

    interface Controller {

        void navigateToFragment(String destinationFragment);

        void startPasswordLogin();

        void startHomeActivity();

        PinLogger getPinLogger();
    }
}
