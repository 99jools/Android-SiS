package com.example.julie.securelyshare;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 01/08/14.
 */

public class AppKeystore {

    public static final String KEY_ALGORITHM = "AES";
    public static final String KEYPAIR_ALGORITHM = "RSA";
    public static final int KEY_LENGTH = 128;
    public static final int KEYPAIR_LENGTH = 1024;
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
    public static final String KEYSTORE_TYPE = "BKS";

    private char[] appPwdAsArray;
    private KeyStore ks;
    Context context;

    public AppKeystore() throws MissingPwdException{
        String appPwd = AppPwdObj.getInstance().getValue();
        this.appPwdAsArray = appPwd.toCharArray();
        this.context =  AppPwdObj.getInstance().getContext();
        try {
            this.ks = loadKeyStore();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieves the key from the Keystore corresponding to the supplied alias and wraps it as SecretKeySpec
     * @param alias
     * @return KeySpec for the retrieved key
     */
    public SecretKeySpec getSKS(String alias) {
        try {
            if (ks.containsAlias(alias)) {
                Key groupKey = ks.getKey(alias, appPwdAsArray);
                return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * with reference to code from http://www.macs.hw.ac.uk/~ml355/lore/pkencryption.htm
     */
    public void importGroupKey(String groupID, File groupKeyFile) throws GeneralSecurityException, IOException{
        // get private key to decrypt key file
        PrivateKey privateKey = getPrivateKey();

        //set up Cipher to decrypt groupKeyFile
        Cipher deCipher = Cipher.getInstance(KEYPAIR_ALGORITHM);
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);

        //read and decrypt encoded group key from file
        byte[] groupKey = new byte[KEY_LENGTH/8];   //need to convert to bytes
        FileInputStream fis = new FileInputStream(groupKeyFile);

        CipherInputStream cis = new CipherInputStream(fis, deCipher);
        cis.read(groupKey);

        //add to key store
        SecretKeySpec sks = new SecretKeySpec(groupKey, KEY_ALGORITHM);
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();

    }


    /****************************************************************************************************************
     * Lists all the groups in the keystore
     * @throws MissingPwdException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void listGroups() throws MissingPwdException, GeneralSecurityException, IOException {
        Enumeration<String> es = ks.aliases();
        for (String key : Collections.list(es)){
            Log.e("key found", key);
        }
    } //end listGroups

    //**********************************************************************************************************************************************



    /****************************************************************************************************************
     * Updates the stored copy of the keystore
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private void writeKeyStore() throws GeneralSecurityException, IOException {
        FileOutputStream fos = context.openFileOutput(KEYSTORE_NAME, Context.MODE_PRIVATE);
        try {
            ks.store(fos, appPwdAsArray);
        } finally {
            fos.close();
        }
    } //end writeKeyStore



    /**
     * Loads the key store from disc
     * @return the keystore
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private KeyStore loadKeyStore() throws IOException, GeneralSecurityException {
        KeyStore myks = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(KEYSTORE_NAME);
            myks.load(fis, appPwdAsArray);
        } finally {
            if (fis != null) fis.close();
        }
        return myks;
    } //end loadKeyStore






    private PrivateKey getPrivateKey(){
        PrivateKey key = null;
        try {
            key = (PrivateKey) ks.getKey("rsassokey", appPwdAsArray);
        } catch (UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return key;
    }

    private PublicKey getPublicKey(File certFile){

        //read in the certificate from file
        FileInputStream certIn;
        PublicKey pk = null;
        try {
            certIn = new FileInputStream(certFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certIn);
            pk = cert.getPublicKey();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pk;
    }


    /**********************************************************************************************************************8
     * validates that password (appPwd) provides access to the keystore
     * @return
     * @throws IOException
     */
    public boolean validate()  {
        try {
            ks.load(new FileInputStream(KEYSTORE_NAME), appPwdAsArray);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * ONLY NEEDED WHILST TESTING IN ANDROID
     * @param groupID
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public  void addGroupKey(String groupID) throws IOException, GeneralSecurityException{
        //load keystore
        String appPwd = AppPwdObj.getInstance().getValue();

        //generate key
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        SecretKeySpec newSecretKeySpec =
                new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);

        //add to keystore
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();


    } //end generateKey

    public int getSize(){
        try {
            return ks.size();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    return  99;}


} //end AppKeystore