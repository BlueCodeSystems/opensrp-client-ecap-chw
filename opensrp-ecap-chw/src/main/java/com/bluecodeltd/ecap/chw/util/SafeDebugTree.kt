package com.bluecodeltd.ecap.chw.util

import android.util.Log
import timber.log.Timber

/**
 * Prevents OOMs when logs accidentally include very large payloads (e.g. HTTP responses).
 * Timber formats messages using String.format, which can allocate massive intermediate buffers.
 */
class SafeDebugTree(
    private val maxArgChars: Int = 4_000,
    private val minPriority: Int = Log.VERBOSE
) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < minPriority) return
        super.log(priority, tag, message, t)
    }

    override fun formatMessage(message: String, args: Array<out Any?>): String {
        if (args.isEmpty()) return message

        val safeArgs: Array<out Any?> = args.map { arg ->
            when (arg) {
                is String -> truncate(arg)
                is CharSequence -> truncate(arg.toString())
                else -> arg
            }
        }.toTypedArray()

        return try {
            String.format(message, *safeArgs)
        } catch (_: Exception) {
            message
        }
    }

    private fun truncate(value: String): String {
        if (value.length <= maxArgChars) return value
        val head = value.substring(0, maxArgChars)
        return "$head… (truncated, len=${value.length})"
    }
}
