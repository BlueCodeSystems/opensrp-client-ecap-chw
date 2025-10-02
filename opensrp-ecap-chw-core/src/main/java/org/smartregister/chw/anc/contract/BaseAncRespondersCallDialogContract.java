package org.smartregister.chw.anc.contract;

import android.content.Context;

public interface BaseAncRespondersCallDialogContract {
    interface View extends BaseAncWomanCallDialogContract.View {
        Context getCurrentContext();

        void setUpPosition();
    }

    interface Model {

        void setId(String id);

        String getId();

        void setResponderName(String responderName);

        void setResponderPhoneNumber(String responderPhoneNumber);

        void setResponderLocation(String responderLocation);

        String getResponderName();

        String getResponderPhoneNumber();

        String getResponderLocation();

        boolean isAncResponder();

        void setIsAncResponder(boolean isAncResponder);
    }
}
