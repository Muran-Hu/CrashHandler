package com.droid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.ToDoubleBiFunction;

/**
 * Created by Muran Hu on 2019-03-20.
 * Email: muranhu@gmail.com
 * Version: v1.0.0
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
  private static final String TAG = "CrashHandler";
  private static final boolean DEBUG = true;
  private static final  String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
  private static final String FILE_NAME = "crash";
  private static final String FILE_NAME_SUFFIX = ".trace";

  private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
  private Context mContext;

  private static CrashHandler crashHandler = null;

  private CrashHandler() {}

  public static CrashHandler getInstance() {
    if (null == crashHandler) {
      synchronized (CrashHandler.class) {
        if (null == crashHandler) {
          crashHandler = new CrashHandler();
        }
      }
    }

    return crashHandler;
  }

  public void init(Context context) {
    mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
    mContext = context.getApplicationContext();
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    dumpExceptionToSDCard(e);

    uploadExceptionToServer();

    e.printStackTrace();

    if (null != mDefaultCrashHandler) {
      mDefaultCrashHandler.uncaughtException(t, e);
    } else {
      Process.killProcess(Process.myPid());
    }
  }

  private void dumpExceptionToSDCard(Throwable e) {
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      if (DEBUG) {
        Log.w(TAG, "dumpExceptionToSDCard: sdcard unmounted, skip dump exception");
        return;
      }
    }

    File dir = new File(PATH);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    long current = System.currentTimeMillis();
    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));

    File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

    try {
      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      pw.println(time);
      dumpPhoneInfo(pw);
      pw.println();
      e.printStackTrace(pw);
      pw.close();
    } catch (Exception e1) {
      Log.e(TAG, "dumpExceptionToSDCard: dump crash info failed");
    }
  }

  private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
    PackageManager pm = mContext.getPackageManager();
    PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
    pw.print("App Version: ");
    pw.print(pi.versionName);
    pw.print("_");
    pw.println(pi.versionCode);

    // Android 版本号
    pw.print("OS Version: ");
    pw.print(Build.VERSION.RELEASE);
    pw.print("_");
    pw.println(Build.VERSION.SDK_INT);

    // 手机制造商
    pw.print("Vendor: ");
    pw.println(Build.MANUFACTURER);

    // 手机型号
    pw.print("Model: ");
    pw.println(Build.MODEL);

    // CPU 架构
    pw.print("CPU ABI: ");
    pw.println(Build.CPU_ABI);
  }

  private void uploadExceptionToServer() {
    // TODO Upload Exception Message To Web Server
  }
}
