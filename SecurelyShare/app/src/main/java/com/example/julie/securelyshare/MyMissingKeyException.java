package com.example.julie.securelyshare;

/**
 * Created by Julie on 27/08/2014.
 */
public class MyMissingKeyException  extends Exception {
    public MyMissingKeyException() {

        super("Not able to retrieve key for this GroupID");
    }

}
