package com.bluecodeltd.ecap.chw.fragment;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.domain.FormDetails;
import com.bluecodeltd.ecap.chw.model.FamilyProfileActivityModel;
import com.bluecodeltd.ecap.chw.presenter.FamilyProfileActivityPresenter;
import com.bluecodeltd.ecap.chw.provider.FamilyActivityRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class FamilyProfileActivityFragment extends BaseFamilyProfileActivityFragment {
    private String familyBaseEntityId;

    public static BaseFamilyProfileActivityFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileActivityFragment fragment = new FamilyProfileActivityFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        FamilyActivityRegisterProvider familyActivityRegisterProvider = new FamilyActivityRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new FamilyRecyclerViewCustomAdapter(null, familyActivityRegisterProvider, context().commonrepository(this.tablename), Utils.metadata().familyActivityRegister.showPagination);
        clientAdapter.setCurrentlimit(Utils.metadata().familyActivityRegister.currentLimit);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        presenter = new FamilyProfileActivityPresenter(this, new FamilyProfileActivityModel(), null, familyBaseEntityId);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        Timber.d("setAdvancedSearchFormData");
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        // Count query
                        if (args != null && args.getBoolean("count_execute")) {
                            countExecute();
                        }
                        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
                        String query = sqb.orderbyCondition(Sortqueries);
                        query = sqb.Endquery(query);
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
    }

    public void countExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            sqb.addCondition(filters);
            String query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(query);

            Timber.i(getClass().getName(), query);
            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));
            Timber.v("total count here %s", clientAdapter.getTotalcount());
            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case org.smartregister.chw.core.R.id.patient_column:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    displayWashCheckHistory((CommonPersonObjectClient) view.getTag());
                }
                break;
            case org.smartregister.chw.core.R.id.next_arrow:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
                    displayWashCheckHistory((CommonPersonObjectClient) view.getTag());
                }
                break;
            default:
                break;
        }
    }

    private void displayWashCheckHistory(CommonPersonObjectClient commonPersonObjectClient) {
        String type = commonPersonObjectClient.getColumnmaps().get("visit_type");
        Long visitDate = Long.parseLong(commonPersonObjectClient.getColumnmaps().get("visit_date"));

        if (CoreConstants.EventType.WASH_CHECK.equalsIgnoreCase(type)) {
            WashCheckDialogFragment dialogFragment = WashCheckDialogFragment.getInstance(familyBaseEntityId, visitDate);
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            dialogFragment.show(ft, WashCheckDialogFragment.DIALOG_TAG);
        } else if (CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT.equalsIgnoreCase(type)) {

            FormDetails formDetails = new FormDetails();
            formDetails.setTitle(getString(R.string.routine_household_visit));
            formDetails.setBaseEntityID(familyBaseEntityId);
            formDetails.setEventDate(visitDate);
            formDetails.setEventType(com.bluecodeltd.ecap.chw.util.Constants.EventType.ROUTINE_HOUSEHOLD_VISIT);
            formDetails.setFormName(com.bluecodeltd.ecap.chw.util.Constants.JSON_FORM.getRoutineHouseholdVisit());

            FormHistoryDialogFragment dialogFragment = FormHistoryDialogFragment.getInstance(formDetails);
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            dialogFragment.show(ft, FormHistoryDialogFragment.DIALOG_TAG);
        } else if (CoreConstants.EventType.FAMILY_KIT.equalsIgnoreCase(type)) {
            FamilyKitDialogFragment dialogFragment = FamilyKitDialogFragment.getInstance(familyBaseEntityId, visitDate);
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            dialogFragment.show(ft, FamilyKitDialogFragment.DIALOG_TAG);
        }
    }

}
