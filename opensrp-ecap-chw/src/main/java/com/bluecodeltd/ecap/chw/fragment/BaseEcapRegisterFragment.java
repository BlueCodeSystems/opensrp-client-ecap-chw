package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.fragment.BaseRegisterFragment;

public abstract class BaseEcapRegisterFragment extends BaseRegisterFragment {

    private static final String TAG = "BaseEcapRegisterFragment";

    private CommonRepository resolveRepository(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return null;
        }
        Context opensrpContext = context();
        if (opensrpContext == null) {
            Log.w(TAG, "OpenSRP context unavailable while resolving repository for table: " + tableName);
            return null;
        }
        CommonRepository repository = opensrpContext.commonrepository(tableName);
        if (repository == null) {
            Log.w(TAG, "Missing common repository for table: " + tableName);
        }
        return repository;
    }

    protected CommonRepository getRepository(String tableName) {
        return resolveRepository(tableName);
    }

    protected CommonRepository getCurrentRepository() {
        return resolveRepository(tablename);
    }

    private boolean isRepositoryReady() {
        CommonRepository repository = getCurrentRepository();
        if (repository == null) {
            if (tablename == null || tablename.trim().isEmpty()) {
                Log.w(TAG, "Cannot execute queries before tablename is set");
            }
            return false;
        }
        return true;
    }

    @Override
    protected void setUpActionBar() {
        if (!(getActivity() instanceof AppCompatActivity) || rootView == null) {
            return;
        }

        Toolbar toolbar = rootView.findViewById(org.smartregister.R.id.register_toolbar);
        if (toolbar == null) {
            return;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setLogo(org.smartregister.R.drawable.round_white_background);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void filterandSortInInitializeQueries() {
        if (!isRepositoryReady()) {
            return;
        }
        super.filterandSortInInitializeQueries();
    }

    @Override
    public void initialFilterandSortExecute() {
        if (!isRepositoryReady()) {
            return;
        }
        super.initialFilterandSortExecute();
    }

    @Override
    public void filterandSortExecute(Bundle bundle) {
        if (!isRepositoryReady()) {
            return;
        }
        super.filterandSortExecute(bundle);
    }

    @Override
    public void filterandSortExecute() {
        if (!isRepositoryReady()) {
            return;
        }
        super.filterandSortExecute();
    }

    @Override
    public void countExecute() {
        if (!isRepositoryReady()) {
            return;
        }
        super.countExecute();
    }

    @Override
    public void showProgressView() {
        if (clientsProgressView == null && rootView != null) {
            clientsProgressView = rootView.findViewById(org.smartregister.R.id.client_list_progress);
        }
        if (clientsView == null && rootView != null) {
            clientsView = rootView.findViewById(org.smartregister.R.id.recycler_view);
        }
        if (clientsProgressView == null || clientsView == null) {
            return;
        }
        super.showProgressView();
    }

    @Override
    public void hideProgressView() {
        if (clientsProgressView == null && rootView != null) {
            clientsProgressView = rootView.findViewById(org.smartregister.R.id.client_list_progress);
        }
        if (clientsView == null && rootView != null) {
            clientsView = rootView.findViewById(org.smartregister.R.id.recycler_view);
        }
        if (clientsProgressView == null || clientsView == null) {
            return;
        }
        super.hideProgressView();
    }
}
