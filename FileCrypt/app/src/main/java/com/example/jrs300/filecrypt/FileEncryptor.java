package com.example.jrs300.filecrypt;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by jrs300 on 10/07/14.
 */
public class FileEncryptor {

    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits

    private FileEncryptor() {
        // dummy constructor to prevent accidental instantiation
    }

    /**
     * Encrypt input stream and outputs result to a file
     * @param plaintextAsFileIn
     * @param ciphertextAsFileOut
     * @throws java.security.NoSuchAlgorithmException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.InvalidKeyException
     * @throws java.io.IOException
     * @throws java.security.InvalidAlgorithmParameterException
     */
    public static  KeyManagement encryptFile(FileInputStream plaintextAsFileIn, FileOutputStream ciphertextAsFileOut )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {

        //get a new encryption key - THIS FUNCTIONALITY TO BE MOVED LATER
        KeyManagement newKey = new KeyManagement();

        //set up cipher for encryption
        IvParameterSpec ips = makeIV();
        Cipher encryptionCipher;
        encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, newKey.getMyKeySpec(), ips);

        ciphertextAsFileOut.write(ips.getIV(),0,AES_BLOCKSIZE);
        CipherOutputStream cos = new CipherOutputStream(ciphertextAsFileOut, encryptionCipher);
        byte[] block = new byte[AES_BLOCKSIZE];
        int bytesRead = plaintextAsFileIn.read(block);
        while (bytesRead != -1) {
            cos.write(block, 0, bytesRead);
            bytesRead = plaintextAsFileIn.read(block);
        }
        cos.close();
        return newKey;
    } //end EncryptFile


    /**
     * Decrypts an input stream and outputs result to a file
     * @param ciphertextAsFileIn
     * @param plaintextAsFileOut
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     */
    public static  void decryptFile(FileInputStream ciphertextAsFileIn, FileOutputStream  plaintextAsFileOut, KeyManagement decryptKey )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException{
        decryptKey.dummyKey();
        CipherInputStream cis;

        //initialise encryption cipher
        Cipher decryptionCipher;

        // read initialisation vector from input stream
        byte[] initVector = new byte[16];
        ciphertextAsFileIn.read(initVector);
        IvParameterSpec ips = new IvParameterSpec(initVector);

        decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        decryptionCipher.init(Cipher.DECRYPT_MODE,decryptKey.getMyKeySpec(),ips);

        cis = new CipherInputStream(ciphertextAsFileIn,decryptionCipher );

        //read and decrypt file
        byte[] block = new byte[AES_BLOCKSIZE];
        int bytesRead;

        bytesRead = cis.read(block);
        while (bytesRead != -1) {
            plaintextAsFileOut.write(block,0,bytesRead);
            bytesRead = cis.read(block);  //read next block
        }
        //close file
        plaintextAsFileOut.close();
        return;
    } //end decryptFile


    public static KeyManagement encryptString(String plaintextAsString, FileOutputStream ciphertextAsFileOut )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{

        //get a new encryption key - THIS FUNCTIONALITY TO BE MOVED LATER
        KeyManagement newKey = new KeyManagement();

        //set up dummy iv - CHANGE THIS
        IvParameterSpec ips = makeIV();

        Cipher encryptionCipher;
        encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, newKey.getMyKeySpec(), ips);

        //convert String to byte array using UTF-8 encoding

        byte[] StringAsByteArray = plaintextAsString.getBytes("UTF-8");

        byte[] ciphertextAsByteArray = encryptionCipher.doFinal(StringAsByteArray);

        //write out IV to output file first
        ciphertextAsFileOut.write(ips.getIV());

        //write out encrypted file
        ciphertextAsFileOut.write(ciphertextAsByteArray);
        ciphertextAsFileOut.close();

        return newKey;
    } //end encryptString


    public static IvParameterSpec makeIV(){

        //set up dummy iv - CHANGE THIS
        byte ff = (byte) 0xff;
        byte[] dummyIV = {ff,ff,ff,ff,0x00,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff};
        return new IvParameterSpec(dummyIV);
    }




       /**
     * takes a string of text, encrypts and writes to a file
     * @param cipherText

    public void writeToFile(String cipherText){

        if (isExternalStorageWritable()) {

            try {

                FileOutputStream fos = openFileOutput("DayTwentyTwoFile", Context.MODE_APPEND);
                fos.write(cipherText.getBytes());
                fos.close();

                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {

                    File file = new File(getExternalFilesDir(null),
                            "DayTwentyTwoFileTwo.enc");

                    FileOutputStream fos2 = new FileOutputStream(file);

                    //   fos2.write(cipherText.getBytes());

                    fos2.close();

                }

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
        else Log.e("writefile", "external storage is not writeable");

    }
*/
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String encrypt (String text)
    {
        return text.toUpperCase();
    }
}

