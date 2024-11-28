package org.smartregister.chw.core.shadows;

import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.FormDataRepository;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * @author rkodev
 */

@Implements(Context.class)
public class ContextShadow {

    private static CommonRepository testCommonRepository;

    public static void setTestCommonRepository(CommonRepository commonRepository) {
        testCommonRepository = commonRepository;
    }

    @Implementation
    public FormDataRepository formDataRepository() {
        return Mockito.mock(FormDataRepository.class);
    }

    @Implementation
    public AllSharedPreferences allSharedPreferences() {
        return new AllSharedPreferences(getDefaultSharedPreferences(RuntimeEnvironment.application));
    }

    @Implementation
    public CommonRepository commonrepository(String tablename) {
        return testCommonRepository == null? Mockito.mock(CommonRepository.class) : testCommonRepository;
    }
}