# Implementation Plan - Fix Monthly Nutrition Report View

This plan addresses the discrepancies between the newly added `activity_monthly_nutrition_report_view.xml` and the existing `MonthlyNutritionReportViewActivity.java`, ensuring that all data fields are correctly populated and the new UI features (tabs) are functional, while preserving the "old keys" (view IDs and data keys) as requested.

## User Review Required

> [!IMPORTANT]
> The original expansion logic (collapsable sections) will be replaced with a tab-based navigation system to better suit the new "spreadsheet-style" layout provided.

## Proposed Changes

### Android App Sub-project (`:opensrp-ecap-chw`)

#### [MODIFY] [activity_monthly_nutrition_report_view.xml](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/res/layout/activity_monthly_nutrition_report_view.xml)
- Update View IDs to match `MonthlyNutritionReportViewActivity.java` expectations.
- Align section labels with the actual data keys (JSON keys) from `monthly_nutrition_report.json`.
- Add ID `report_view_back_button` to the back arrow in the Toolbar.
- Ensure all sections and tabs have correct IDs for navigation.

#### [MODIFY] [MonthlyNutritionReportViewActivity.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/MonthlyNutritionReportViewActivity.java)
- Implement tab clicking logic to scroll to the corresponding section.
- Update `populateData()` to:
    - Calculate and set "Total" values for each section (e.g., `tv_a_total`, `tv_b_total`).
    - Use correct Snackbar anchor (change `header_card` to `report_scroll`).
- Remove obsolete expansion logic.

## Verification Plan

### Automated Tests
- Build the project to ensure no ID mismatches remain.
- (Optional) Run UI tests if available for this activity.

### Manual Verification
- Deploy the app to a device/emulator.
- Open the Monthly Nutrition Report view.
- Verify that all data fields (Section A through I) are correctly populated.
- Verify that clicking on tabs (A, B, C/D, E/F/G, H/I) scrolls to the correct section.
- Verify that the back button and edit button work correctly.
- Verify that the "Total" counts are accurately calculated.
