package com.example.jrs300.shareinsecrettest;


import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 01/08/14.
 */

public class AppKeystore {

    public static final String KEY_ALGORITHM = "AES";
    public static final int KEY_LENGTH = 128;
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";

    private AppKeystore(){
        //dummy constructor to prevent instantiation
    }

    /**
     * recovers stored key from Keystore and wraps as SecretKeySpec
     * @param groupID
     * @return
     * @throws MissingPwdException
     */
    public static SecretKeySpec getKeySpec( String groupID)
            {
        KeyStore ks;
                String appPwd = null;
                try {
                    appPwd = AppPwdObj.getInstance().getValue();
                } catch (MissingPwdException e) {
                    Log.e("getpwd", e.getMessage());
                }
                Context context = AppPwdObj.getInstance().getContext();
        try {
            //load the keystore
            ks = loadKeyStore(appPwd.toCharArray(), context);

            /*recover the key entry for the group and package as a SecretKeySpec
             * - errors relating to inability to recover a key are thrown for calling class to handle */
            if (ks.containsAlias(groupID)){
                Log.e("getkeyspec", groupID + "alias found ");
                Key groupKey = ks.getKey(groupID, appPwd.toCharArray());
                Log.e("gotkey", groupKey.getEncoded().toString());
                return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
            }
        } catch (IOException e) {
            Log.e("getKeySpec",e.getMessage());
        } catch (GeneralSecurityException e) {
            Log.e("getKeySpec", e.getMessage());
        }
        return null;  //ie not able to recover key
    } //end getExistingKey


    /**
     * Ggenerates a new group encryption key for AES encryption using 256 bit key and stores in KeyStore
     * Since this needs access to the apps internal storage, it needs to be passed a Context
     * @param groupID
     * @param prefs
     * @return
     * @throws MissingPwdException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static boolean addGroupKey(String groupID, SharedPrefs prefs)
            throws MissingPwdException,IOException, GeneralSecurityException{

        if (prefs.groupExists(groupID)) return false;

        //otherwise add new group
        String appPwd = AppPwdObj.getInstance().getValue();
        Context context = AppPwdObj.getInstance().getContext();
        KeyStore ks = loadKeyStore(appPwd.toCharArray(), context);

        //generate key
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        SecretKeySpec newSecretKeySpec =
                new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);

        //add to keystore
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));
 ks.setEntry("groupZ", skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));
        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore(ks, appPwd.toCharArray(), context);

        //add new group to shared preferences
        prefs.addGroup(groupID);
 prefs.addGroup("groupZ");
        return true;

    } //end addGroupKey

    public static boolean  validate(String appPwd, Context context) throws IOException{
        try {
            KeyStore ks = loadKeyStore(appPwd.toCharArray(), context);
            return true;

        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    public static void listGroups() throws MissingPwdException, GeneralSecurityException, IOException {
        KeyStore ks;
        String appPwd = AppPwdObj.getInstance().getValue();
        Context context = AppPwdObj.getInstance().getContext();
        ks = loadKeyStore(appPwd.toCharArray(), context);
        Enumeration<String> es = ks.aliases();
        for (String key : Collections.list(es)){
            Log.e("enum", key + "found");
        }}


    /**
     * Loads the key store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadKeyStore(char[] pwd, Context context) throws IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance("BKS");
        FileInputStream fis = null;
        try {

            fis = context.openFileInput(KEYSTORE_NAME);
            //    fis = new FileInputStream(KEYSTORE_NAME);
            ks.load(fis, pwd);
        } catch (FileNotFoundException e) {
            Log.e("keystore","New keystore created");
            //this should only get run if file store doesn't exists at all - creates a new one
            ks.load(null);


        } finally {
            if (fis != null) fis.close();
        }
        return ks;
    } //end loadKeyStore

    /**
     * Updates the stored copy of the keystore - needs to be given a context in order to get access to the Internal Storage
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void writeKeyStore(KeyStore ks, char[] pwd, Context context) throws GeneralSecurityException, IOException {


        FileOutputStream fos = context.openFileOutput(KEYSTORE_NAME, Context.MODE_PRIVATE);
        Log.e("fos", fos.getFD().toString());
        try {
            ks.store(fos, pwd);
        } finally {
            fos.close();
        }
    } //end writeKeyStore



} //end AppKeystore