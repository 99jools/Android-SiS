package com.example.julie.securelyshare;

import android.content.Context;

/**
 * Created by jrs300 on 01/08/14.
 */
public class AppPwdObj {

    private static AppPwdObj appPwdObj = null;
    private String value = null;
    private final Context context;

    private AppPwdObj(Context context) {
        this.context = context;
    }  //this is just here for testing in case object is destroyed - remove once password fragment available

    public static AppPwdObj makeObj(Context context) {
        if (appPwdObj == null)
            return appPwdObj = new AppPwdObj(context);
        else
            return appPwdObj;
    }

    public static AppPwdObj getInstance(){
        return appPwdObj;
    }

    public String getValue(){

        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        return;
    }

    public Context getContext(){
        return this.context;
    }
}