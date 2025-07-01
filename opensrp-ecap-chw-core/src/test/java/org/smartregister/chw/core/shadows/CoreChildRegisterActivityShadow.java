package org.smartregister.chw.core.shadows;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.core.presenter.CoreChildRegisterPresenter;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class CoreChildRegisterActivityShadow extends CoreChildRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new CoreChildRegisterPresenter(this, mock(CoreChildRegisterModel.class));
    }

    @Override
    protected CoreChildRegisterFragment getRegisterFragment() {
        return new CoreChildRegisterFragment() {
            @Override
            protected void initializePresenter() {
                //mocked method do nothing
            }

            @Override
            public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
                //mocked method do nothing
            }

            @Override
            protected String getMainCondition() {
                return null;
            }

            @Override
            protected String getDefaultSortQuery() {
                return null;
            }
        };
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }
}
