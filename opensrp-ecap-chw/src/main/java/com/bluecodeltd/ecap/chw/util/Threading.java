package com.bluecodeltd.ecap.chw.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
    // IO pool: elastic up to 4x CPU (prevents starvation of blocking I/O)
    private static final ExecutorService IO = new ThreadPoolExecutor(
            /* core */ 2,
            /* max  */ CPU_COUNT * 4,
            /* keepAlive */ 60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            named("IO-")
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

    private Threading() {}

    public static ExecutorService ioExecutor() { return IO; }
    public static ExecutorService cpuExecutor() { return CPU; }
    public static Handler mainHandler() { return MAIN; }

    public static void io(Runnable r) { IO.execute(r); }
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
