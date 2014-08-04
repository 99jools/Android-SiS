package com.example.jrs300.shareinsecrettest;

/**
 * Created by jrs300 on 01/08/14.
 */
public class AppPwdObj {

    private static AppPwdObj appPwdObj = null;
    private String value = null;

    private AppPwdObj() {
    this.value =  "mypassword";
    }

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

    public void setValue(String value){
        this.value = value;
    }
}
