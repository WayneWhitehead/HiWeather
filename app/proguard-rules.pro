# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn kotlin.reflect.jvm.internal.**
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Ignore the missing org.apiguardian.api.API class
-dontwarn org.apiguardian.api.API

# Ignore the missing org.apiguardian.api.API$Status class
-dontwarn org.apiguardian.api.API$Status

# Ignore the missing edu.umd.cs.findbugs.annotations.SuppressFBWarnings class
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings

# Ignore the missing java.lang.instrument.UnmodifiableClassException class
-dontwarn java.lang.instrument.UnmodifiableClassException

# Ignore the missing org.slf4j.Logger class
-dontwarn org.slf4j.Logger

# Ignore the missing org.slf4j.LoggerFactory class
-dontwarn org.slf4j.LoggerFactory