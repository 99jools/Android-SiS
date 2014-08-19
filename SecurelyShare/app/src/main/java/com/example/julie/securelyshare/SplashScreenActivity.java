package com.example.julie.securelyshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;
/*
Splash screen implementation based on code from http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
 */

public class SplashScreenActivity extends Activity {
    private static final int SHOW_SPLASH = 3000;
    public static final String CERTIFICATE_FILE = "SiSCert.ks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

      // delayed start is done by using a Handler to add the Runnable to the message queueto run after delay period
      new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //check if keystore exists
               if (new File(CERTIFICATE_FILE).isFile()) {
                  Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                  startActivity(i);
               } else {
                   Intent i = new Intent(SplashScreenActivity.this, InitActivity.class);
                 i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                  startActivity(i);
              }

               // close this activity
                finish();
            }
        }, SHOW_SPLASH);
    }
}