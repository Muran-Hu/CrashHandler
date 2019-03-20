package com.droid;

import android.app.Application;

/**
 * Created by Muran Hu on 2019-03-20.
 * Email: muranhu@gmail.com
 * Version: v1.0.0
 */
public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    CrashHandler crashHandler = CrashHandler.getInstance();
    crashHandler.init(this);
  }
}
