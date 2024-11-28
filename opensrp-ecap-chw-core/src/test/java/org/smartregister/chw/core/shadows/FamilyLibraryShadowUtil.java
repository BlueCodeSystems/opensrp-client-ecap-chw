package org.smartregister.chw.core.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.Context;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;

/**
 * @author rkodev
 */

@Implements(FamilyLibrary.class)
public class FamilyLibraryShadowUtil {

    private static FamilyLibrary instance;

    @Implementation
    public static FamilyLibrary getInstance() {
        if (instance == null) {
            instance = Mockito.mock(FamilyLibrary.class);
        }

        return instance;
    }

    public static void setInstance(FamilyLibrary instance) {
        FamilyLibraryShadowUtil.instance = instance;
    }

    @Implementation
    public FamilyMetadata metadata() {
        return Mockito.mock(FamilyMetadata.class);
    }

    @Implementation
    public Context context() {
        return Context.getInstance();
    }
}
