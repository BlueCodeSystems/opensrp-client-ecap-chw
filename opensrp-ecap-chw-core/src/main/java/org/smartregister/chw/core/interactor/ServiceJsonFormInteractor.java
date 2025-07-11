package org.smartregister.chw.core.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.chw.core.widget.ServiceEditTextFactory;

public class ServiceJsonFormInteractor extends JsonFormInteractor {
    private static final JsonFormInteractor PATH_INTERACTOR_INSTANCE = new ServiceJsonFormInteractor();

    private ServiceJsonFormInteractor() {
        super();
    }

    public static JsonFormInteractor getServiceInteractorInstance() {
        return PATH_INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new ServiceEditTextFactory());
    }

}
