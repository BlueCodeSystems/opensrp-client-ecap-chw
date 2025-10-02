package org.smartregister.chw.anc.contract;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.List;

public interface BaseAncUpcomingServicesContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void displayLoadingState(boolean state);

        void refreshServices(List<BaseUpcomingService> serviceList);

        Context getContext();
    }

    interface Presenter {

        void initialize();

        View getView();
    }

    interface Interactor {

        void getUpComingServices(MemberObject memberObject, Context context, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onDataFetched(List<BaseUpcomingService> serviceList);

    }
}
