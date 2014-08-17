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

    public AppKeystore() throws MissingPwdException{
        String appPwd = AppPwdObj.getInstance().getValue();
        this.appPwdAsArray = appPwd.toCharArray();
        try {
            this.ks = loadKeyStore(appPwdAsArray);
        } catch (IOException e) {
            System.out.println( e.getMessage());
        } catch (GeneralSecurityException e) {
            System.out.println( e.getMessage());
        }

    }

    /**
     * Retrieves the key from the Keystore corresponding to the supplied alias and wraps it as SecretKeySpec
     * @param alias
     * @return KeySpec for the retrieved key
     */
    public SecretKeySpec getSKS(String alias){
        Key key = getKey(alias);
        return new SecretKeySpec(key.getEncoded(), KEY_ALGORITHM);
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
            System.out.println(key + "found");
        }
    } //end listGroups

    //**********************************************************************************************************************************************



    /****************************************************************************************************************
     * retrieves a key from the keystore
     * @param alias of the key to be retrieved
     */
    private Key getKey( String alias)  {
        try {
            if (ks.containsAlias(alias)){


                Key key = ks.getKey(alias, appPwdAsArray);
                System.out.println(key.getEncoded().length)   ;
                FileOutputStream temp = new FileOutputStream(new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/secretkeyforjulie"));
                temp.write(key.getEncoded());
                temp.close();

                return key;
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;  //ie not able to recover key
    } //end getKey






    /****************************************************************************************************************
     * Updates the stored copy of the keystore
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private void writeKeyStore() throws GeneralSecurityException, IOException {
        FileOutputStream fos = new FileOutputStream(KEYSTORE_NAME);
        try {
            ks.store(fos, appPwdAsArray);
        } finally {
            fos.close();
        }
    } //end writeKeyStore


    /**
     * Loads the key store from disc
     * @param pwd
     * @return the keystore
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadKeyStore(char[] pwd) throws IOException, GeneralSecurityException {
        KeyStore myks = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(KEYSTORE_NAME);
            myks.load(fis, pwd);
        } catch (FileNotFoundException e) {
            Log.d("KeyStore: ", "New keystore created");
            myks.load(null);
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
     * validates that password (appPwd) provides access to the keystore and that keystore is initialised with a keypair
     * @param appPwd
     * @return
     * @throws IOException
     */
    public boolean  validate(String appPwd, Context context) throws IOException {
        try {
            KeyStore ks = loadKeyStore(appPwd.toCharArray());
            ks.load(new FileInputStream(KEYSTORE_NAME),
                    appPwd.toCharArray());
            return true;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

} //end AppKeystore