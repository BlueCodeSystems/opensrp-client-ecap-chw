package org.smartregister.chw.core.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.presenter.CoreAllClientsRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.model.OpdRegisterFragmentModel;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class CoreAllClientsRegisterFragment extends BaseOpdRegisterFragment {

    private static final Object DUE_FILTER_TAG = "PRESSED";
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(getActivity(), null, toolbar);

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
            titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }

        if (getSearchView() != null) {
            getSearchView().setHint(R.string.search_name_or_id);
        }
    }

    @Override
    protected void startRegistration() {
        //Implemented but not required for Health Facility app
    }

    @Override
    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        //Overridden
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationMenu.getInstance(getActivity(), null, toolbar);
    }

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        //implement
    }

    @Override
    public void toggleFilterSelection(View dueOnlyLayout) {
        if (dueOnlyLayout != null) {
            if (dueOnlyLayout.getTag() == null) {
                dueFilter(dueOnlyLayout);
            } else if (dueOnlyLayout.getTag().toString().equals(DUE_FILTER_TAG)) {
                normalFilter(dueOnlyLayout);
            }
        }
    }

    private void normalFilter(View dueOnlyLayout) {
        filter(searchText(), "", mainCondition);
        dueOnlyLayout.setTag(null);
        switchViews(dueOnlyLayout, false);
    }

    private void dueFilter(View dueOnlyLayout) {
        filter(searchText(), "", presenter().getDueFilterCondition());
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, true);
    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        TextView dueOnlyTextView = dueOnlyLayout.findViewById(R.id.due_only_text_view);
        if (isPress) {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
        } else {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new CoreAllClientsRegisterFragmentPresenter(this, new OpdRegisterFragmentModel());
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.menu_all_clients;
    }
}
