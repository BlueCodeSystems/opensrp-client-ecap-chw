package com.bluecodeltd.ecap.chw.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.MotherPmtctProfileActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.PMTCTRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.PMTCTRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.github.javiersantos.appupdater.AppUpdater;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public class PMTCTRegisterFragment extends BaseRegisterFragment implements IndexRegisterFragmentContract.View {

    AlertDialog.Builder builder;

    @Override
    protected void initializePresenter() {
        this.presenter = new PMTCTRegisterFragmentPresenter();
        ((PMTCTRegisterFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);
       Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
        View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity()));

         ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }
        CustomFontTextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.pmtct_services));
            titleView.setFontVariant(FontVariant.REGULAR);
            titleView.setClickable(false);

        }
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
        }
        View topRightLayout = view.findViewById(org.smartregister.R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);
        View topLeftLayout = view.findViewById(org.smartregister.R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);
        View sortFilterBarLayout = view.findViewById(org.smartregister.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);
        View filterSortLayout = view.findViewById(org.smartregister.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        builder = new AlertDialog.Builder(getActivity());

      /*  if (!isSyncing()){
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        }*/
    }



    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        //return "case_status > 0 AND is_closed = 0 ";
                return "(delete_status IS NULL OR delete_status != '1') AND first_name IS NOT NULL AND last_name IS NOT NULL";
    }
    @Override
    protected String getDefaultSortQuery() {
        return "last_interacted_with DESC ";
    }

    @Override
    protected void startRegistration() {

    }


    @Override
    protected void onViewClicked(View view) {

        if(view.getId() == R.id.index_warning){

            builder.setMessage("\u2022  Household has not been Screened");
            builder.setNegativeButton("OK", (dialog, id) -> {
                //  Action for 'NO' Button
                dialog.cancel();

            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();

        } else if (view.getId() == R.id.register_columns){

            CommonPersonObjectClient client =(CommonPersonObjectClient) view.getTag();
            String childId = client.getColumnmaps().get("base_entity_id");
            String clientId = client.getColumnmaps().get("pmtct_id");
//         Toasty.success(getActivity(),"Clicked the person",Toasty.LENGTH_LONG).show();
           goToMotherDetailActivity(clientId,client);
        }
    }

    protected void goToMotherDetailActivity(String clientId, CommonPersonObjectClient client) {

        Intent intent = new Intent(getActivity(), MotherPmtctProfileActivity.class);
        intent.putExtra("client_id",  clientId);
       intent.putExtra("baseId",  client);
        startActivity(intent);
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        PMTCTRegisterProvider registerProvider = new PMTCTRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_HIV_TESTING_SERVICE));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);

    }

    @Override
    protected boolean isSyncing() {
        return super.isSyncing();
    }

    @Override
    protected void onResumption() {

            super.onResumption();

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus))) {
            Utils.showShortToast(getActivity(), getString(org.smartregister.R.string.sync_complete));
            getActivity().recreate();
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        } else {
            super.onSyncComplete(fetchStatus);
        }
    }
}
