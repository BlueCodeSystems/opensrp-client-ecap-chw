package com.bluecodeltd.ecap.chw.issue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Keeps the most recent log lines in memory so an issue report can carry what the app was doing
 * just before the user hit a problem. Nothing is written to disk; the buffer is lost on restart.
 */
public class LogBufferTree extends Timber.Tree {
    private static final int MAX_LINES = 300;
    private static final int MAX_LINE_CHARS = 1_000;
    private static final ArrayDeque<String> LINES = new ArrayDeque<>(MAX_LINES);

    private final int minPriority;

    public LogBufferTree(int minPriority) {
        this.minPriority = minPriority;
    }

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return priority >= minPriority;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
        StringBuilder line = new StringBuilder()
                .append(new SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(new Date()))
                .append(' ').append(level(priority))
                .append('/').append(tag == null ? "-" : tag)
                .append(": ").append(message);
        if (t != null && !message.contains(t.getClass().getName())) {
            line.append(" | ").append(t);
        }
        String text = line.length() > MAX_LINE_CHARS ? line.substring(0, MAX_LINE_CHARS) + "…" : line.toString();
        synchronized (LINES) {
            if (LINES.size() == MAX_LINES) {
                LINES.removeFirst();
            }
            LINES.addLast(text);
        }
    }

    /** The buffered lines, oldest first. */
    public static List<String> snapshot() {
        synchronized (LINES) {
            return new ArrayList<>(LINES);
        }
    }

    private static char level(int priority) {
        switch (priority) {
            case Log.VERBOSE: return 'V';
            case Log.DEBUG: return 'D';
            case Log.INFO: return 'I';
            case Log.WARN: return 'W';
            case Log.ERROR: return 'E';
            default: return 'A';
        }
    }
}
