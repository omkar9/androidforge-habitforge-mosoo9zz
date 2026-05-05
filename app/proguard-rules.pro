# Add project specific ProGuard rules here.
# You can control the default rules by using the -dontobfuscate, -dontoptimize,
# -dontshrink, and -dontwarn options in your ProGuard configuration file.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# AdMob-specific rules
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.**

# Hilt-specific rules
-keep class **.model.** { *; }
-keep class **.di.** { *; }
-keep class **.usecase.** { *; }
-keep class **.repository.** { *; }
-keep class **.entity.** { *; }
-keep class **.dao.** { *; }
-keep class **.mapper.** { *; }
-keep class **.util.** { *; }
-keep class **.presentation.** { *; }

-keep class com.androidforge.habitforge.HabitForgeApp { *; }
-keep class com.androidforge.habitforge.MainActivity { *; }

# Keep all classes that are annotated with @HiltViewModel and its constructors.
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * {
    <init>(...);
}

# Keep worker classes annotated with @HiltWorker and their constructors
-keep @androidx.hilt.work.HiltWorker class * {
    <init>(...);
}

# Hilt internal classes
-keepnames class * implements dagger.MembersInjector
-keepnames class * implements dagger.internal.Factory
-keepnames class * implements dagger.Lazy
-keepnames class * implements javax.inject.Provider

# Room-specific rules
-keepnames class * extends androidx.room.RoomDatabase { public <fields>; public <methods>; }
-keep class **.data.local.entity.* { *; }
-keep class **.data.local.dao.* { *; }
-keep class **.data.local.converter.* { *; }
-keep class **.data.local.AppDatabase_AutoMigration* { *; }
-keep class **.data.local.AppDatabase_Impl { *; }

# For Java 8 Time API (LocalDate, etc.) serialization/deserialization if used by Room
-keep class java.time.** { *; }

# Timber logging library
-dontwarn timber.log.**
-keep class timber.log.** { *; }