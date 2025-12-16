package com.bluecodeltd.ecap.chw.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Centralized executors/handlers for background and main-thread work.
 * Use Threading.io(...) for I/O bound tasks and Threading.main(...) to post UI updates.
 */
public final class Threading {
    private static final int CPU_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors());
    // IO pool: reliable queued executor for general background work.
    // Use ioBestEffort(...) for RecyclerView per-row binding work (which can burst).
    private static final ThreadPoolExecutor IO = new ThreadPoolExecutor(
            /* core */ CPU_COUNT,
            /* max  */ CPU_COUNT,
            /* keepAlive */ 60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            named("IO-")
    );

    // Best-effort IO pool: elastic, no queue, drops work when saturated (prevents UI-thread crashes/ANRs).
    private static final ThreadPoolExecutor IO_BEST_EFFORT = new ThreadPoolExecutor(
            /* core */ 0,
            /* max  */ CPU_COUNT * 4,
            /* keepAlive */ 30L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            named("IOBE-"),
            new ThreadPoolExecutor.DiscardPolicy()
    );
    // CPU pool: bound to CPU cores for compute-bound work
    private static final ExecutorService CPU = new ThreadPoolExecutor(
            /* core */ CPU_COUNT,
            /* max  */ CPU_COUNT,
            /* keepAlive */ 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            named("CPU-")
    );
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    static {
        IO.allowCoreThreadTimeOut(true);
        IO_BEST_EFFORT.allowCoreThreadTimeOut(true);
    }

    private Threading() {}

    public static ExecutorService ioExecutor() { return IO; }
    public static ExecutorService ioBestEffortExecutor() { return IO_BEST_EFFORT; }
    public static ExecutorService cpuExecutor() { return CPU; }
    public static Handler mainHandler() { return MAIN; }

    public static void io(Runnable r) {
        try {
            IO.execute(r);
        } catch (RejectedExecutionException ignored) {
            // Executor should not reject under normal operation, but never crash the app if it does.
        }
    }

    public static void ioBestEffort(Runnable r) {
        try {
            IO_BEST_EFFORT.execute(r);
        } catch (RejectedExecutionException ignored) {
            // Best-effort: ok to drop.
        }
    }
    public static void cpu(Runnable r) { CPU.execute(r); }
    public static void main(Runnable r) { MAIN.post(r); }

    private static ThreadFactory named(String prefix) {
        return runnable -> {
            Thread t = new Thread(runnable);
            t.setName(prefix + t.getId());
            t.setDaemon(true);
            return t;
        };
    }
}
