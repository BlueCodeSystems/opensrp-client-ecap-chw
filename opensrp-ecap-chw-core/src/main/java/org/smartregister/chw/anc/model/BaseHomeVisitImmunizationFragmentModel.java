package org.smartregister.chw.anc.model;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseHomeVisitImmunizationFragmentContract;
import org.smartregister.chw.anc.util.Constants;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class BaseHomeVisitImmunizationFragmentModel implements BaseHomeVisitImmunizationFragmentContract.Model {

    @Override
    public void getVaccinesState(JSONObject jsonObject, BaseHomeVisitImmunizationFragmentContract.Presenter presenter) {
        try {
            if (jsonObject != null) {

                Map<String, String> map = new HashMap<>();

                JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
                int x = 0;
                int size = jsonArray.length();
                boolean noSelected = true;
                boolean variedMode = false;
                String preValue = null;

                while (x < size) {
                    JSONObject field = jsonArray.getJSONObject(x);
                    String key = field.getString(JsonFormConstants.KEY);
                    String value = field.getString(JsonFormConstants.VALUE);


                    if (!value.equalsIgnoreCase(Constants.HOME_VISIT.VACCINE_NOT_GIVEN)) {
                        noSelected = false;

                        if (preValue != null && !variedMode && !preValue.equalsIgnoreCase(value)) {
                            variedMode = true;
                        }

                        preValue = value;
                    }

                    map.put(key, value);

                    x++;
                }

                ///
                // if the view has multiple dates, notify the ui to prepare it self for multi mode.
                // This function must be called before the other 2 functions

                presenter.onNoVaccineState(noSelected);
                presenter.onSelectedVaccinesInitialized(map, variedMode);
            }else{
                presenter.onNoVaccineState(false);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
