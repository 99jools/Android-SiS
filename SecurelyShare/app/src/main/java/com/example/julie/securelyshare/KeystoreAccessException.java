package com.example.julie.securelyshare;

/**
 * Created by jrs300 on 04/08/14.
 */
public class KeystoreAccessException extends Exception {
   public KeystoreAccessException() {
        super("Keystore password not found.");
    }

}