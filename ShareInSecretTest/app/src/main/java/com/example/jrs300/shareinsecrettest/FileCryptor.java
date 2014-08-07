package com.example.jrs300.shareinsecrettest;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by jrs300 on 09/07/14.
 */
public class FileCryptor {

    private FileCryptor() {
        // dummy constructor to prevent accidental instantiation
    }


    public static  void encryptFile(FileInputStream fis,FileOutputStream fos, String groupID, SharedPrefs prefs)
            throws MissingPwdException, IOException {

        MyCipher encryptionCipher = new MyCipher(groupID, 'E');

        //write metadata to FileOutputStream
        fos.write(encryptionCipher.getGroupLength());
        fos.write(encryptionCipher.getGroupAsByteArray());
        fos.write(encryptionCipher.getIv());

        //wrap fos in cipherstream to encrypt remaining blocks
        CipherOutputStream cos = new CipherOutputStream(fos, encryptionCipher.getmCipher());
        byte[] block = new byte[MyCipher.AES_BLOCKSIZE];
        int bytesRead = fis.read(block);

        //write data to output file and read next block
        while (bytesRead != -1) {
            cos.write(block, 0, bytesRead);
            bytesRead = fis.read(block);
        }
        fis.close();
        cos.close();
          } //end EncryptFile

    public static void encryptString(String plaintextAsString, FileOutputStream fos, String groupID, SharedPrefs prefs)
            throws MissingPwdException,GeneralSecurityException, IOException {

        MyCipher encryptionCipher = new MyCipher(groupID, 'E');

        //write metadata to FileOutputStream
        fos.write(encryptionCipher.getGroupLength());
        fos.write(encryptionCipher.getGroupAsByteArray());
        fos.write(encryptionCipher.getIv());

        //convert String to byte array using UTF-8 encoding and convert to encrypted array
        byte[] stringAsByteArray = plaintextAsString.getBytes("UTF-8");
        byte[] ciphertextAsByteArray = encryptionCipher.getmCipher().doFinal(stringAsByteArray);

        //write out encrypted file
        fos.write(ciphertextAsByteArray);
        fos.close();

    } //end encryptString



    public static String decryptFile(FileInputStream fis, FileOutputStream  fos, SharedPrefs prefs)
            throws MissingPwdException, GeneralSecurityException, IOException {

        // read meta data from input stream
        byte[] initVector = new byte[AES_BLOCKSIZE];
        int groupCode = fis.read();
        String groupID = prefs.getID(groupCode);
        fis.read(initVector,0,AES_BLOCKSIZE);
    IvParameterSpec ips = new IvParameterSpec(initVector);
        //setup decryption
        Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        KeySpec k = AppKeystore.getKeySpec(groupID);
        decryptionCipher.init(Cipher.DECRYPT_MODE,AppKeystore.getKeySpec(groupID),ips);
        CipherInputStream cis = new CipherInputStream(fis,decryptionCipher );

        //read and decrypt file
        byte[] block = new byte[AES_BLOCKSIZE];
        int bytesRead;
        bytesRead = cis.read(block);
        while (bytesRead != -1) {
            fos.write(block,0,bytesRead);
            bytesRead = cis.read(block);  //read next block
        }
        //close file
        fos.close();
        cis.close();
        fis.close();

        return groupID;
    } //end decryptFile







//********************************************************************************************

    public static IvParameterSpec dummyIV(){
        //set up dummy iv - CHANGE THIS
        byte ff = (byte) 0xff;
        byte[] dummyIV = {ff,ff,ff,ff,0x00,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff};
        return new IvParameterSpec(dummyIV);
    }


} //end FileCryptor




