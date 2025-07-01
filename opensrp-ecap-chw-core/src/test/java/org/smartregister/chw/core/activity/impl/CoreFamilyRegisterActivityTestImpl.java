package org.smartregister.chw.core.activity.impl;

import org.mockito.Mockito;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CoreFamilyRegisterActivityTestImpl extends CoreFamilyRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return Mockito.mock(BaseRegisterFragment.class);
    }

    public void startFormActivity(String s, String s1, Map<String, String> map) {
        Timber.v("startFormActivity stub");
    }

    public List<String> getViewIdentifiers() {
        List<String> result = new ArrayList<>();
        result.add("1234");
        return result;
    }
}
