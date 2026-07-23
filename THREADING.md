Threading and Background Work

Overview

- IO tasks: Use `Threading.io { ... }` for database, file, or network work. This executes on a shared queued pool sized to the CPU count for stability (no task rejections).
- Best-effort IO tasks: Use `Threading.ioBestEffort { ... }` for RecyclerView per-row/background decoration work (row badges, icons, counts). This work may be dropped when the app is under load to avoid crashes/backlogs.
- CPU tasks: Use `Threading.cpu { ... }` for compute-bound work (parsing, transformations). This uses a pool fixed to the CPU cores to prevent oversubscription.
- UI updates: Use `Threading.main { ... }` (or Android’s usual mechanisms) to post updates to the main thread.

Guidelines

- Never call DAOs or long operations on the main thread.
- Use `Threading.io { ... }` (not best-effort) for critical work that must run (e.g., loading a details screen’s models).
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
