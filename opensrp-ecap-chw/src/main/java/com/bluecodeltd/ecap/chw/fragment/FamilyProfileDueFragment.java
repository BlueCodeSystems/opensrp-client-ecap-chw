package com.bluecodeltd.ecap.chw.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.model.FamilyProfileDueModel;
import com.bluecodeltd.ecap.chw.presenter.FamilyProfileDuePresenter;
import com.bluecodeltd.ecap.chw.provider.ChwDueRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class FamilyProfileDueFragment extends BaseFamilyProfileDueFragment {

    private int dueCount = 0;
    private View emptyView;
    private String familyBaseEntityId;

    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new FamilyProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String childBaseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter = new FamilyProfileDuePresenter(this, new FamilyProfileDueModel(), null, familyBaseEntityId, childBaseEntityId);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.d("setAdvancedSearchFormData");
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        ChwDueRegisterProvider chwDueRegisterProvider = new ChwDueRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler);
        this.clientAdapter = new FamilyRecyclerViewCustomAdapter(null, chwDueRegisterProvider, this.context().commonrepository(this.tablename), Utils.metadata().familyDueRegister.showPagination);
        this.clientAdapter.setCurrentlimit(Utils.metadata().familyDueRegister.currentLimit);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case org.smartregister.chw.core.R.id.patient_column:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    getNextAction(view, getArguments());
                }
                break;
            case org.smartregister.chw.core.R.id.next_arrow:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
                    getNextAction(view, getArguments());
                }
                break;
            default:
                break;
        }
    }

    private void getNextAction(View view, Bundle fragmentArguments) {
        FamilyProfileActivity activity = (FamilyProfileActivity) getActivity();
        if (activity == null)
            return;

        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String scheduleName = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.SCHEDULE_NAME, false);

            if (scheduleName.equalsIgnoreCase(CoreConstants.SCHEDULE_TYPES.WASH_CHECK)) {
                startForm(com.bluecodeltd.ecap.chw.util.Constants.JSON_FORM.getWashCheck(), com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_WASH);
            } else if (scheduleName.equalsIgnoreCase(CoreConstants.SCHEDULE_TYPES.FAMILY_KIT)) {
                startForm(com.bluecodeltd.ecap.chw.util.Constants.JSON_FORM.getFamilyKit(), com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_FAMILY_KIT);
            } else if (scheduleName.equalsIgnoreCase(CoreConstants.SCHEDULE_TYPES.ROUTINE_HOUSEHOLD_VISIT)) {
                startForm(com.bluecodeltd.ecap.chw.util.Constants.JSON_FORM.getRoutineHouseholdVisit(), com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_HOUSEHOLD);
            } else {
                activity.goToProfileActivity(view, fragmentArguments);
            }
        }
    }

    public FamilyProfileDuePresenter getPresenter() {
        return (FamilyProfileDuePresenter) presenter;
    }

    @Override
    public void countExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            sqb.addCondition(filters);
            String query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(query);

            Timber.i(query);
            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            dueCount = c.getInt(0);
            clientAdapter.setTotalcount(dueCount);
            Timber.tag("total count here").v("%s", clientAdapter.getTotalcount());
            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

            // update view
            Activity activity = getActivity();
            if (activity != null)
                activity.runOnUiThread(() -> {
                    onEmptyRegisterCount(dueCount < 1);
                    ((FamilyProfileActivity) activity).updateDueCount(dueCount);
                });


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void onEmptyRegisterCount(final boolean has_no_records) {
        if (emptyView != null) {
            emptyView.setVisibility(has_no_records ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_WASH:
            case com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_FAMILY_KIT:
            case com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON_HOUSEHOLD:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        String encounterType = form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE);

                        switch (encounterType) {
                            case com.bluecodeltd.ecap.chw.util.Constants.EventType.WASH_CHECK:
                            case com.bluecodeltd.ecap.chw.util.Constants.EventType.ROUTINE_HOUSEHOLD_VISIT:
                                ((FamilyProfileDuePresenter) presenter).saveData(jsonString);
                                break;
                            case com.bluecodeltd.ecap.chw.util.Constants.EventType.FAMILY_KIT:
                                ((FamilyProfileDuePresenter) presenter).saveDataFamilyKit(jsonString);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void startForm(String formName, int requestCode) {
        try {
            JSONObject jsonForm = FormUtils.getInstance(getActivity()).getFormJson(formName);
            jsonForm.put(JsonFormUtils.ENTITY_ID, familyBaseEntityId);
            Intent intent = new Intent(getActivity(), Utils.metadata().familyMemberFormActivity);
            intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            Form form = new Form();
            form.setWizard(false);
            form.setActionBarBackground(R.color.family_actionbar);

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(Constants.WizardFormActivity.EnableOnCloseDialog, true);
            if (getActivity() != null) {
                getActivity().startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
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
                        return commonRepository().rawCustomQueryForAdapter(mainSelect);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }

    }

}