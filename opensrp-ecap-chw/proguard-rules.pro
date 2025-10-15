# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# R8 compatibility shims for optional integrations and desktop-only APIs.
-dontwarn com.ctc.wstx.**
-dontwarn com.google.firebase.crashlytics.**
-dontwarn com.google.firebase.perf.**
-dontwarn java.beans.**
-dontwarn lombok.**
-dontwarn org.joda.convert.**
-dontwarn net.sqlcipher.database.**
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *; }
-keep class org.smartregister.domain.jsonmapping.** { *; }
-keep class org.smartregister.domain.** { *; }
-keep class org.smartregister.domain.db.** { *; }
-keep class org.smartregister.clientandeventmodel.** { *; }
-keep class org.smartregister.commonregistry.** { *; }
-keep class org.smartregister.sync.** { *; }
-keep class org.smartregister.job.** { *; }
-keep class com.bluecodeltd.ecap.chw.model.** { *; }
