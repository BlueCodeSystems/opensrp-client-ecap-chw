package org.smartregister.chw.anc.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BaseAncRegisterFragmentPresenter implements BaseAncRegisterFragmentContract.Presenter {

    protected WeakReference<BaseAncRegisterFragmentContract.View> viewReference;

    protected BaseAncRegisterFragmentContract.Model model;

    protected RegisterConfiguration config;

    protected Set<View> visibleColumns = new TreeSet<>();
    protected String viewConfigurationIdentifier;

    public BaseAncRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
//        implement
    }

    @Override
    public String getMainCondition() {
        return "";
    }

    @Override
    public String getDefaultSortQuery() {
        return Constants.TABLES.ANC_MEMBERS + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.ANC_MEMBERS;
    }

    @Override
    public String getDueFilterCondition() {
        return "(( " +
                "IFNULL(SUBSTR(" + DBConstants.KEY.LAST_HOME_VISIT + ",7,4) || SUBSTR(" + DBConstants.KEY.LAST_HOME_VISIT + ",4,2) || SUBSTR(" + DBConstants.KEY.LAST_HOME_VISIT + ",1,2) || '000000',0) " +
                "< STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month')) " +
                "AND IFNULL(STRFTIME('%Y%m%d%H%M%S', datetime((" + DBConstants.KEY.VISIT_NOT_DONE + ")/1000,'unixepoch')),0) " +
                "< STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month')) " +
                " ))";
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            this.visibleColumns = model.getRegisterActiveColumns(viewConfigurationIdentifier);
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = getMainTable();

        String countSelect = model.countSelect(tableName, mainCondition);
        String mainSelect = model.mainSelect(tableName, mainCondition);

        getView().initializeQueryParams(tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    protected BaseAncRegisterFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void startSync() {
//        implement

    }

    @Override
    public void searchGlobally(String s) {
//        implement

    }
}
