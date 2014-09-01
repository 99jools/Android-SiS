package com.example.julie.securelyshare;

import android.content.Context;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;


public class AppKeystore {

    private static AppKeystore appKeystore = null;

    public static final String KEY_ALGORITHM = "AES";
    public static final String KEYPAIR_ALGORITHM = "RSA";
    public static final int KEY_LENGTH = 128;
    public static final int RSA_LENGTH = 2048;
    public static final String KEYSTORE_NAME = "SiSKeyStore.bks";
    public static final String CERTIFICATE_FILE = "SiSCert.bks";
    public static final String KEYSTORE_TYPE = "BKS";
    Context context;
    private char[] appPwdAsArray;
    private KeyStore ks;
    private KeyStore cs;



    public KeyStore getCs(){ return this.cs;}


    public static AppKeystore getInstance() throws MyKeystoreAccessException {
        if (appKeystore == null)
            return appKeystore = new AppKeystore();
        else
            return appKeystore;
    }

    /**
     * AppKeystore constructor
     *
     * @throws MyKeystoreAccessException if the password stored in AppPwdObj
     *                                   is null (as may have happened if object has been recreated)
     *                                   or if it is incorrect and doesn't unlock the keystore
     */
    private AppKeystore() throws MyKeystoreAccessException {
        String appPwd = AppPwdObj.getInstance().getValue();
        if (appPwd == null) throw new MyKeystoreAccessException();
        //otherwise continue to load the keystore
        this.appPwdAsArray = appPwd.toCharArray();
        this.context = AppPwdObj.getInstance().getContext();
        FileInputStream fis = null;
        FileInputStream fiscert = null;
        try {
            this.ks = KeyStore.getInstance("BKS");
            this.cs = KeyStore.getInstance("BKS");
            //fis = context.openFileInput(KEYSTORE_NAME);
            //moved to external storage for testing and demo
            File file = new File(context.getExternalFilesDir(null), KEYSTORE_NAME);
            fis = new FileInputStream(file);
            File filecert = new File(context.getExternalFilesDir(null), CERTIFICATE_FILE);
            fiscert = new FileInputStream(filecert);
            // need to handle exceptions from loading keystore separately as need to trap
            // IO Exception caused by password problems
            try {
                ks.load(fis, appPwdAsArray);
                cs.load(fiscert, appPwdAsArray);
            } catch (IOException e) {
                throw new MyKeystoreAccessException();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fiscert != null) try {
                fiscert.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end try catch finally
    } //end constructor

    /**
     * Retrieves the key from the Keystore corresponding to the supplied alias and wraps it as SecretKeySpec
     *
     * @param alias
     * @return KeySpec for the retrieved key
     */
    public SecretKeySpec getSKS(String alias) throws MyMissingKeyException {
        try {
            if (ks.containsAlias(alias)) {
                Key groupKey = ks.getKey(alias, appPwdAsArray);
                return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
            } else throw new MyMissingKeyException();
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
    public void importGroupKey(String groupID, FileInputStream fis) throws GeneralSecurityException, IOException {
        // get private key to decrypt key file
        PrivateKey privateKey = getMyPrivateKey();


//PrintPrivateKey.printPK(context, cs);



        //set up Cipher to decrypt groupKeyFile
        Cipher deCipher = Cipher.getInstance(KEYPAIR_ALGORITHM);
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);

        //read and decrypt encoded group key from file
        byte[] groupKey = new byte[KEY_LENGTH / 8];   //need to convert to bytes

//PrintPrivateKey.printEncrypted(context, fis);




        CipherInputStream cis = new CipherInputStream(fis, deCipher);
        cis.read(groupKey);



        //add to key store
        SecretKeySpec sks = new SecretKeySpec(groupKey, KEY_ALGORITHM);
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

 PrintPrivateKey.printSymK(context, groupKey);

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();

    }

    public String[] getGroups() throws MyKeystoreAccessException, GeneralSecurityException, IOException {
        Enumeration<String> es = ks.aliases();
        String[] groups = new String[ks.size()];
        int index = 0;
        for (String key : Collections.list(es)) {
            groups[index++] = key;
        }
        Arrays.sort(groups);
        return groups;
    } //end listGroups

//**********************************************************************************************************************************************


    /**
     * *************************************************************************************************************
     * Updates the stored copy of the keystore
     *
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private void writeKeyStore() throws GeneralSecurityException, IOException {
        //      FileOutputStream fos = context.openFileOutput(KEYSTORE_NAME, Context.MODE_PRIVATE);
        FileOutputStream fos = new FileOutputStream(new File(context.getExternalFilesDir(null), KEYSTORE_NAME));

        try {
            ks.store(fos, appPwdAsArray);
        } finally {
            fos.close();
        }
    } //end writeKeyStore

    private PrivateKey getMyPrivateKey() {
        PrivateKey key = null;
        try {
            key = (PrivateKey) cs.getKey("rsassokey", appPwdAsArray);


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

    //TODO check whether this can be removed
    private PublicKey getPublicKey(File certFile) {
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

    /**
     * ONLY NEEDED WHILST TESTING IN ANDROID
     *
     * @param groupID
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void addGroupKey(String groupID) throws IOException, GeneralSecurityException {
        //generate key
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        SecretKeySpec newSecretKeySpec =
                new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);

        //add to keystore
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

        //TODO REMOVE AFTER TESTING
        writeSKS(newSecretKeySpec);

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();
    } //end generateKey


    public void deleteGroupKey(String groupID) throws IOException, GeneralSecurityException {
        //delete from keystore
        ks.deleteEntry(groupID);
        //update stored copy of keystore  (
        writeKeyStore();
    } //end delete key


    public void writeSKS(SecretKeySpec sks) {
        byte[] mykey = sks.getEncoded();
        File mFile = new File(context.getExternalFilesDir(null), "julie.xeb");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mFile);
            fos.write(mykey);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testImport(String groupID, byte[] groupKey) {

        //add to key store
        SecretKeySpec sks = new SecretKeySpec(groupKey, KEY_ALGORITHM);
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        try {
            ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));
            writeKeyStore();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)


    }


} //end AppKeystore