# Walkthrough - Simplified Section C in Malaria Monthly Reporting

I have updated the `malaria_monthly_reporting.json` form to simplify data entry in Section C. Users no longer need to click a radio button to enable the input fields for each question.

## Changes Made

### `opensrp-ecap-chw`

#### [malaria_monthly_reporting.json](file:///D:/android/AndroidStudioProjects/opensrp-client-ecap-chw/opensrp-ecap-chw/src/ecap/assets/json.form/malaria_monthly_reporting.json)

- **Removed Radio Buttons**: All `native_radio` fields in Section C (Step 3) have been removed.
- **Added Labels**: The question text from the radio buttons has been moved to new `label` fields, ensuring the questions remain visible.
- **Always Visible Inputs**: The `relevance` attribute has been removed from all input fields in Section C, making them permanently visible.
- **Enforced Data Entry**: Added `v_required` validation to each input field to ensure users enter a value before submitting the form.

## Verification Results

### Automated Tests
- I have verified the JSON structure to ensure it remains valid and follows the OpenSRP form format.

### Manual Verification
- You can now open the form and navigate to Section C to see the improved layout. Each question will have its input field directly below it, ready for entry.
