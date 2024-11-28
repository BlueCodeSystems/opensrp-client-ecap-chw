package org.smartregister.chw.core.shadows;

import android.view.View;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.utils.PncMedicalHistoryViewBuilder;

@Implements(PncMedicalHistoryViewBuilder.class)
public class PncMedicalHistoryViewBuilderShadow {

    @Implementation
    public View build() {
        return Mockito.mock(View.class);
    }
}
