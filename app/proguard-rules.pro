# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android_sdk_new/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#压缩(Shrink):侦测并移除代码中无用的类、字段、方法、和特性(Attribute)。
#优化(OPtimize):对字节码进行优化，移除无用指令。
#混淆(Obfuscate):使用a、b、c、d这样简短而无意义的名称，对类、字段和方法进行重命名。
#预检(Preveirfy): 在java平台上对处理后的代码进行预检。
-dontshrink     #shrink，测试后发现会将一些无效代码给移除，即没有被显示调用的代码，该选项 表示 不启用 shrink。
-dontskipnonpubliclibraryclassmembers    #不混淆jars中的 非public classes   默认选项   # 指定不去忽略非公共的库的类的成员
-ignorewarnings   #忽略警告
-optimizationpasses 5   #指定代码的压缩级别
-dontusemixedcaseclassnames   #不使用大小写混合类名    # 混淆时不使用大小写混合，混淆后的类名为小写
-dontskipnonpubliclibraryclasses    #不混淆第三方jar  # 指定不去忽略非公共的库的类      # 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontoptimize       #不启用优化  不优化输入的类文件
-dontpreverify    # 不做预检验，preverify是proguard的四个步骤之一    # Android不需要preverify，去掉这一步可以加快混淆速度
-verbose      #混淆时是否记录日志  # 有了verbose这句话，混淆后就会生成映射文件    # 包含有类名->混淆后类名的映射关系    然后使用printmapping指定映射文件的名称
-printmapping proguardMapping.txt   #混淆前后的映射


#指定重新打包,所有包重命名,这个选项会进一步模糊包名，将包里的类混淆成n个再重新打包到一个个的package中
-flattenpackagehierarchy
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*    # 指定混淆时采用的算法，后面的参数是一个过滤器    # 这个过滤器是谷歌推荐的算法，一般不改变
#保护注解
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable    # 抛出异常时保留代码行号    #保持源文件和行号的信息,用于混淆后定位错误位置
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*   #保持含有Annotation字符串的 attributes    # 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature    # 避免混淆泛型    # 这在JSON实体映射时非常重要，比如fastJson

-dontwarn org.apache.**
-dontwarn android.support.**
#---------------------------------1.实体类-------
-keep class com.xingfabu.direct.entity.**{
    *;   #全部忽略
}
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
#---------------------------------基本指令区----------------------------------
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#---------------------------------基本指令区----------------------------------
#基础配置
# 保持哪些类不被混淆
# 系统组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends Android.support.**
-keep public class * extends android.view.View    #自定义View

# V4,V7
-keep class android.support.** {*;}
-keep class android.support.v4.**{ *; }
-keep class android.support.v7.**{ *; }
-keep class android.webkit.**{*;}
-keep interface android.support.v4.app.** { *; }


-dontwarn android.support.v4.**
-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2


#保持 本化方法及其类声明
-keepclasseswithmembers class * {
    native <methods>;
}
#保持view的子类成员： getter setter
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#保持Activity的子类成员：参数为一个View类型的方法   如setContentView(View v)   # 保留Activity中的方法参数是view的方法，   # 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持枚举类的成员：values方法和valueOf  (每个enum 类都默认有这两个方法)    # 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View{
    *** get*();
    public void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持Parcelable的实现类和它的成员：类型为android.os.Parcelable$Creator 名字任意的 属性     # 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    !static !transient <fields>;
}

# 对R文件下的所有类及其方法，都不能被混淆   #保持 任意包名.R类的类成员属性。  即保护R文件中的属性名不变
-keepclassmembers class **.R$* {
    *;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}

#内嵌类
-keep class com.xingfabu.direct.WatchHLSActivity* {
    *;
}

#--------webview----------
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
#对JavaScript的处理
#-keepclassmembers class com.null.test.MainActivity$JSInterfacel {
#    <methods>;
#}

#--------webview----------
#--------okhttputils------
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
#--------okhttputils------

#-----------okhttp---------
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#-----------okhttp---------

#------------okio----------
-dontwarn okio.**
-keep class okio.**{*;}
#------------okio----------

#----------youmeng---------------
 -dontwarn com.google.android.maps.**
 -dontwarn android.webkit.WebView
 -dontwarn com.umeng.**
 -dontwarn com.tencent.weibo.sdk.**
 -dontwarn com.facebook.**
 -keep public class javax.**
 -keep public class android.webkit.**
 -dontwarn android.support.v4.**
 -keep enum com.facebook.**

 -keep public interface com.facebook.**
 -keep public interface com.tencent.**
 -keep public interface com.umeng.socialize.**
 -keep public interface com.umeng.socialize.sensor.**
 -keep public interface com.umeng.scrshot.**

 -keep public class com.umeng.socialize.* {*;}


 -keep class com.facebook.**
 -keep class com.facebook.** { *; }
 -keep class com.umeng.scrshot.**
 -keep public class com.tencent.** {*;}
 -keep class com.umeng.socialize.sensor.**
 -keep class com.umeng.socialize.handler.**
 -keep class com.umeng.socialize.handler.*
 -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
 -keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

 -keep class im.yixin.sdk.api.YXMessage {*;}
 -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

 -dontwarn twitter4j.**
 -keep class twitter4j.** { *; }

 -keep class com.tencent.** {*;}
 -dontwarn com.tencent.**
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep class com.tencent.open.TDialog$*
 -keep class com.tencent.open.TDialog$* {*;}
 -keep class com.tencent.open.PKDialog
 -keep class com.tencent.open.PKDialog {*;}
 -keep class com.tencent.open.PKDialog$*
 -keep class com.tencent.open.PKDialog$* {*;}

 -keep class com.sina.** {*;}
 -dontwarn com.sina.**
 -keep class  com.alipay.share.sdk.** {
    *;
 }

 -keep class com.linkedin.** { *; }
#----------youmeng---------------

# ----------Glide-------
-keep class com.xingfabu.direct.transform.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# ----------Glide-------

# ------------vhall----------
-dontwarn com.vhall.**
-dontwarn com.vinny.**
-keep class com.vhall.**{*;}
-keep class com.vinny.**{*;}
# ------------vhall----------

#---------支付宝支付----------------------------
-dontwarn com.alipay.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
#---------支付宝支付----------------------------

#----------微信支付-----------------------------
-dontwarn com.tencent.mm.sdk.**
-keep class com.tencent.mm.sdk.** {
   *;
}
#------------友盟统计---------------------
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.xingfabu.direct.R$*{
public static final int *;
}
#------------友盟统计---------------------

#------------友盟推送---------------------
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep public class **.R$*{
   public static final int *;
}
#------------友盟推送---------------------