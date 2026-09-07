package com.bluecodeltd.ecap.chw.presenter;

import org.smartregister.chw.anc.repository.VisitRepository;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.model.WashCheckModel;
import com.bluecodeltd.ecap.chw.util.Threading;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {
    private WashCheckModel washCheckModel;

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = VisitRepository.VISIT_TABLE;

        String countSelect = model.countSelect(tableName, mainCondition);
        String mainSelect = model.mainSelect(tableName, mainCondition);

        getView().initializeQueryParams(Utils.metadata().familyActivityRegister.tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        // countExecute() runs a synchronous COUNT query against the SQLCipher-encrypted DB on
        // whatever thread calls it; called here from Fragment.onResume() it can block the UI
        // thread long enough to ANR. Run it off the main thread instead.
        Threading.io(() -> {
            FamilyProfileActivityContract.View countingView = getView();
            if (countingView != null) {
                countingView.countExecute();
            }
            Threading.main(() -> {
                FamilyProfileActivityContract.View view = getView();
                if (view != null) {
                    view.filterandSortInInitializeQueries();
                }
            });
        });
    }

    @Override
    public String getMainCondition() {
        return "(ec_family_member.relational_id = '" + familyBaseEntityId + "' or visits.base_entity_id = '" + familyBaseEntityId + "') AND " +
                ChildDBConstants.KEY.VISIT_TYPE + " in ( " + getEventsInCSV() + ") ";
    }

    private String getEventsInCSV() {
        List<String> events = new ArrayList<>();

        if (ChwApplication.getApplicationFlavor().hasANC()) {
            events.add(CoreConstants.EventType.ANC_HOME_VISIT);
            events.add(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE);
        }

        if (ChwApplication.getApplicationFlavor().hasPNC())
            events.add(CoreConstants.EventType.PNC_HOME_VISIT);

        if (ChwApplication.getApplicationFlavor().hasWashCheck())
            events.add(CoreConstants.EventType.WASH_CHECK);

        if (ChwApplication.getApplicationFlavor().hasFamilyKitCheck())
            events.add(CoreConstants.EventType.FAMILY_KIT);

        if (ChwApplication.getApplicationFlavor().hasRoutineVisit())
            events.add(CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT);

        events.add(CoreConstants.EventType.CHILD_HOME_VISIT);
        events.add(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);

        StringBuilder res = new StringBuilder();
        for (String s : events) {
            if (res.length() > 0)
                res.append(",");

            res.append("'").append(s).append("'");
        }

        return res.toString();
    }

    @Override
    public String getDefaultSortQuery() {
        return VisitRepository.VISIT_TABLE + "." + ChildDBConstants.KEY.VISIT_DATE + " DESC";
    }
}
