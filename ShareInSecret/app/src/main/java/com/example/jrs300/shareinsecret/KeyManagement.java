package com.example.jrs300.shareinsecret;

import android.util.Log;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 09/07/14.
 */
public class KeyManagement {

    public static final String KEY_ALGORITHM = "AES";
    private SecretKeySpec myKeySpec;

    /**
     * generateKey method called to generate a new encryption key for AES encryption using 128 bit key
     */
    private void keyGen(){

        try {
            KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            myKeyGenerator.init(128);
            this.myKeySpec = new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
        }
        catch (Exception e) {
            Log.e("AES", "Error generating secret key spec");
        }
    }














}
