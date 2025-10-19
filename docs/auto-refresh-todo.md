# Auto-Refresh & Form Loading Follow-Up

## Context
- Commit `fix(gating)` resolved the immediate gating bug by normalizing household screening checks, but several related screens still rely on `activity.recreate()`/`finish()` to refresh UI after form submissions. Users therefore see a full reload and newly added items do not appear instantly.
- The VCA profile now warms the `vca_screening` JSON form, yet other large JSON forms (service reports, visitations, safety plans) still load cold and block the UI without a spinner.
- Andre asked for a detailed TODO so another agent can continue the refresh/caching work without rediscovering the issues.

## TODO
- [x] **Refactor caregiver case-plan refresh**  
  Update `HouseholdCasePlanActivity` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HouseholdCasePlanActivity.java`) and `HouseholdDomainPlanAdapter` so they call a local `fetchData()`/`notifyDataSetChanged()` on save/delete instead of `recreate()`/`finish()`. Preserve scroll position and reuse the existing `Handler` pattern from `CasePlan`.

- [x] **Clean up child case-plan list refresh**  
  Replace `activity.recreate()` in `CasePlanAdapter` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/adapter/CasePlanAdapter.java`) with a callback into the hosting fragment/activity that re-queries case plans and updates the adapter in place.

- [x] **Household service flows**  
  In both `HouseholdServiceActivity` and `HouseholdServiceAdapter` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/HouseholdServiceActivity.java`, `.../adapter/HouseholdServiceAdapter.java`) replace `runOnUiThread(this::recreate)`/`activity.recreate()` with a shared refresh method that reloads `familyServiceList` and uses `notifyDataSetChanged()`. Mirror the callback structure used by `DomainPlanAdapter`.

- [ ] **VCA service history refresh**  
  Adjust `VcaServiceActivity` and `VCAServiceAdapter` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/VcaServiceActivity.java`, `.../adapter/VCAServiceAdapter.java`) to remove `recreate()` calls, wiring an `OnDataUpdateListener` that triggers a targeted `fetchServices()` method.

- [ ] **Safety-plan activities**  
  For `ChildSafetyPlanActivity` and `ChildSafetyPlanActions` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/ChildSafetyPlanActivity.java`, `.../ChildSafetyPlanActions.java`), eliminate the `finish()/startActivity()`/`recreate()` cycle in `onActivityResult`. Introduce a reusable loader that repopulates the adapter data and refreshes summary counters.

- [ ] **Referral list refresh**  
  Update `ShowReferralsActivity` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/ShowReferralsActivity.java`) to refresh the list in place rather than finishing and relaunching the activity.

- [ ] **Register fragments (`recreate()` removal)**  
  In `PMTCTRegisterFragment`, `HivTestingServiceRegisterFragment`, and `IndexFragmentRegister`, replace `getActivity().recreate()` with fragment-level data reloads so the register stays responsive post submission (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/fragment/PMTCTRegisterFragment.java`, `.../HivTestingServiceRegisterFragment.java`, `.../IndexFragmentRegister.java`).

- [ ] **Extend JSON form caching**  
  Reuse the `warmFormAsync` helper (currently called with `"vca_screening"` in `IndexDetailsActivity`) for heavyweight forms invoked from service/visitation/safety-plan entry points. Add a simple cache eviction or size cap to avoid keeping too many multi-megabyte JSON strings in memory (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/VcaServiceActivity.java`, `HouseholdServiceActivity.java`, adapters loading edit forms).

- [ ] **Spinner/feedback parity**  
  Surface the lightweight loading dialog/spinner before opening cached forms when the form still needs disk IO (service reports, visitations, safety plans). Ensure dismissal happens in both success and error paths to prevent stuck spinners.
