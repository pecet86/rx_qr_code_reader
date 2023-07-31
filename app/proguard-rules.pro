# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/yshrsmz/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-dontwarn okio.**

-keep class * extends androidx.fragment.app.Fragment
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable