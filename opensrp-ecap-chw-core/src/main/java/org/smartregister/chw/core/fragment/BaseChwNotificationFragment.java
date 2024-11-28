package org.smartregister.chw.core.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.BaseChwNotificationFragmentContract;
import org.smartregister.chw.core.provider.BaseChwNotificationProvider;
import org.smartregister.chw.core.provider.BaseChwNotificationQueryProvider;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;

import java.util.List;

import timber.log.Timber;

public abstract class BaseChwNotificationFragment extends BaseChwRegisterFragment implements BaseChwNotificationFragmentContract.View {

    private BaseChwNotificationQueryProvider queryProvider;

    public BaseChwNotificationFragment(){
        queryProvider = new BaseChwNotificationQueryProvider();
    }

    @Override
    public void initializeAdapter() {
        BaseChwNotificationProvider registerProvider = new BaseChwNotificationProvider(getActivity(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus)
                || FetchStatus.nothingFetched.equals(fetchStatus))) {
            Utils.showShortToast(getActivity(), getString(org.smartregister.opd.R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncComplete(fetchStatus);
        }

        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.top_right_layout).setVisibility(View.GONE);
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.updates;
    }

    @Override
    protected String getMainCondition() {
        return mainCondition;
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

    @NotNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (id == LOADER_ID && getActivity() != null) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    String query = filterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    private String filterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = queryProvider.getObjectIdsQuery(filters);
                sql = sqb.addlimitandOffset(sql, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());

                List<String> ids = commonRepository().findSearchIds(sql);
                query = queryProvider.mainSelectWhereIDsIn();

                String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
                return query.replace("%s", joinedIds);
            } else {
                if (!TextUtils.isEmpty(filters) && TextUtils.isEmpty(Sortqueries)) {
                    sqb.addCondition(filters);
                    query = sqb.orderbyCondition(Sortqueries);
                    query = sqb.Endquery(sqb.addlimitandOffset(query,
                            clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    @Override
    public void countExecute() {
        try {
            int totalCount = 0;
            for (String sql : queryProvider.countExecuteQueries()) {
                Timber.i(sql);
                totalCount += commonRepository().countSearchIds(sql);
            }

            clientAdapter.setTotalcount(totalCount);
            Timber.i("Total Register Count %d", clientAdapter.getTotalcount());
            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
