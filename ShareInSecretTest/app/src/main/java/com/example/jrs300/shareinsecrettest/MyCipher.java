package com.example.jrs300.shareinsecrettest;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Creates a a Cipher object based on the particular encryption key retrieved from
 * the Keystore for the group in question
 *
 * This can be constructed as either an encryption or decryption cipher depending on the input parameters
 * Created by jrs300 on 07/08/14.
 */
public class MyCipher {
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits
    public static final int AES_KEYLENGTH = 256;  //may decide to make this a user option later

    private SecretKeySpec groupSKS;
    private String groupID;
    private Cipher mCipher;
    private byte[] iv;

    public MyCipher(String groupID) throws MissingPwdException {
Log.e("encrypt", groupID);
        this.groupSKS = AppKeystore.getKeySpec(groupID);
        this.groupID = groupID;

        try {

            this.mCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            mCipher.init(Cipher.ENCRYPT_MODE, groupSKS);
            this.iv =  mCipher.getIV();  //array length will be equivalent to AES blocksize
        } catch (GeneralSecurityException e) {
            Log.e("MyCipher: ", e.getMessage());
        }
    }

    public MyCipher(byte[] gaba, byte[] iv ) throws MissingPwdException {
        try {

            this.iv = iv;
            IvParameterSpec ips =  new IvParameterSpec(iv);
            this.groupID = new String(gaba, "UTF-8");
            this.groupSKS = AppKeystore.getKeySpec(groupID);
            this.mCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            mCipher.init(Cipher.DECRYPT_MODE, groupSKS,ips);
        } catch (GeneralSecurityException e) {
            Log.e("MyCipher: ", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("decrypt", groupID);
    }

    public Cipher getmCipher() {
        return mCipher;
    }

    public String getGroupID() {
        return groupID;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getGroupAsByteArray() {
        try {
            return groupID.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("MyCipher: ", e.getMessage());
        }
        return null;
    }


    public SecretKeySpec getGroupSKS() {
        return groupSKS;
    }

    public byte[] getGroupLength(){
        int len = this.getGroupAsByteArray().length;
        return ByteBuffer.allocate(4).putInt(len).array();
    }
}