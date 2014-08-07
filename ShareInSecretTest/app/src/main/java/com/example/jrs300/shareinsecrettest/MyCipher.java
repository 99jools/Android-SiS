package com.example.jrs300.shareinsecrettest;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
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

    private Cipher mCipher;
    private String groupID;
    private byte[] iv;
    private byte[] groupAsByteArray;
    private SecretKeySpec groupSKS;


    public MyCipher(String groupID, char mode) throws MissingPwdException {

        this.groupSKS = AppKeystore.getKeySpec(groupID);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            if (mode == 'E') {
                cipher.init(Cipher.ENCRYPT_MODE, groupSKS);
                this.iv =  cipher.getIV();  //array length will be equivalent to AES blocksize
            }
            else cipher.init(Cipher.DECRYPT_MODE, groupSKS);

            this.mCipher = cipher;
            this.groupID = groupID;
            this.groupAsByteArray = groupID.getBytes("UTF-8");

        } catch (GeneralSecurityException e) {
            Log.e("MyCipher: ", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e("MyCipher: ", e.getMessage());
        }
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
        return groupAsByteArray;
    }

    public SecretKeySpec getGroupSKS() {
        return groupSKS;
    }
}