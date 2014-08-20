package com.example.julie.securelyshare;

import android.content.Context;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

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

    public String getValue() throws MissingPwdException{
        if  (value==null)
            //password has not been initialised - throw exception
            throw new MissingPwdException();

        return this.value;
    }

    public boolean setValue(String value){
        this.value = value;
        //check that it is possible to access Keystore
        try {
            AppKeystore aks = new AppKeystore();
                  aks.validate();
            return true;
        } catch (MissingPwdException e) {
            e.printStackTrace();
            //this should not occur as we have just set the password above!!
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Context getContext(){
        return this.context;
    }
}
