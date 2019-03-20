/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午5:16
 * ********************************************************
 */

package app;



import com.telchina.libskin.load.SkinManager;

import com.zcolin.frame.app.BaseApp;
import com.zcolin.frame.http.okhttp.OkHttpUtils;
import com.zcolin.frame.http.okhttp.cookie.CookieJarImpl;
import com.zcolin.frame.http.okhttp.cookie.store.MemoryCookieStore;
import com.zcolin.frame.http.okhttp.https.HttpsUtils;
import com.zcolin.frame.util.LogUtil;
import com.zcolin.frame.util.ScreenUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 程序入口
 */
public class App extends BaseApp {
    public static boolean      isPad;  //是否是（pad或者大于7寸的手机）

    @Override
    public void onCreate() {
        super.onCreate();
        //if (LeakCanary.isInAnalyzerProcess(this)) {
        //     // This process is dedicated to LeakCanary for heap analysis.
        //     // You should not init your app in this process.
        //     return;
        // }
        // LeakCanary.install(this);
        // Normal app init code...

        //facebook调试工具
        //if (BuildConfig.DEBUG) {
        //    Stetho.initializeWithDefaults(this);
        //}
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();
        isPad = ScreenUtil.isTablet(this);
//        LogUtil.LOG_DEBUG = BuildConfig.DEBUG;
//        initHttpOptions();
//        tsfConfig = new TSFConfig(); //todo 设置默认配置类，可自定义设置
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    private void initHttpOptions() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //facebook调试工具
        //if (BuildConfig.DEBUG) {
        //    builder.addNetworkInterceptor(new StethoInterceptor());
        //}
        OkHttpClient okHttpClient = builder.connectTimeout(10000L, TimeUnit.MILLISECONDS)
                                           .readTimeout(10000L, TimeUnit.MILLISECONDS)
                                           .cookieJar(cookieJar1)
                                           .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                                           .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
