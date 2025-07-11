package org.smartregister.chw.core.activity.mock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.chw.core.activity.BaseChwNotificationRegister;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.mockito.Mockito.mock;

public class BaseChwNotificationRegisterMock extends BaseChwNotificationRegister {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(org.smartregister.R.style.AppThemeNoActionBarAndTitle);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializePresenter() {
        presenter = mock(BaseRegisterContract.Presenter.class);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BaseRegisterFragmentMock();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        Timber.v("startFormActivity stub");
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        //mock do nothing
    }

    @Override
    public void startFormActivity(JSONObject form) {
        //mock do nothing
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        //mock do nothing
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {
        //mock do nothing
    }

    public static class BaseRegisterFragmentMock extends BaseRegisterFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initializePresenter();
        }

        @Override
        protected void initializePresenter() {
            presenter = mock(BaseRegisterFragmentContract.Presenter.class);
        }

        @Override
        public void setUniqueID(String qrCode) {
            //mock do nothing
        }

        @Override
        public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
            //mock do nothing
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
        protected void startRegistration() {
            //mock do nothing
        }

        @Override
        protected void onViewClicked(View view) {
            //mock do nothing
        }

        @Override
        public void showNotFoundPopup(String opensrpId) {
            //mock do nothing
        }
    }
}
