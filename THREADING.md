Threading and Background Work

Overview

- IO tasks: Use `Threading.io { ... }` for database, file, or network work. This executes on a shared elastic pool sized up to 4× the CPU count to avoid starvation during I/O bursts.
- CPU tasks: Use `Threading.cpu { ... }` for compute-bound work (parsing, transformations). This uses a pool fixed to the CPU cores to prevent oversubscription.
- UI updates: Use `Threading.main { ... }` (or Android’s usual mechanisms) to post updates to the main thread.

Guidelines

- Never call DAOs or long operations on the main thread.
- For RecyclerView binding, tag views (e.g., with row IDs) and validate tags in main-thread callbacks to avoid recycled-view glitches.
- Prefer narrow queries and avoid mapping large models if only a few columns are needed.

Coroutines + ViewModel (Recommended)

- Preferred approach for lifecycle-aware concurrency:
  - Use `viewModelScope.launch(Dispatchers.IO)` for I/O and `Dispatchers.Default` for CPU work.
  - Expose LiveData or StateFlow from ViewModel to the UI and observe in Activities/Fragments.
- This repo already includes Kotlin + lifecycle dependencies. New code should default to ViewModel + coroutines where possible.

Examples

// IO + UI update
Threading.io(() -> {
    val result = dao.load()
    Threading.main(() -> adapter.submitList(result))
})

// CPU work
Threading.cpu(() -> heavyParse())


Main-Thread IO Remediation Plan
------------------------------

- **Phase 1 – Inventory & Design (½ day):**
  - Audit DAO usage inside `CasePlan` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/activity/CasePlan.java`), `ShowReferralsActivity`, `ChildSafetyPlanActivity`, `HouseholdServicesOnlyActivity`, and `IndexDetailsActivity` to catalogue synchronous calls and payload sizes.
  - Choose per-screen strategy (dedicated ViewModel with `Dispatchers.IO` vs. `Threading.io`) and define any composite state objects needed to batch DAO results.
  - Record the decisions so reviewers understand the intended threading model per screen.

- **Phase 2 – Infrastructure Prep (¼ day):**
  - Confirm Java activities can lean on `Threading` helpers; add missing convenience methods if needed.
  - Prepare factories/constructors for new ViewModels that require navigation arguments.
  - Verify adapters can accept async updates without complete reconstruction.

- **Phase 3 – Screen Refactors (1½–2 days):**
  - `CasePlan`: Load `IndexPersonDao.getDomainsById(...)` off the UI thread, post results back before touching adapters, and remove `recreate()` usage in favour of adapter updates.
  - `ShowReferralsActivity`: Fetch `ReferralDao.getReferralsByID(...)` asynchronously, toggling the empty state once results arrive.
  - `ChildSafetyPlanActivity`: Move both initial and refresh loads off the main thread and cache results to prevent duplicate DAO hits.
  - `HouseholdServicesOnlyActivity`: Batch caregiver, service list, and gating checks in a single IO task; cache the derived gating data for button handlers.
  - `IndexDetailsActivity`: Introduce a ViewModel that aggregates all required DAO calls (screening, assessments, case plans, caregiver data) and exposes a single state object observed by the activity.

- **Phase 4 – Secondary Optimisations (½ day):**
  - Review DAO implementations for opportunities to add pagination/limits or streaming variants where large lists are still materialised.
  - Move heavy transformations (date parsing, mapping) into the background before posting UI state.
  - Switch adapters to diff-friendly patterns (`ListAdapter`,`submitList`) where feasible to minimise UI churn.

- **Phase 5 – Validation & QA (½ day):**
  - Manually exercise each refactored screen on a mid-tier device or emulator, confirming smooth loads and no main-thread ANRs.
  - Check logs for skipped frames or StrictMode main-thread disk warnings.
  - Run automated tests; add ViewModel unit coverage for new asynchronous loaders.

- **Phase 6 – Rollout Support (¼ day):**
  - Update internal trackers (e.g., `oom-remediation-tracker.md`) with completed items and note remaining candidates.
  - Highlight the smoother loads in release notes and monitor crash/ANR dashboards after deployment.

