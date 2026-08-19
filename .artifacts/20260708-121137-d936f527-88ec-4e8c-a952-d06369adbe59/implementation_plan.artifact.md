# Add DBS Monitoring Tab in HEI Register

This plan outlines the steps to add a new "DBS MONITORING" tab in the HEI PMTCT register for each child, beside the existing "Monitoring" tab.

## Proposed Changes

### UI Layouts

#### [NEW] [fragment_pmct_child_dbs_monitoring.xml](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/res/layout/fragment_pmct_child_dbs_monitoring.xml)

- Create a new layout for the DBS Monitoring fragment, identical to the existing `fragment_pmct_child_monitoring.xml`.

### Fragments

#### [NEW] [PmctChildDbsMonitoringFragment.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/fragment/PmctChildDbsMonitoringFragment.java)

- Create a new fragment class to handle the DBS Monitoring tab.
- This fragment will use `PmctChildMonitoringAdapter` and `ChildMonitoringDao` to display records, matching the layout and functionality of the existing "Monitoring" tab.

### Activities

#### [HeiDetailsActivity.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HeiDetailsActivity.java)

- Update `returnViewPager()` to include the new `PmctChildDbsMonitoringFragment`.
- Add `updateDbsTabTitle()` to set the custom view for the new tab (similar to `updateAncTabTitle()`).
- Update `tabMediator` to set the title "DBS MONITORING" for the third tab.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the HEI register.
- Select a child to view their details.
- Verify that a new "DBS MONITORING" tab is visible beside the "Monitoring" tab.
- Verify that the layout of the "DBS MONITORING" tab matches the "Monitoring" tab.
- Verify that the count in the tab correctly reflects the number of records.
