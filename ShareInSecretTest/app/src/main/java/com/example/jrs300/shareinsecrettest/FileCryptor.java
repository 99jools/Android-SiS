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


    /**
     * Encrypt input stream and outputs result to a file - location of output stream is set up in calling
     * @param fis
     * @param fos
     * @param groupID
     * @param prefs
     * @throws MissingPwdException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static  void encryptFile(FileInputStream fis,FileOutputStream fos, String groupID, SharedPrefs prefs)
            throws MissingPwdException, IOException, GeneralSecurityException{

        MyCipher encryptionCipher = new MyCipher(groupID, 'E');


        //write metadata to FileOutputStream
        fos.write(prefs.getCode(groupID));  	 						 //writes 4 byte groupCde
        fos.write(encryptionCipher.getIV(),0,AES_BLOCKSIZE);            //IV length depends on blocksize

        //wrap fos in cipherstream to encrypt remaining blocks
        CipherOutputStream cos = new CipherOutputStream(fos, encryptionCipher);
        byte[] block = new byte[AES_BLOCKSIZE];
        int bytesRead = fis.read(block);

        //write data to output file and read next block
        while (bytesRead != -1) {
            cos.write(block, 0, bytesRead);
            bytesRead = fis.read(block);
        }
        fis.close();
        cos.close();
          } //end EncryptFile


    /**
     * Decrypts a fileoutputstream
     * @param fis file input stream
     * @param fos file output stream
     * @param prefs shared preferences
     * @return returns the groupID
     * @throws GeneralSecurityException
     * @throws IOException
     */
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


    /**
     * Encrypt a String and write to a given FileOutputStream
     * @param plaintextAsString
     * @param fos
     * @param groupID
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void encryptString(String plaintextAsString, FileOutputStream fos, String groupID, SharedPrefs prefs)
            throws MissingPwdException,GeneralSecurityException, IOException {

        Cipher encryptionCipher = initEncryptCipher(groupID);

        //convert String to byte array using UTF-8 encoding and convert to encrypted array
        byte[] stringAsByteArray = plaintextAsString.getBytes("UTF-8");
        byte[] ciphertextAsByteArray = encryptionCipher.doFinal(stringAsByteArray);

        //write metadata to FileOutputStream
        fos.write(prefs.getCode(groupID));  	 						 //writes 4 byte hashcode
        fos.write(encryptionCipher.getIV(),0,AES_BLOCKSIZE);  //IV length depends on blocksize

        //write out encrypted file
        fos.write(ciphertextAsByteArray);
        fos.close();

    } //end encryptString




//********************************************************************************************

    public static IvParameterSpec dummyIV(){
        //set up dummy iv - CHANGE THIS
        byte ff = (byte) 0xff;
        byte[] dummyIV = {ff,ff,ff,ff,0x00,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff};
        return new IvParameterSpec(dummyIV);
    }

} //end FileCryptor




