package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.contract.ListContract;
import org.smartregister.family.util.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;


/**
 * @author rkodev
 */

public class ListInteractor<T extends ListContract.Identifiable> implements ListContract.Interactor<T> {

    protected AppExecutors appExecutors;

    public ListInteractor() {
        appExecutors = new AppExecutors();
    }

    @Override
    public void runRequest(Callable<List<T>> callable, ListContract.Presenter<T> presenter) {

        Runnable runnable = () -> {
            try {
                List<T> tList = callable.call();
                appExecutors.mainThread().execute(() -> presenter.onItemsFetched(tList));
            } catch (Exception e) {
                Timber.e(e);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
