package com.bluecodeltd.ecap.chw.contract;

import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import com.bluecodeltd.ecap.chw.model.ReferralTypeModel;

import java.util.List;

public interface AncMemberProfileContract extends org.smartregister.chw.core.contract.AncMemberProfileContract {
    interface Presenter{
        void referToFacility();
    }

    interface View extends OnRetrieveNotifications {
        List<ReferralTypeModel> getReferralTypeModels();
    }
}
