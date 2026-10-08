package org.smartregister.chw.core.utils;

/**
 * Process-wide AppExecutors instances.
 * <p>
 * Each {@code new AppExecutors()} starts a single-thread disk executor and a 3-thread network
 * pool that are never shut down. Interactors and screens used to create one per instance, so every
 * screen opened leaked four idle threads (crash reports showed 1000+ "pool-N" executors). Use these
 * shared instances instead.
 */
public final class SharedAppExecutors {

    private static volatile org.smartregister.family.util.AppExecutors family;
    private static volatile org.smartregister.chw.anc.util.AppExecutors anc;

    private SharedAppExecutors() {
    }

    public static org.smartregister.family.util.AppExecutors family() {
        if (family == null) {
            synchronized (SharedAppExecutors.class) {
                if (family == null) {
                    family = new org.smartregister.family.util.AppExecutors();
                }
            }
        }
        return family;
    }

    public static org.smartregister.chw.anc.util.AppExecutors anc() {
        if (anc == null) {
            synchronized (SharedAppExecutors.class) {
                if (anc == null) {
                    anc = new org.smartregister.chw.anc.util.AppExecutors();
                }
            }
        }
        return anc;
    }
}
