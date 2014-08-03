package com.example.jrs300.shareinsecret;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jrs300 on 01/08/14.
 */
public class AppPwdObj {

    private static AppPwdObj appPwdObj = null;
    private Context appContext = null;
    private String value = null;

    private AppPwdObj(Context appContext) {
        this.appContext = appContext;
    }

    public static AppPwdObj getInstance(Context appContext) {
        if (appPwdObj == null)
            return appPwdObj = new AppPwdObj(appContext);
        else
            return appPwdObj;
    }

    public String getValue(){
        if  (value==null){
            //password has not been initialised - start activity to request user input
            //create intent for Create Activity
            Intent intent = new Intent(appContext, ChooserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity( intent );
        }

        return this.value;
    }

    public void setValue(String value){
        this.value = value;
        return;
    }
}
