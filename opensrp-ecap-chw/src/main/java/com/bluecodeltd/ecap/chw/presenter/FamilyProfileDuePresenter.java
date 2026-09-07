package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.model.FamilyKitModel;
import com.bluecodeltd.ecap.chw.model.WashCheckModel;
import com.bluecodeltd.ecap.chw.util.Threading;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {
    private WashCheckModel washCheckModel;
    private FamilyKitModel familyKitModel;
    private String childBaseEntityId;

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String childBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
        familyKitModel = new FamilyKitModel(familyBaseEntityId);
        this.childBaseEntityId = childBaseEntityId;
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = CoreConstants.TABLE_NAME.SCHEDULE_SERVICE;

        String selectCondition = getSelectCondition();


        String countSelect = model.countSelect(tableName, selectCondition);
        String mainSelect = model.mainSelect(tableName, selectCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        // countExecute() runs a synchronous COUNT query against the SQLCipher-encrypted DB on
        // whatever thread calls it; called here from Fragment.onResume() it can block the UI
        // thread long enough to ANR. Run it off the main thread instead.
        Threading.io(() -> {
            FamilyProfileDueContract.View countingView = getView();
            if (countingView != null) {
                countingView.countExecute();
            }
            Threading.main(() -> {
                FamilyProfileDueContract.View view = getView();
                if (view != null) {
                    view.filterandSortInInitializeQueries();
                }
            });
        });
    }

    private String getDefaultChildDueQuery() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) ";
    }

    private String getChildDueQueryForChildrenUnderTwoAndGirlsAgeNineToEleven() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') " +
                "and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) " +
                "and (((julianday('now') - julianday(ec_child.dob))/365.25) < 2 or (ec_child.gender = 'Female' and (((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11)))\n";
    }

    private String getSelectCondition(){
        if(ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus()){
            return " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                    + getDefaultChildDueQuery();
        }

        else {
            return " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                    + getChildDueQueryForChildrenUnderTwoAndGirlsAgeNineToEleven();
        }
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

    public boolean saveDataFamilyKit(String jsonObject) {
        return familyKitModel.saveFamilyKitEvent(jsonObject);
    }
}
