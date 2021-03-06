package com.example.julie.securelyshare;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;


/**
 * Created by jrs300 on 09/07/14.
 */
public class FileCryptor {

    private FileCryptor() {
        // dummy constructor to prevent accidental instantiation
    }


    public static  void encryptFile(FileInputStream fis,FileOutputStream fos, String groupID)
            throws MyKeystoreAccessException, IOException, MyMissingKeyException {

        MyCipher encryptionCipher = new MyCipher(groupID);

        //write metadata to FileOutputStream
        fos.write(encryptionCipher.getGroupLength());
        fos.write(encryptionCipher.getGroupAsByteArray());
        fos.write(encryptionCipher.getIv());

        //wrap fos in cipherstream to encrypt remaining blocks
        CipherOutputStream cos = new CipherOutputStream(fos, encryptionCipher.getmCipher());
        byte[] block = new byte[1024*MyCipher.AES_BLOCKSIZE];
        int bytesRead = fis.read(block);

        //write data to output file and read next block
        while (bytesRead != -1) {
            cos.write(block, 0, bytesRead);
            bytesRead = fis.read(block);
        }
        fis.close();
        cos.close();
          } //end EncryptFile


    public static void encryptString(String plaintextAsString, FileOutputStream fos, String groupID)
            throws MyKeystoreAccessException, GeneralSecurityException, IOException, MyMissingKeyException {

        MyCipher encryptionCipher = new MyCipher(groupID);

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

    /********************************************************************************************************************************
     *
     * @param fis
     * @param fos
     * @return
     * @throws MyKeystoreAccessException
     */

    public static String decryptFile(FileInputStream fis, FileOutputStream  fos)
            throws MyKeystoreAccessException, MyMissingKeyException {
        MyCipher decryptionCipher=null;
        // read meta data from input stream
int b = 0;
        byte [] bytes = new byte[4];
               try {
                   b=fis.read(bytes);
                   int len = ByteBuffer.wrap(bytes).getInt();
                   byte[] gaba = new byte[len];
                   b=fis.read(gaba);
                   byte[] initVector = new byte[MyCipher.AES_BLOCKSIZE];
                   b=fis.read(initVector);

        //setup decryption
        decryptionCipher = new MyCipher(gaba, initVector);

        CipherInputStream cis = new CipherInputStream(fis,decryptionCipher.getmCipher() );

        //read and decrypt file
        byte[] block = new byte[1024*MyCipher.AES_BLOCKSIZE];
        int bytesRead=0;
        bytesRead = cis.read(block);
        while (bytesRead != -1) {
            fos.write(block,0,bytesRead);
            bytesRead = cis.read(block);  //read next block
        }
                   fos.close();
                   cis.close();
                   fis.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
        return decryptionCipher.getGroupID();
    } //end decryptFile





    public static String decryptToString(FileInputStream fis)
            throws MyKeystoreAccessException, MyMissingKeyException {
        MyCipher decryptionCipher=null;
        String outString = "";
        // read meta data from input stream
        int b = 0;
        byte [] bytes = new byte[4];
        try {
            b=fis.read(bytes);
            int len = ByteBuffer.wrap(bytes).getInt();
            byte[] gaba = new byte[len];
            b=fis.read(gaba);
            byte[] initVector = new byte[MyCipher.AES_BLOCKSIZE];
            b=fis.read(initVector);

            //setup decryption
            decryptionCipher = new MyCipher(gaba, initVector);

            CipherInputStream cis = new CipherInputStream(fis,decryptionCipher.getmCipher() );

            //read and decrypt file
            byte[] block = new byte[1024*MyCipher.AES_BLOCKSIZE];
            int bytesRead=0;
            bytesRead = cis.read(block);


            while (bytesRead != -1) {
                outString = outString + new String(block, "UTF-8");

                bytesRead = cis.read(block);  //read next block
            }

            cis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outString;
    } //end decryptFile

} //end FileCryptor




