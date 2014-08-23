package com.example.julie.securelyshare;

/**
 * Created by jrs300 on 04/08/14.
 */
public class WrongPwdException extends Exception {
   public WrongPwdException() {
        super("Keystore password not found.");
    }

}