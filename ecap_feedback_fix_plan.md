ECAP II Testing Feedback – Planned Fixes
========================================

Navigation & Branding
---------------------
- Restore the Home tab label by reverting `@string/reports` to "Home" in base resources and ensuring Swahili/localized values match.
- Update flavor-specific `nav_logo` (and localized copies) from "USAID Ecap II" to the approved "ECAP II" text.
- Remove manifest hard-coded "All Households" label so runtime strings control titles, eliminating duplicates.
- Reapply ECAP primary/accent palette across `colors.xml` (primary #218CC5, accent #3797ca, etc.) and update bottom navigation selector to use those colors.

Login Screen
------------
- Hide admin overflow options on the login screen (Reset PIN, Export Database) or gate them behind a debug toggle.
- Restyle the Show Password checkbox to use visible stroke/fill colors aligned with the ECAP palette and increase size to Material minimums.

Drawer & Toolbar Copy
---------------------
- Replace the shared toolbar layout’s title binding with context-aware strings so each register/activity displays the intended label.
- Align base-flavor strings (e.g., `all_index_title`, `facility_ovc_register`) with the ECAP flavor values to avoid mixed terminology in the UI.

Forms & Workflows
-----------------
- Change `@string/submit` back to “Save” (or introduce a dedicated `save_label`) so form CTAs match user expectations.
- Audit form launchers (`CasePlan`, `ShowReferralsActivity`, `HouseholdDetails` etc.) to re-enable wizard mode where multi-step navigation is expected and ensure encounter types for edit flows persist correctly.
- Adjust pencil-icon navigation to return users to the profile overview after save rather than jumping to the register list.

Profile & Layout
----------------
- Clean up `activity_household_details.xml` (remove stray characters, convert absolute margins to ConstraintLayout constraints) to fix misalignment.
- Review Graduation Benchmark FAB gating logic to keep the button responsive unless blockers truly exist.
- Fix the squashed “Previous” button by revisiting toolbar/action button styling once titles/colors are restored.

QA & Validation
---------------
- After implementing UI/flow fixes, run through the full login → dashboard → register → pencil-form workflow and capture screenshots to confirm parity with the original design.

Additional Flow Fixes
---------------------
- Household screening flow: update `HouseholdIndexActivity.onActivityResultExtended` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HouseholdIndexActivity.java:162-185`) so that, after saving, control returns to the `HouseholdDetails` overview (or the launching activity) instead of launching `SignatureActivity`/jumping to the register list.
- Graduation Benchmark FAB: loosen or refresh the readiness checks in `HouseholdDetails.onClick` for `R.id.graduation` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HouseholdDetails.java:608-643`) and ensure the ViewModel populates `house` before the button gating logic runs so the FAB remains responsive.
- Multi-page forms: re-enable wizard navigation for Household/VCA/FSW/HTS/PMTCT enrollment flows by setting `form.setWizard(true)` (and restoring next/previous labels) in each launcher (`HouseholdDetails`, `CasePlan`, `IdentificationRegisterActivity`, etc.) and update `@string/submit` back to “Save” so the first screen doesn’t immediately show a Save CTA.
