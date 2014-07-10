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

    //constructor
    public KeyManagement() {
        keyGen();
        dummyKey();
    }

    // getters and setters
    public SecretKeySpec getMyKeySpec() {
        return myKeySpec; }

    public void setMyKeySpec(SecretKeySpec myKeySpec) {
        this.myKeySpec = myKeySpec;
    }
    /**
     * generateKey method called to generate a new encryption key for AES encryption using 128 bit key
     */
    private void keyGen(){

        try {
            KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            myKeyGenerator.init(128);
            this.myKeySpec = new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
            //		System.out.println("generated key" +(javax.xml.bind.DatatypeConverter.printHexBinary(this.myKeySpec.getEncoded())));
        }
        catch (Exception e) {
            System.out.println( "AES - error generating secret key spec");
        }
    }

    public void dummyKey(){
        //set up dummy key - CHANGE THIS
        byte[] dummykey ={0x00, 0x11, 0x11, 0x11, 0x00, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x01, 0x11,0x11, 0x11, 0x11};
        this.myKeySpec = new SecretKeySpec(dummykey, KEY_ALGORITHM);
    }

}














}
