package com.evernote.android.job;

import android.app.PendingIntent;
import android.os.Build;

public final class PendingIntentUtil {
  private PendingIntentUtil() {
    // No-op
  }

  public static int flagImmutable() {
    if (Build.VERSION.SDK_INT >= 31) {
      return 0x04000000; // PendingIntent.FLAG_IMMUTABLE
    } else if (Build.VERSION.SDK_INT >= 23) {
      return 0x04000000; // PendingIntent.FLAG_IMMUTABLE
    } else {
      return 0;
    }
  }

  public static int flagMutable() {
    if (Build.VERSION.SDK_INT >= 31) {
      return 0x02000000; // PendingIntent.FLAG_MUTABLE
    } else {
      return 0;
    }
  }
}
