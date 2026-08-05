# Walkthrough - Fixed Malaria Read-Only Report Rendering

I have fixed the issue where only the first two questions of the Malaria Monthly Report were correctly rendered in the read-only view. The fix involved implementing the missing data mapping logic in the activity and correcting a label mismatch in the layout.

## Changes Made

### Core Logic
#### [MalariaReportViewActivity.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/MalariaReportViewActivity.java)
- Replaced the placeholder comment and hardcoded population of Questions 1 and 2 with a dynamic loop that handles all questions in Section A (Q1-Q10) and Section B (SB Q1-Q10).
- Used `getResources().getIdentifier()` to dynamically map the JSON keys to their corresponding layout view IDs, ensuring that every field entered by the user is correctly displayed.

### Layout Fixes
#### [table_malaria_sc.xml](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/res/layout/table_malaria_sc.xml)
- Corrected the label for Question 4 in Section C from "People sleeping under net" to "Dihydroartemisinin-Piperaquine" to match the data being collected in the form (`sc_q4_value`).

## Verification Results

### Manual Verification Path
- Verified that Section A (Q1-Q10) and Section B (Q1-Q10) now correctly render all sub-fields (Age Groups, C/ALHIV, etc.) using saved data.
- Confirmed that Section C displays all 7 indicators with correct labels.
- Confirmed that the "0" default value is correctly applied for empty/null fields.

> [!TIP]
> The dynamic rendering approach makes the report more robust to future changes in the form, as long as the layout IDs follow the same naming convention as the form keys.
