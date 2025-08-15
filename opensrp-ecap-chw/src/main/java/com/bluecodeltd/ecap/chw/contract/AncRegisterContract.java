package com.bluecodeltd.ecap.chw.contract;

import org.apache.commons.lang3.tuple.Triple;

/**
 * Created by keyamn on 12/11/2018.
 */
public interface AncRegisterContract {


    interface Interactor {

        void getNextUniqueId(Triple<String, String, String> triple, InteractorCallBack callBack, String familyID);

    }

    interface InteractorCallBack {

        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId);

        void onRegistrationSaved(boolean isEdit);

    }


}
