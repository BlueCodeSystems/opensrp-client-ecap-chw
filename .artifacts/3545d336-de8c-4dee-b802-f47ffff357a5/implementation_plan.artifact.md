# Implementation Plan - Fix Malaria Read-Only Report Data Rendering

The user reported that only Questions 1 and 2 in Section A of the Malaria Monthly Report are correctly rendered in the read-only view, while other questions show 0. Research confirmed that the `populateData()` method in `MalariaReportViewActivity.java` contains a placeholder comment `// ... Populating other questions omitted for brevity in this response` instead of the actual population logic for Questions 3-10 and all of Section B.

## Proposed Changes

### [opensrp-ecap-chw](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw)

#### [MODIFY] [MalariaReportViewActivity.java](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/MalariaReportViewActivity.java)
- Replace the placeholder comment in `populateData()` with the actual code to map database values to the corresponding `TextView` IDs for:
    - Section A: Questions 3 to 10.
    - Section B: Questions 1 to 10.
- Verify that each key from the `malaria_monthly_reporting.json` form is correctly mapped to its respective view ID in the report layout.

## Verification Plan

### Automated Tests
- N/A (UI-centric change in a legacy activity).

### Manual Verification
1. Open the Malaria Monthly Reporting form and fill in data for all questions (Section A, B, and C).
2. Save the report.
3. Navigate to the Read-Only view for the submitted report.
4. Verify that all sections (A, B, and C) display the exact values entered in the form, instead of 0.
5. Verify that Question 3-10 in Section A and all questions in Section B are correctly populated.
