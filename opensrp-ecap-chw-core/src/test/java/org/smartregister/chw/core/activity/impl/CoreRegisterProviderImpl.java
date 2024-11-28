package org.smartregister.chw.core.activity.impl;

import android.content.Context;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.commonregistry.CommonRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoreRegisterProviderImpl extends CoreRegisterProvider {

    public CoreRegisterProviderImpl(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {
        // Do nothing for now
    }

    @Override
    public List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        // Do nothing for now
        return null;
    }

    @Override
    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        // Do nothing for now
        return null;
    }
}
