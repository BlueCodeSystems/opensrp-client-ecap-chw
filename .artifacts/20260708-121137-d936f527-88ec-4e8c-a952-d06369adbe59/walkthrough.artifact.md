# DBS Monitoring Tab Walkthrough

A new "DBS MONITORING" tab has been added to the HEI details screen in the PMTCT register. This tab is designed to be identical in layout and functionality to the existing "Monitoring" tab.

## Changes Made

### UI Layouts

#### [fragment_pmct_child_dbs_monitoring.xml](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/res/layout/fragment_pmct_child_dbs_monitoring.xml)

- Created a new layout for the DBS Monitoring fragment. It follows the same structure as the "Monitoring" tab, with a header showing the visit count and a recycler view for the list of visits.

### Fragments

#### [PmctChildDbsMonitoringFragment.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/fragment/PmctChildDbsMonitoringFragment.java)

- Implemented a new fragment class that:
    - Fetches child monitoring data using `ChildMonitoringDao`.
    - Displays the data using `PmctChildMonitoringAdapter`.
    - Updates the visit count in the header.
    - Handles the empty state view.

### Activities

#### [HeiDetailsActivity.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HeiDetailsActivity.java)

- Updated `returnViewPager()` to include the `PmctChildDbsMonitoringFragment` as the third tab.
- Added `updateDbsTabTitle()` to set the custom view for the "DBS MONITORING" tab, including the record count.
- Called `updateDbsTabTitle()` in `onCreate` to ensure the tab is properly initialized.

## Verification Summary

### Manual Verification
- Navigated to the HEI register and selected a child.
- Confirmed that the "DBS MONITORING" tab is visible next to the "Monitoring" tab.
- Verified that the record count in the "DBS MONITORING" tab matches the data in the "Monitoring" tab (as they currently share the same DAO).
- Checked that the layout is consistent with the "Monitoring" tab.
