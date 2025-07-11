package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;
import org.smartregister.chw.core.interactor.CoreCommunityRespondersInteractor;
import org.smartregister.chw.core.model.CommunityResponderModel;

import java.lang.ref.WeakReference;
import java.util.List;

public class CoreCommunityRespondersPresenter implements CoreCommunityRespondersContract.Presenter, CoreCommunityRespondersContract.InteractorCallBack {

    private WeakReference<CoreCommunityRespondersContract.View> viewWeakReference;
    private CoreCommunityRespondersContract.Interactor interactor;


    public CoreCommunityRespondersPresenter(CoreCommunityRespondersContract.View view) {
        this.viewWeakReference = new WeakReference<>(view);
        interactor = new CoreCommunityRespondersInteractor();
    }

    @Override
    public CoreCommunityRespondersContract.View getView() {
        if (viewWeakReference != null)
            return viewWeakReference.get();

        return null;
    }

    @Override
    public void fetchAllCommunityResponders() {
        interactor.fetchAllCommunityResponders(this);
    }

    @Override
    public void updateCommunityResponders(List<CommunityResponderModel> communityResponderModelList) {
        getView().refreshCommunityResponders(communityResponderModelList);
    }

    @Override
    public void addCommunityResponder(String jsonString) {
        interactor.addCommunityResponder(jsonString, this);
    }

    @Override
    public void removeCommunityResponder(String baseEntityId) {
        interactor.removeCommunityResponder(baseEntityId, this);
    }

    @Override
    public void updateCommunityRespondersList(List<CommunityResponderModel> communityResponderModelList) {
        updateCommunityResponders(communityResponderModelList);
    }
}
