package com.droid;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final RxPermissions rxPermissions = new RxPermissions(this);
    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
              @Override
              public void accept(Boolean granted) throws Exception {
                if (granted) {
                  Toast.makeText(MainActivity.this, "granted", Toast.LENGTH_LONG).show();
                } else {
                  Toast.makeText(MainActivity.this, "Not granted", Toast.LENGTH_LONG).show();
                }
              }
            });
  }

  public void click(View view) {
    throw new RuntimeException("Custom exception");
  }
}
