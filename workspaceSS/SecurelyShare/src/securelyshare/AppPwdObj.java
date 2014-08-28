package securelyshare;

import java.io.IOException;

/**
 * Created by jrs300 on 01/08/14.
 */
public class AppPwdObj {

    private static AppPwdObj appPwdObj = null;
    private String value = null;

    private AppPwdObj() {

    } 
    
    public static AppPwdObj makeObj() {
        if (appPwdObj == null)
            return appPwdObj = new AppPwdObj();
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

    public boolean setValue(String value) throws IOException{
        this.value = value;
        //check that it is possible to access Keystore
        return  AppKeystore.validate(value);
    }
}
