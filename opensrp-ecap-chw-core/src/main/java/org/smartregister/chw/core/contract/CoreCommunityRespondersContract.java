package org.smartregister.chw.core.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRespondersCallDialogContract;
import org.smartregister.chw.core.model.CommunityResponderModel;

import java.util.List;

public interface CoreCommunityRespondersContract {

    interface View {

        Presenter presenter();

        Context getContext();

        void startEditFormActivity(CommunityResponderModel communityResponderModel, String baseEntityID) throws Exception;

        void startFormActivity(JSONObject jsonForm);

        void showPopUpMenu(android.view.View view, CoreCommunityRespondersContract.Model model);

        void refreshCommunityResponders(List<CommunityResponderModel> communityResponderModelList);

    }

    interface Presenter {

        View getView();

        void fetchAllCommunityResponders();

        void updateCommunityResponders(List<CommunityResponderModel> communityResponderModelList);

        void addCommunityResponder(String jsonString);

        void removeCommunityResponder(String baseEntityId);
    }

    interface Model extends BaseAncRespondersCallDialogContract.Model {

    }

    interface Interactor {

        void addCommunityResponder(String jsonString, InteractorCallBack callBack);

        void fetchAllCommunityResponders(InteractorCallBack callBack);

        void removeCommunityResponder(String baseEntityId, InteractorCallBack callBack);

    }

    interface InteractorCallBack {
        void updateCommunityRespondersList(List<CommunityResponderModel> communityResponderModelList);
    }
}
