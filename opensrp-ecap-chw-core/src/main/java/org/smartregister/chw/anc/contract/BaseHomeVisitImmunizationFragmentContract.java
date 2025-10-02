package org.smartregister.chw.anc.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.domain.VaccineDisplay;

import java.util.Map;

public interface BaseHomeVisitImmunizationFragmentContract {

    interface View {
        void initializePresenter();

        void updateNoVaccineCheck(boolean state);

        void updateSelectedVaccines(Map<String, String> selectedVaccines, boolean variedMode);

        JSONObject getJsonObject();

        Map<String, VaccineDisplay> getVaccineDisplays();

        void setVaccineDisplays(Map<String, VaccineDisplay> vaccineDisplays);

        /**
         * toggle vaccine entry mode
         *
         * @param singleEntryMode
         */
        void setSingleEntryMode(boolean singleEntryMode);

        void noVaccineGivenMode();

        void redrawView();
    }

    interface Presenter {

        /**
         * notifies the view if no vaccine was selected
         *
         * @param state
         */
        void onNoVaccineState(boolean state);

        /**
         * Returns a map of the vaccine and the current value
         *
         * @param selectedVaccines
         */
        void onSelectedVaccinesInitialized(Map<String, String> selectedVaccines, boolean variedMode);

        /**
         * notifies the view that no vaccine should be selected
         */
        void onNoVaccineSelected();
    }

    interface Model {
        void getVaccinesState(JSONObject jsonObject, Presenter presenter);
    }
}
