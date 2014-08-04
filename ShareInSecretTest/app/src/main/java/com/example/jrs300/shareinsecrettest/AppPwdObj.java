package com.example.jrs300.shareinsecrettest;

import java.io.IOException;

/**
 * Created by jrs300 on 01/08/14.
 */
public class AppPwdObj {

    private static AppPwdObj appPwdObj = null;
    private String value = null;

    private AppPwdObj() {
    this.value = "mypassword";
    }  //this is just here for testing in case object is destroyed - remove once password fragment available

    public static AppPwdObj getInstance() {
        if (appPwdObj == null)
            return appPwdObj = new AppPwdObj();
        else
            return appPwdObj;
    }

    public String getValue() throws MissingPwdException{
        if  (value==null)
            //password has not been initialised - throw exception
            throw new MissingPwdException();

        return this.value;
    }

    public boolean setValue(String value) throws IOException{
        this.value = value;
        //check that it is possible to access Keystore
        return  AppKeystore.validate("Dummy");
    }
}
