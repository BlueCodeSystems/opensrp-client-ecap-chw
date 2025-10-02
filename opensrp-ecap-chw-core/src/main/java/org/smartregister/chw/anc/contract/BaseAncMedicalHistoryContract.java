package org.smartregister.chw.anc.contract;

import android.content.Context;

import org.smartregister.chw.anc.domain.Visit;

import java.util.List;

public interface BaseAncMedicalHistoryContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void onDataReceived(List<Visit> visits);

        Context getViewContext();

        android.view.View renderView(List<Visit> visits);

        void displayLoadingState(boolean state);
    }

    interface Presenter {

        void initialize();

        View getView();
    }

    interface Interactor {

        void getMemberHistory(String memberID, Context context, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onDataFetched(List<Visit> visits);

    }

}
