package com.example.jrs300.shareinsecret;

import android.util.Log;
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
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 09/07/14.
 */
public class FileCryptor {

        public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        public static final String KEY_ALGORITHM = "AES";
        public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits

        private SecretKeySpec myKeySpec;
        private byte[] iv = null;

        //constructor when new key is required
        public FileCryptor() {

            keyGen();  //initialise myKeySpec
        }

        //constructor when existing key should be used


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
                Log.e("AES","Error generating secret key spec");
            }
        }

        /**
         * Encrypt input stream and outputs result to a file
         * @param plaintextAsFileIn
         * @param ciphertextAsFileOut
         * @throws NoSuchAlgorithmException
         * @throws NoSuchPaddingException
         * @throws InvalidKeyException
         * @throws IOException
         */
        public  void encryptFile(FileInputStream plaintextAsFileIn, FileOutputStream ciphertextAsFileOut )
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {

            Cipher encryptionCipher;
            encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE,this.myKeySpec);

            CipherOutputStream cos;

            cos = new CipherOutputStream(ciphertextAsFileOut, encryptionCipher);
            byte[] block = new byte[AES_BLOCKSIZE];
            int bytesRead = plaintextAsFileIn.read(block);
            while (bytesRead != -1) {
                cos.write(block, 0, bytesRead);
                bytesRead = plaintextAsFileIn.read(block);
            }
            cos.close();

            //record IV
            this.iv = encryptionCipher.getIV();
            return;
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
        public  void decryptFile(FileInputStream ciphertextAsFileIn, FileOutputStream  plaintextAsFileOut )
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {

            CipherInputStream cis;

            //initialise encryption cipher
            Cipher decryptionCipher;

            decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            decryptionCipher.init(Cipher.DECRYPT_MODE,this.myKeySpec,new IvParameterSpec(this.iv));
            cis = new CipherInputStream(ciphertextAsFileIn,decryptionCipher );

            //read and encrypt file

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



        public  void encryptString(String plaintextAsString, FileOutputStream ciphertextAsFileOut )
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

            Cipher encryptionCipher;
            encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE,this.myKeySpec);

            //CipherOutputStream cos;

            //cos = new CipherOutputStream(ciphertextAsFileOut, encryptionCipher);

            //convert String to byte array using UTF-8 encoding

            byte[] StringAsByteArray = plaintextAsString.getBytes("UTF-8");

            byte[] ciphertextAsByteArray = encryptionCipher.doFinal(StringAsByteArray);

            ciphertextAsFileOut.write(ciphertextAsByteArray);
            ciphertextAsFileOut.close();

            //record IV
            this.iv = encryptionCipher.getIV();
            return;
        } //end EncryptFile


    } //end FileCryptor

