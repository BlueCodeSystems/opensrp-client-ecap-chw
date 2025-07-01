package org.smartregister.chw.core.mock;

import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class MockCoreFpRegisterFragment extends CoreFpRegisterFragment {
    @Override
    protected void initializePresenter() {//do nothing
        clientAdapter = mock(RecyclerViewPaginatedAdapter.class);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {//do nothing
    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    public void openProfile(CommonPersonObjectClient client) {
        super.openProfile(client);
    }
}
