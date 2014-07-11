package com.example.jrs300.shareinsecret;

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
 * Created by jrs300 on 09/07/14.
 */
public class FileCryptor {

        public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits

        private FileCryptor() {
            // dummy constructor to prevent accidental instantiation
        }

        /**
         * Encrypt input stream and outputs result to a file
         * @param plaintextAsFileIn
         * @param ciphertextAsFileOut
         * @throws NoSuchAlgorithmException
         * @throws NoSuchPaddingException
         * @throws InvalidKeyException
         * @throws IOException
         * @throws InvalidAlgorithmParameterException
         */
        public static  KeyManagement encryptFile(FileInputStream plaintextAsFileIn, FileOutputStream ciphertextAsFileOut )
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException{

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

    } //end FileCryptor



