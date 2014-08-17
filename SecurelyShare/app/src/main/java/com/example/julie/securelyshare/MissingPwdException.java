package com.example.julie.securelyshare;

/**
 * Created by jrs300 on 04/08/14.
 */
public class MissingPwdException extends Exception {
   public MissingPwdException() {
        super("Keystore password not found.");
    }

}