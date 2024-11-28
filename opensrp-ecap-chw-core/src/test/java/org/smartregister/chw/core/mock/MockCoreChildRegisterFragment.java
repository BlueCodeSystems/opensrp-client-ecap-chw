package org.smartregister.chw.core.mock;

import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class MockCoreChildRegisterFragment extends CoreChildRegisterFragment {

    @Override
    protected void initializePresenter() {//do nothing
        clientAdapter= mock(RecyclerViewPaginatedAdapter.class);
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
}
