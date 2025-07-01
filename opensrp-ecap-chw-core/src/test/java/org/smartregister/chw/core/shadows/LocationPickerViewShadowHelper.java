package org.smartregister.chw.core.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.view.LocationPickerView;

@Implements(LocationPickerView.class)
public class LocationPickerViewShadowHelper extends CustomFontTextViewShadowHelper {

    @Implementation
    public void init() {
        // Do nothing
    }

    @Implementation
    public String getSelectedItem() {
        return "test_selected_location";
    }
}
