package com.example.julie.securelyshare;

/**
 * Created by jrs300 on 04/08/14.
 */
public class MyKeystoreAccessException extends Exception {
   public MyKeystoreAccessException() {

       super("Keystore password not found.");
    }

}