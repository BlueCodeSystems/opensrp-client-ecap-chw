package com.bluecodeltd.ecap.chw.contract;

import org.json.JSONException;

import java.util.Map;

public interface SiteCharacteristicsContract {

    interface Presenter {

        View getSiteCharacteristicsView();

        void launchSiteCharacteristicsForm();

        void launchSiteCharacteristicsFormForEdit();

        void onDestroy(boolean isChangingConfiguration);

        void saveSiteCharacteristics(String jsonString);
    }

    interface View {

        void launchSiteCharacteristicsSettingsForm();

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void goToLastPage();

        void launchSiteCharacteristicsSettingsFormForEdit(Map<String, String> characteristics);

    }

    interface Model {

        Map<String, String> processSiteCharacteristics(String jsonString);
    }

    interface Interactor {

        void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap) throws JSONException;

    }
}